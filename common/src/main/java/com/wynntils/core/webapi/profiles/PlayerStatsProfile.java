/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.core.webapi.profiles;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.wynntils.core.WynntilsMod;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class PlayerStatsProfile {
    private static final SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private final String username;
    private final UUID uuid;
    // not player rank but game rank (ie player, mod, admin)
    private final PlayerRank rank;
    private final Date firstJoin;
    private final Date lastJoin;
    private final boolean online;
    private final String server;
    private final int playtime;
    private final boolean displayTag;
    private final PlayerTag tag;
    private final boolean veteran;
    private final ArrayList<PlayerClassProfile> classes;
    private final String guildName;
    private final GuildRank guildRank;
    private final int chestsFound;
    private final long blocksWalked;
    private final int itemsIdentified;
    private final int mobsKilled;
    private final int totalCombatLevel;
    private final int totalProfessionLevel;
    private final int totalLevel;
    private final int pvpKills;
    private final int pvpDeaths;
    private final int logins;
    private final int deaths;
    private final int discoveries;
    private final int eventsWon;
    private final int guildRanking;
    private final int combatLevelRanking;
    private final int woodcuttingLevelRanking;
    private final int miningLevelRanking;
    private final int fishingLevelRanking;
    private final int farmingLevelRanking;
    private final int alchemismLevelRanking;
    private final int armouringLevelRanking;
    private final int cookingLevelRanking;
    private final int jewelingLevelRanking;
    private final int scribingLevelRanking;
    private final int tailoringLevelRanking;
    private final int weaponsmithingLevelRanking;
    private final int woodworkingLevelRanking;
    private final int professionLevelRanking;
    private final int overallLevelRanking;
    private final int overallAllRanking;
    private final int combatRanking;
    private final int professionRanking;
    private final int pvpRanking;

    public PlayerStatsProfile(
            String username,
            String uuid,
            PlayerRank rank,
            Date firstJoin,
            Date lastJoin,
            boolean online,
            String server,
            int playtime,
            boolean displayTag,
            PlayerTag tag,
            boolean veteran,
            ArrayList<PlayerClassProfile> classes,
            String guildName,
            GuildRank guildRank,
            int chestsFound,
            long blocksWalked,
            int itemsIdentified,
            int mobsKilled,
            int totalCombatLevel,
            int totalProfessionLevel,
            int totalLevel,
            int pvpKills,
            int pvpDeaths,
            int logins,
            int deaths,
            int discoveries,
            int eventsWon,
            int guildRanking,
            int combatLevelRanking,
            int woodcuttingLevelRanking,
            int miningLevelRanking,
            int fishingLevelRanking,
            int farmingLevelRanking,
            int alchemismLevelRanking,
            int armouringLevelRanking,
            int cookingLevelRanking,
            int jewelingLevelRanking,
            int scribingLevelRanking,
            int tailoringLevelRanking,
            int weaponsmithingLevelRanking,
            int woodworkingLevelRanking,
            int professionLevelRanking,
            int overallLevelRanking,
            int overallAllRanking,
            int combatRanking,
            int professionRanking,
            int pvpRanking) {
        this.username = username;
        this.uuid = UUID.fromString(uuid);
        this.rank = rank;
        this.firstJoin = firstJoin;
        this.lastJoin = lastJoin;
        this.online = online;
        this.server = server;
        this.playtime = playtime;
        this.displayTag = displayTag;
        this.tag = tag;
        this.veteran = veteran;
        this.classes = classes;
        this.guildName = guildName;
        this.guildRank = guildRank;
        this.chestsFound = chestsFound;
        this.blocksWalked = blocksWalked;
        this.itemsIdentified = itemsIdentified;
        this.mobsKilled = mobsKilled;
        this.totalCombatLevel = totalCombatLevel;
        this.totalProfessionLevel = totalProfessionLevel;
        this.totalLevel = totalLevel;
        this.pvpKills = pvpKills;
        this.pvpDeaths = pvpDeaths;
        this.logins = logins;
        this.deaths = deaths;
        this.discoveries = discoveries;
        this.eventsWon = eventsWon;
        this.guildRanking = guildRanking;
        this.combatLevelRanking = combatLevelRanking;
        this.woodcuttingLevelRanking = woodcuttingLevelRanking;
        this.miningLevelRanking = miningLevelRanking;
        this.fishingLevelRanking = fishingLevelRanking;
        this.farmingLevelRanking = farmingLevelRanking;
        this.alchemismLevelRanking = alchemismLevelRanking;
        this.armouringLevelRanking = armouringLevelRanking;
        this.cookingLevelRanking = cookingLevelRanking;
        this.jewelingLevelRanking = jewelingLevelRanking;
        this.scribingLevelRanking = scribingLevelRanking;
        this.tailoringLevelRanking = tailoringLevelRanking;
        this.weaponsmithingLevelRanking = weaponsmithingLevelRanking;
        this.woodworkingLevelRanking = woodworkingLevelRanking;
        this.professionLevelRanking = professionLevelRanking;
        this.overallLevelRanking = overallLevelRanking;
        this.overallAllRanking = overallAllRanking;
        this.combatRanking = combatRanking;
        this.professionRanking = professionRanking;
        this.pvpRanking = pvpRanking;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PlayerRank getRank() {
        return rank;
    }

    public Date getFirstJoin() {
        return firstJoin;
    }

    public Date getLastJoin() {
        return lastJoin;
    }

    public boolean isOnline() {
        return online;
    }

    public String getServer() {
        return server;
    }

    public int getPlaytime() {
        return playtime;
    }

    public boolean isDisplayTag() {
        return displayTag;
    }

    public PlayerTag getTag() {
        return tag;
    }

    public boolean isVeteran() {
        return veteran;
    }

    public ArrayList<PlayerClassProfile> getClasses() {
        return classes;
    }

    public String getGuildName() {
        return guildName;
    }

    public GuildRank getGuildRank() {
        return guildRank;
    }

    public int getChestsFound() {
        return chestsFound;
    }

    public long getBlocksWalked() {
        return blocksWalked;
    }

    public int getItemsIdentified() {
        return itemsIdentified;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public int getTotalCombatLevel() {
        return totalCombatLevel;
    }

    public int getTotalProfessionLevel() {
        return totalProfessionLevel;
    }

    public int getTotalLevel() {
        return totalLevel;
    }

    public int getPvpKills() {
        return pvpKills;
    }

    public int getPvpDeaths() {
        return pvpDeaths;
    }

    public int getLogins() {
        return logins;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getDiscoveries() {
        return discoveries;
    }

    public int getEventsWon() {
        return eventsWon;
    }

    public int getGuildRanking() {
        return guildRanking;
    }

    public int getCombatLevelRanking() {
        return combatLevelRanking;
    }

    public int getWoodcuttingLevelRanking() {
        return woodcuttingLevelRanking;
    }

    public int getMiningLevelRanking() {
        return miningLevelRanking;
    }

    public int getFishingLevelRanking() {
        return fishingLevelRanking;
    }

    public int getFarmingLevelRanking() {
        return farmingLevelRanking;
    }

    public int getAlchemismLevelRanking() {
        return alchemismLevelRanking;
    }

    public int getArmouringLevelRanking() {
        return armouringLevelRanking;
    }

    public int getCookingLevelRanking() {
        return cookingLevelRanking;
    }

    public int getJewelingLevelRanking() {
        return jewelingLevelRanking;
    }

    public int getScribingLevelRanking() {
        return scribingLevelRanking;
    }

    public int getTailoringLevelRanking() {
        return tailoringLevelRanking;
    }

    public int getWeaponsmithingLevelRanking() {
        return weaponsmithingLevelRanking;
    }

    public int getWoodworkingLevelRanking() {
        return woodworkingLevelRanking;
    }

    public int getProfessionLevelRanking() {
        return professionLevelRanking;
    }

    public int getOverallLevelRanking() {
        return overallLevelRanking;
    }

    public int getOverallAllRanking() {
        return overallAllRanking;
    }

    public int getCombatRanking() {
        return combatRanking;
    }

    public int getProfessionRanking() {
        return professionRanking;
    }

    public int getPvpRanking() {
        return pvpRanking;
    }

    public enum PlayerRank {
        Player,
        Moderator,
        Administrator,
        Music,
        Game_Master,
        Item,
        Builder,
        Hybrid,
        CMD,
        Media
    }

    public enum PlayerTag {
        NONE("Basic"),
        VIP("VIP"),
        VIPPLUS("VIP+"),
        HERO("HERO"),
        CHAMPION("CHAMPION");

        @Override
        public String toString() {
            return name;
        }

        private final String name;

        PlayerTag(String name) {
            this.name = name;
        }

        public boolean isVip() {
            return this.ordinal() >= 1;
        }

        public boolean isVipPlus() {
            return this.ordinal() >= 2;
        }
    }

    public enum GuildRank {
        OWNER("★★★★★"),
        CHIEF("★★★★"),
        STRATEGIST("★★★"),
        CAPTAIN("★★"),
        RECRUITER("★"),
        RECRUIT(""),
        NONE("");

        private final String stars;

        GuildRank(String stars) {
            this.stars = stars;
        }

        public String getStars() {
            return stars;
        }

        public static String getRoleNameFromStars(int starCount) {
            switch (starCount) {
                case 5:
                    return "Owner";
                case 4:
                    return "Chief";
                case 3:
                    return "Strategist";
                case 2:
                    return "Captain";
                case 1:
                    return "Recruiter";
                case 0:
                    return "Recruit";
                default:
                    return "";
            }
        }
    }

    public static class PlayerStatsProfileDeserializer implements JsonDeserializer<PlayerStatsProfile> {

        @Override
        public PlayerStatsProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject playerProfile =
                    json.getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject();
            String username = playerProfile.get("username").getAsString();
            String uuid = playerProfile.get("uuid").getAsString().replace("-", "");
            PlayerRank rank =
                    PlayerRank.valueOf(playerProfile.get("rank").getAsString().replace(" ", "_"));

            JsonObject playerMeta = playerProfile.get("meta").getAsJsonObject();
            Date firstJoin;
            Date lastJoin;
            try {
                firstJoin = API_DATE_FORMAT.parse(playerMeta.get("firstJoin").getAsString());
                lastJoin = API_DATE_FORMAT.parse(playerMeta.get("lastJoin").getAsString());
            } catch (ParseException ex) {
                WynntilsMod.warn("Unable to parse join dates from Wynncraft's API", ex);
                firstJoin = new Date();
                lastJoin = new Date();
            }
            JsonObject playerLocation = playerMeta.get("location").getAsJsonObject();
            boolean online = playerLocation.get("online").getAsBoolean();
            String server = null;
            if (!playerLocation.get("server").isJsonNull())
                server = playerLocation.get("server").getAsString();
            int playtime = playerMeta.get("playtime").getAsInt();
            JsonObject playerTag = playerMeta.get("tag").getAsJsonObject();
            boolean displayTag = playerTag.get("display").getAsBoolean();
            PlayerTag tag;
            if (playerTag.get("value").isJsonNull()) tag = PlayerTag.NONE;
            else tag = PlayerTag.valueOf(playerTag.get("value").getAsString().replace("+", "PLUS"));
            boolean veteran = playerMeta.get("veteran").getAsBoolean();

            ArrayList<PlayerClassProfile> classes = new ArrayList<>();
            JsonArray playerClasses = playerProfile.get("classes").getAsJsonArray();
            Type playerClassType = new TypeToken<PlayerClassProfile>() {}.getType();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(playerClassType, new PlayerClassProfile.PlayerClassProfileDeserializer());
            Gson gson = gsonBuilder.create();

            for (JsonElement playerClass : playerClasses) classes.add(gson.fromJson(playerClass, playerClassType));

            JsonObject playerGuild = playerProfile.get("guild").getAsJsonObject();
            String guildName = "";
            GuildRank guildRank = GuildRank.NONE;
            if (!playerGuild.get("name").isJsonNull()) {
                guildName = playerGuild.get("name").getAsString();
                if (playerGuild.get("rank").getAsString().isEmpty()) guildRank = GuildRank.NONE;
                else
                    guildRank = GuildRank.valueOf(
                            playerGuild.get("rank").getAsString().toUpperCase(Locale.ROOT));
            }

            JsonObject globalStats = playerProfile.get("global").getAsJsonObject();
            // Chest stats in API are gone, set to 0 for now
            // int chestsFound = globalStats.get("chestsFound").getAsInt();
            int chestsFound = 0;
            long blocksWalked = globalStats.get("blocksWalked").getAsLong();
            int itemsIdentified = globalStats.get("itemsIdentified").getAsInt();
            int mobsKilled = globalStats.get("mobsKilled").getAsInt();
            JsonObject totalLevels = globalStats.get("totalLevel").getAsJsonObject();
            int totalCombatLevel = totalLevels.get("combat").getAsInt();
            int totalProfessionLevel = totalLevels.get("profession").getAsInt();
            int totalLevel = totalLevels.get("combined").getAsInt();
            JsonObject pvpStats = globalStats.get("pvp").getAsJsonObject();
            int pvpKills = pvpStats.get("kills").getAsInt();
            int pvpDeaths = pvpStats.get("deaths").getAsInt();
            int logins = globalStats.get("logins").getAsInt();
            int deaths = globalStats.get("deaths").getAsInt();
            int discoveries = globalStats.get("discoveries").getAsInt();
            int eventsWon = globalStats.get("eventsWon").getAsInt();

            JsonObject rankings = playerProfile.get("ranking").getAsJsonObject();
            int guildRanking = 0;
            if (!rankings.get("guild").isJsonNull())
                guildRanking = rankings.get("guild").getAsInt();
            JsonObject playerRankings = rankings.get("player").getAsJsonObject();
            JsonObject soloRankings = playerRankings.get("solo").getAsJsonObject();
            int combatLevelRanking = 0;
            if (!soloRankings.get("combat").isJsonNull())
                combatLevelRanking = soloRankings.get("combat").getAsInt();
            int woodcuttingLevelRanking = 0;
            if (!soloRankings.get("woodcutting").isJsonNull())
                woodcuttingLevelRanking = soloRankings.get("woodcutting").getAsInt();
            int miningLevelRanking = 0;
            if (!soloRankings.get("mining").isJsonNull())
                miningLevelRanking = soloRankings.get("mining").getAsInt();
            int fishingLevelRanking = 0;
            if (!soloRankings.get("fishing").isJsonNull())
                fishingLevelRanking = soloRankings.get("fishing").getAsInt();
            int farmingLevelRanking = 0;
            if (!soloRankings.get("farming").isJsonNull())
                farmingLevelRanking = soloRankings.get("farming").getAsInt();
            int alchemismLevelRanking = 0;
            if (!soloRankings.get("alchemism").isJsonNull())
                alchemismLevelRanking = soloRankings.get("alchemism").getAsInt();
            int armouringLevelRanking = 0;
            if (!soloRankings.get("armouring").isJsonNull())
                armouringLevelRanking = soloRankings.get("armouring").getAsInt();
            int cookingLevelRanking = 0;
            if (!soloRankings.get("cooking").isJsonNull())
                cookingLevelRanking = soloRankings.get("cooking").getAsInt();
            int jewelingLevelRanking = 0;
            if (!soloRankings.get("jeweling").isJsonNull())
                jewelingLevelRanking = soloRankings.get("jeweling").getAsInt();
            int scribingLevelRanking = 0;
            if (!soloRankings.get("scribing").isJsonNull())
                scribingLevelRanking = soloRankings.get("scribing").getAsInt();
            int tailoringLevelRanking = 0;
            if (!soloRankings.get("tailoring").isJsonNull())
                tailoringLevelRanking = soloRankings.get("tailoring").getAsInt();
            int weaponsmithingLevelRanking = 0;
            if (!soloRankings.get("weaponsmithing").isJsonNull())
                weaponsmithingLevelRanking = soloRankings.get("weaponsmithing").getAsInt();
            int woodworkingLevelRanking = 0;
            if (!soloRankings.get("woodworking").isJsonNull())
                woodworkingLevelRanking = soloRankings.get("woodworking").getAsInt();
            int professionLevelRanking = 0;
            if (!soloRankings.get("profession").isJsonNull())
                professionLevelRanking = soloRankings.get("profession").getAsInt();
            int overallLevelRanking = 0;
            if (!soloRankings.get("overall").isJsonNull())
                overallLevelRanking = soloRankings.get("overall").getAsInt();
            JsonObject overallRankings = playerRankings.get("overall").getAsJsonObject();
            int overallAllRanking = 0;
            if (!overallRankings.get("all").isJsonNull())
                overallAllRanking = overallRankings.get("all").getAsInt();
            int combatRanking = 0;
            if (!overallRankings.get("combat").isJsonNull())
                combatRanking = overallRankings.get("combat").getAsInt();
            int professionRanking = 0;
            if (!overallRankings.get("profession").isJsonNull())
                professionRanking = overallRankings.get("profession").getAsInt();
            int pvpRanking = 0;
            if (!rankings.get("pvp").isJsonNull())
                pvpRanking = rankings.get("pvp").getAsInt();
            return new PlayerStatsProfile(
                    username,
                    uuid,
                    rank,
                    firstJoin,
                    lastJoin,
                    online,
                    server,
                    playtime,
                    displayTag,
                    tag,
                    veteran,
                    classes,
                    guildName,
                    guildRank,
                    chestsFound,
                    blocksWalked,
                    itemsIdentified,
                    mobsKilled,
                    totalCombatLevel,
                    totalProfessionLevel,
                    totalLevel,
                    pvpKills,
                    pvpDeaths,
                    logins,
                    deaths,
                    discoveries,
                    eventsWon,
                    guildRanking,
                    combatLevelRanking,
                    woodcuttingLevelRanking,
                    miningLevelRanking,
                    fishingLevelRanking,
                    farmingLevelRanking,
                    alchemismLevelRanking,
                    armouringLevelRanking,
                    cookingLevelRanking,
                    jewelingLevelRanking,
                    scribingLevelRanking,
                    tailoringLevelRanking,
                    weaponsmithingLevelRanking,
                    woodworkingLevelRanking,
                    professionLevelRanking,
                    overallLevelRanking,
                    overallAllRanking,
                    combatRanking,
                    professionRanking,
                    pvpRanking);
        }
    }
}
