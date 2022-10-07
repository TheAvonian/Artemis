/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.wynn.event;

import com.wynntils.core.events.EventThread;
import net.minecraftforge.eventbus.api.Event;

public abstract class QuestBookReloadedEvent extends Event {
    public static class QuestsReloaded extends QuestBookReloadedEvent {}

    public static class MiniQuestsReloaded extends QuestBookReloadedEvent {}

    public static class DialogueHistoryReloaded extends QuestBookReloadedEvent {}

    @EventThread(EventThread.Type.ANY)
    public static class FriendStatsReloaded extends QuestBookReloadedEvent {}
}
