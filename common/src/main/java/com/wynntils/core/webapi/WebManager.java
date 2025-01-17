/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.core.webapi;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.managers.CoreManager;
import com.wynntils.core.webapi.account.WynntilsAccount;
import com.wynntils.core.webapi.profiles.ItemGuessProfile;
import com.wynntils.core.webapi.profiles.PlayerStatsProfile;
import com.wynntils.core.webapi.profiles.TerritoryProfile;
import com.wynntils.core.webapi.profiles.item.IdentificationProfile;
import com.wynntils.core.webapi.profiles.item.ItemProfile;
import com.wynntils.core.webapi.profiles.item.ItemType;
import com.wynntils.core.webapi.profiles.item.MajorIdentification;
import com.wynntils.core.webapi.request.RequestBuilder;
import com.wynntils.core.webapi.request.RequestHandler;
import com.wynntils.mc.event.WebSetupEvent;
import com.wynntils.mc.utils.ComponentUtils;
import com.wynntils.mc.utils.McUtils;
import com.wynntils.utils.Utils;
import com.wynntils.wynn.event.QuestBookReloadedEvent;
import com.wynntils.wynn.item.IdentificationOrderer;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

/** Provides and loads web content on demand */
public final class WebManager extends CoreManager {
    public static final File API_CACHE_ROOT = WynntilsMod.getModStorageDir("apicache");
    private static final int REQUEST_TIMEOUT_MILLIS = 10000;

    private static boolean setup = false;
    private static final RequestHandler handler = new RequestHandler();

    private static WebReader apiUrls = null;

    private static final Gson gson = new Gson();

    private static HashMap<String, ItemProfile> items = new HashMap<>();
    private static Collection<ItemProfile> directItems = new ArrayList<>();
    private static HashMap<String, ItemGuessProfile> itemGuesses = new HashMap<>();
    private static HashMap<String, String> translatedReferences = new HashMap<>();
    private static HashMap<String, String> internalIdentifications = new HashMap<>();
    private static HashMap<String, MajorIdentification> majorIds = new HashMap<>();
    private static HashMap<ItemType, String[]> materialTypes = new HashMap<>();

    private static String currentSplash = "";

    private static TerritoryUpdateThread territoryUpdateThread;
    private static final HashMap<String, TerritoryProfile> territories = new HashMap<>();

    private static FriendUpdateThread friendUpdateThread;

    private static WynntilsAccount account = null;

    private static final String USER_AGENT = String.format(
            "Wynntils Artemis\\%s-%d (%s) %s",
            WynntilsMod.getVersion(),
            WynntilsMod.getBuildNumber(),
            WynntilsMod.isDevelopmentEnvironment() ? "dev" : "client",
            WynntilsMod.getModLoader());

    public static void init() {
        tryReloadApiUrls(false);
        setupUserAccount();

        WebManager.updateCurrentSplash();

        loadCommonObjects();
        updateFriendThreadStatus(true);
    }

    private static void loadCommonObjects() {
        WebManager.tryLoadItemList();
        WebManager.tryLoadItemGuesses();
    }

    public static boolean isLoggedIn() {
        return (account != null && account.isConnected());
    }

    public static void reset() {
        // tryReloadApiUrls
        apiUrls = null;

        // tryLoadItemGuesses
        itemGuesses = null;

        // tryLoadItemList
        items = null;
        directItems = null;
        translatedReferences = null;
        internalIdentifications = null;
        majorIds = null;
        materialTypes = null;

        // tryLoadTerritories
        territories.clear();

        updateTerritoryThreadStatus(false);
        updateFriendThreadStatus(false);
    }

