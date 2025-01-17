/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.gui.screens.settings.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.config.ConfigHolder;

public abstract class ConfigOptionElement {
    protected final ConfigHolder configHolder;

    public ConfigOptionElement(ConfigHolder configHolder) {
        this.configHolder = configHolder;
    }

    public ConfigHolder getConfigHolder() {
        return configHolder;
    }

    public abstract void renderConfigAppropriateButton(
            PoseStack poseStack, float width, float height, int mouseX, int mouseY, float partialTicks);

    public abstract boolean mouseClicked(double mouseX, double mouseY, int button);
}
