/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.core.webapi;

import com.wynntils.core.WynntilsMod;
import com.wynntils.core.webapi.request.RequestHandler;
import com.wynntils.wynn.event.ChatMessageReceivedEvent;
import java.util.regex.Pattern;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FriendUpdateThread extends Thread {

    private static final int START_UPDATE_TIME = 1000;
    private static final int ONLINE_UPDATE_TIME = 10000;
    private static final Pattern COMPILE = Pattern.compile(", ");

    private int timeBetween = START_UPDATE_TIME;

    public FriendUpdateThread(String name) {
        super(name);

        WynntilsMod.registerEventListener(this);
    }

    private int friendsLeft = 0;

    @SubscribeEvent
    public void handleMessages(ChatMessageReceivedEvent event) {
        String message = event.getMessage().getString();
        WynntilsMod.info("New Message: " + message);
        if (!message.contains("'s friends (")) {
            return;
        }

        String[] friendNames = COMPILE.split(message);

        for (String name : friendNames) {
            WynntilsMod.info("Found friend " + name);
            String fName = name;
            if (fName.isEmpty()) {
                continue;
            }

            if (fName.contains(" ")) {
                fName = fName.replace(" ", "");
            }

            WebManager.addFriend(fName);
            ++friendsLeft;
        }
    }

    @Override
    public void run() {
        RequestHandler handler = new RequestHandler();

        try {
            /*
            Thread.sleep(ONLINE_UPDATE_TIME);

            while (!isInterrupted() && friendsLeft > 0) {
                WebManager.tryLoadFriends(handler);
                Thread.sleep(timeBetween);
                --friendsLeft;
            }*/

            timeBetween = ONLINE_UPDATE_TIME;

            while (!isInterrupted()) {
                WebManager.tryLoadFriends(handler);
                // handler.dispatch();
                Thread.sleep(timeBetween);
            }
        } catch (InterruptedException ignored) {
        }

        WynntilsMod.unregisterEventListener(this);
        WynntilsMod.info("Terminating friend update thread.");
    }
}