    private static void setupUserAccount() {
        if (isLoggedIn()) return;

        account = new WynntilsAccount();
        boolean accountSetup = account.login();

        if (!accountSetup) {
            MutableComponent failed = new TextComponent(
                            "Welps! Trying to connect and set up the Wynntils Account with your data has failed. "
                                    + "Most notably, cloud config syncing will not work. To try this action again, run ")
                    .withStyle(ChatFormatting.GREEN);
            failed.append(new TextComponent("/wynntils reload")
                    .withStyle(Style.EMPTY
                            .withColor(ChatFormatting.AQUA)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wynntils reload"))));

            if (McUtils.player() == null) {
                WynntilsMod.error(ComponentUtils.getUnformatted(failed));
                return;
            }

            McUtils.sendMessageToClient(failed);
        }
    }

    public static boolean tryLoadTerritories() {
        return tryLoadTerritories(handler);
    }

    public static boolean tryLoadTerritories(RequestHandler handler) {
        if (apiUrls == null || !apiUrls.hasKey("Athena")) return false;
        String url = apiUrls.get("Athena") + "/cache/get/territoryList";
        handler.addAndDispatch(new RequestBuilder(url, "territory")
                .cacheTo(new File(API_CACHE_ROOT, "territories.json"))
                .handleJsonObject(json -> {
                    if (!json.has("territories")) return false;

                    Type type = new TypeToken<HashMap<String, TerritoryProfile>>() {}.getType();

                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeHierarchyAdapter(
                            TerritoryProfile.class, new TerritoryProfile.TerritoryDeserializer());
                    Gson gson = builder.create();

                    territories.clear();
                    territories.putAll(gson.fromJson(json.get("territories"), type));
                    return true;
                })
                .build());

        return isTerritoryListLoaded();
    }

    private static void updateTerritoryThreadStatus(boolean start) {
        if (start) {
            if (territoryUpdateThread == null) {
                territoryUpdateThread = new TerritoryUpdateThread("Territory Update Thread");
                territoryUpdateThread.start();
                return;
            }
            return;
        }

        if (territoryUpdateThread != null) {
            territoryUpdateThread.interrupt();
        }
        territoryUpdateThread = null;
    }

    private static void updateFriendThreadStatus(boolean start) {
        if (start) {
            if (friendUpdateThread == null) {
                friendUpdateThread = new FriendUpdateThread("Friend Update Thread");
                WynntilsMod.registerEventListener(friendUpdateThread);
                friendUpdateThread.start();
                return;
            }
            return;
        }

        if (friendUpdateThread != null) {
            friendUpdateThread.interrupt();
            WynntilsMod.unregisterEventListener(friendUpdateThread);
        }
        friendUpdateThread = null;
    }

    // uuid to player
    private static final Map<String, PlayerStatsProfile> onlineFriends = new HashMap<>();

    // queue of usernames
    private static final Queue<String> playerQueue = new LinkedList<>();

    private static final Map<String, PlayerStatsProfile> friends = new HashMap<>();

    // Loads current friend in friend queue
    public static boolean tryLoadFriends(RequestHandler handler) {
        if (apiUrls == null || !apiUrls.hasKey("PlayerStatsv2")) return false;

        // add to online friends in dispatch and friend join
        // remove from online friends in dispatch if offline
        for (PlayerStatsProfile player : onlineFriends.values()) {
            if (!playerQueue.contains(player.getUsername())) {
                playerQueue.add(player.getUsername());
            }
        }

        String currentFriend = playerQueue.poll();
        if (currentFriend == null) {
            return true;
        }
        String url = apiUrls.get("PlayerStatsv2") + currentFriend + "/stats";
        handler.addAndDispatch(new RequestBuilder(url, "friend-" + currentFriend)
                .cacheTo(new File(API_CACHE_ROOT, "friends/" + currentFriend + ".json"))
                .handleJsonObject(json -> {
                    if (json.get("code").getAsInt() != 200) return false;

                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeHierarchyAdapter(
                            PlayerStatsProfile.class, new PlayerStatsProfile.PlayerStatsProfileDeserializer());
                    Gson lGson = builder.create();

                    PlayerStatsProfile player;
                    try {
                        player = lGson.fromJson(json, PlayerStatsProfile.class);
                    } catch (JsonSyntaxException e) {
                        playerQueue.add(currentFriend);
                        return false;
                    }

                    if (player.isOnline()) {
                        onlineFriends.put(player.getUuid(), player);
                    } else {
                        onlineFriends.remove(player.getUuid());
                    }

                    friends.put(player.getUuid(), player);

                    WynntilsMod.info("Updated " + player.getUsername());
                    return true;
                })
                .build());

        WynntilsMod.postEvent(new QuestBookReloadedEvent.FriendStatsReloaded());

        return isTerritoryListLoaded();
    }

    public static void addFriend(String fName) {
        playerQueue.add(fName);
    }

    private static void tryLoadItemGuesses() {
        if (apiUrls == null || !apiUrls.hasKey("ItemGuesses")) return;
        handler.addAndDispatch(new RequestBuilder(apiUrls.get("ItemGuesses"), "item_guesses")
                .cacheTo(new File(API_CACHE_ROOT, "item_guesses.json"))
                .handleJsonObject(json -> {
                    Type type = new TypeToken<HashMap<String, ItemGuessProfile>>() {}.getType();

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeHierarchyAdapter(
                            HashMap.class, new ItemGuessProfile.ItemGuessDeserializer());
                    Gson gson = gsonBuilder.create();

                    itemGuesses = new HashMap<>();
                    itemGuesses.putAll(gson.fromJson(json, type));

                    return true;
                })
                .useCacheAsBackup()
                .build());

        // Check for success
    }

    private static void tryLoadItemList() {
        if (apiUrls == null || !apiUrls.hasKey("Athena")) return;
        handler.addAndDispatch(new RequestBuilder(apiUrls.get("Athena") + "/cache/get/itemList", "item_list")
                .cacheTo(new File(API_CACHE_ROOT, "item_list.json"))
                .handleJsonObject(json -> {
                    Type hashmapType = new TypeToken<HashMap<String, String>>() {}.getType();
                    translatedReferences = gson.fromJson(json.getAsJsonObject("translatedReferences"), hashmapType);
                    internalIdentifications =
                            gson.fromJson(json.getAsJsonObject("internalIdentifications"), hashmapType);

                    Type majorIdsType = new TypeToken<HashMap<String, MajorIdentification>>() {}.getType();
                    majorIds = gson.fromJson(json.getAsJsonObject("majorIdentifications"), majorIdsType);
                    Type materialTypesType = new TypeToken<HashMap<ItemType, String[]>>() {}.getType();
                    materialTypes = gson.fromJson(json.getAsJsonObject("materialTypes"), materialTypesType);

                    // FIXME: We should not be doing Singleton housekeeping for IdentificationOrderer!
                    IdentificationOrderer.INSTANCE =
                            gson.fromJson(json.getAsJsonObject("identificationOrder"), IdentificationOrderer.class);

                    ItemProfile[] gItems = gson.fromJson(json.getAsJsonArray("items"), ItemProfile[].class);

                    HashMap<String, ItemProfile> citems = new HashMap<>();
                    for (ItemProfile prof : gItems) {
                        prof.getStatuses().values().forEach(IdentificationProfile::calculateMinMax);
                        prof.addMajorIds(majorIds);
                        citems.put(prof.getDisplayName(), prof);
                    }

                    citems.values().forEach(ItemProfile::registerIdTypes);

                    directItems = citems.values();
                    items = citems;

                    return true;
                })
                .useCacheAsBackup()
                .build());

        // Check for success
    }

    private static void tryReloadApiUrls(boolean async) {
        handler.addRequest(new RequestBuilder("https://api.wynntils.com/webapi", "webapi")
                .cacheTo(new File(API_CACHE_ROOT, "webapi.txt"))
                .useCacheAsBackup()
                .handleWebReader(reader -> {
                    apiUrls = reader;
                    if (!setup) {
                        setup = true;
                    }

                    WynntilsMod.postEvent(new WebSetupEvent());
                    return true;
                })
                .build());

        handler.dispatch(async);
    }

    /**
     * Request all online players to WynnAPI
     *
     * @return a {@link HashMap} who the key is the server and the value is an array containing all
     *     players on it
     * @throws IOException thrown by URLConnection
     */
    public static Map<String, List<String>> getOnlinePlayers() throws IOException {
        if (apiUrls == null || !apiUrls.hasKey("OnlinePlayers")) return new HashMap<>();

        URLConnection st = generateURLRequest(apiUrls.get("OnlinePlayers"));
        InputStreamReader stInputReader = new InputStreamReader(st.getInputStream(), StandardCharsets.UTF_8);
        JsonObject main = JsonParser.parseReader(stInputReader).getAsJsonObject();

        if (!main.has("message")) {
            main.remove("request");

            Type type = new TypeToken<LinkedHashMap<String, ArrayList<String>>>() {}.getType();

            return gson.fromJson(main, type);
        } else {
            return new HashMap<>();
        }
    }

    private static void updateCurrentSplash() {
        if (apiUrls == null || apiUrls.getList("Splashes") == null) return;

        List<String> splashes = apiUrls.getList("Splashes");
        currentSplash = splashes.get(Utils.getRandom().nextInt(splashes.size()));
    }

    private static URLConnection generateURLRequest(String url) throws IOException {
        URLConnection st = new URL(url).openConnection();
        st.setRequestProperty("User-Agent", USER_AGENT);
        if (apiUrls != null && apiUrls.hasKey("WynnApiKey")) st.setRequestProperty("apikey", apiUrls.get("WynnApiKey"));
        st.setConnectTimeout(REQUEST_TIMEOUT_MILLIS);
        st.setReadTimeout(REQUEST_TIMEOUT_MILLIS);

        return st;
    }

    public static String getApiUrl(String key) {
        if (apiUrls == null) return null;

        return apiUrls.get(key);
    }

    public static boolean isTerritoryListLoaded() {
        return !territories.isEmpty();
    }

    public static HashMap<String, ItemGuessProfile> getItemGuesses() {
        return itemGuesses;
    }

    public static Collection<ItemProfile> getItemsCollection() {
        return directItems;
    }

    public static HashMap<String, ItemProfile> getItemsMap() {
        return items;
    }

    public static HashMap<ItemType, String[]> getMaterialTypes() {
        return materialTypes;
    }

    public static HashMap<String, MajorIdentification> getMajorIds() {
        return majorIds;
    }

    public static HashMap<String, String> getInternalIdentifications() {
        return internalIdentifications;
    }

    public static HashMap<String, String> getTranslatedReferences() {
        return translatedReferences;
    }

    public static HashMap<String, TerritoryProfile> getTerritories() {
        return territories;
    }

    public static String getUserAgent() {
        return USER_AGENT;
    }

    public static boolean isSetup() {
        return setup;
    }

    public static String getCurrentSplash() {
        return currentSplash;
    }

    public static Optional<WebReader> getApiUrls() {
        return Optional.ofNullable(apiUrls);
    }

    public static Optional<WynntilsAccount> getAccount() {
        return Optional.ofNullable(account);
    }

    public static RequestHandler getHandler() {
        return handler;
    }

    public static List<PlayerStatsProfile> getFriendStats() {
        return new ArrayList<>(friends.values());
    }
}
