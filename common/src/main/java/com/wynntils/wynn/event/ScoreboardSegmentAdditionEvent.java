/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.wynn.event;

import com.wynntils.core.events.EventThread;
import com.wynntils.wynn.model.scoreboard.Segment;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
@EventThread(EventThread.Type.WORKER)
public class ScoreboardSegmentAdditionEvent extends Event {
    private final Segment segment;

    public ScoreboardSegmentAdditionEvent(Segment segment) {
        this.segment = segment;
    }

    public Segment getSegment() {
        return segment;
    }
}
