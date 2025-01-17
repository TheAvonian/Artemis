/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.features.user;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.wynntils.core.config.Config;
import com.wynntils.core.features.UserFeature;
import com.wynntils.core.managers.Model;
import com.wynntils.mc.event.RenderLevelLastEvent;
import com.wynntils.mc.objects.CommonColors;
import com.wynntils.mc.objects.CustomColor;
import com.wynntils.mc.objects.Location;
import com.wynntils.mc.utils.McUtils;
import com.wynntils.wynn.event.TrackedQuestUpdateEvent;
import com.wynntils.wynn.model.CompassModel;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BeaconBeamFeature extends UserFeature {
    @Config
    public CustomColor waypointBeamColor = CommonColors.RED;

    @Config
    public boolean autoTrackQuestCoordinates = true;

    @Override
    public List<Class<? extends Model>> getModelDependencies() {
        return List.of(CompassModel.class);
    }

    @SubscribeEvent
    public void onTrackedQuestUpdate(TrackedQuestUpdateEvent event) {
        if (event.getQuestInfo() == null || !autoTrackQuestCoordinates) return;

        Optional<Location> location = event.getQuestInfo().getNextLocation();
        if (location.isEmpty()) return;

        CompassModel.setCompassLocation(location.get());
    }

    @SubscribeEvent
    public void onRenderLevelLast(RenderLevelLastEvent event) {
        if (CompassModel.getCompassLocation().isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource =
                MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        Player player = McUtils.player();

        poseStack.pushPose();
        float partials = event.getPartialTick();
        Camera camera = McUtils.mc().gameRenderer.getMainCamera();
        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        Location location = CompassModel.getCompassLocation().get();
        poseStack.translate(location.x, location.y, location.z);

        BeaconRenderer.renderBeaconBeam(
                poseStack,
                bufferSource,
                BeaconRenderer.BEAM_LOCATION,
                partials,
                1,
                player.level.getGameTime(),
                (int) -McUtils.player().position().y,
                319 + (int) McUtils.player().position().y,
                waypointBeamColor.asFloatArray(),
                0.166F,
                0.33F);
        poseStack.popPose();

        bufferSource.endBatch();
    }
}
