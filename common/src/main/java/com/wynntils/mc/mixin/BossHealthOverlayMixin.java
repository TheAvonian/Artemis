/*
 * Copyright © Wynntils 2021.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.mc.mixin;

import com.google.common.collect.Maps;
import com.wynntils.mc.EventFactory;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {
    @Mutable
    @Shadow
    public Map<UUID, LerpingBossEvent> events;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onCtor(CallbackInfo ci) {
        events = Maps.newConcurrentMap();
    }

    @Inject(
            method = "update(Lnet/minecraft/network/protocol/game/ClientboundBossEventPacket;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void updatePre(ClientboundBossEventPacket packet, CallbackInfo ci) {
        if (EventFactory.onBossHealthUpdate(packet, events).isCanceled()) {
            ci.cancel();
        }
    }
}
