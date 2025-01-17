/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.wynn.item.properties;

import com.wynntils.features.user.inventory.ItemHighlightFeature;
import com.wynntils.features.user.inventory.ItemTextOverlayFeature;
import com.wynntils.gui.render.TextRenderSetting;
import com.wynntils.gui.render.TextRenderTask;
import com.wynntils.mc.objects.CustomColor;
import com.wynntils.utils.MathUtils;
import com.wynntils.wynn.item.WynnItemStack;
import com.wynntils.wynn.item.parsers.WynnItemMatchers;
import com.wynntils.wynn.item.properties.type.HighlightProperty;
import com.wynntils.wynn.item.properties.type.TextOverlayProperty;
import java.util.regex.Matcher;
import net.minecraft.ChatFormatting;

public class EmeraldPouchTierProperty extends ItemProperty implements HighlightProperty, TextOverlayProperty {
    private final CustomColor highlightColor = CustomColor.fromChatFormatting(ChatFormatting.GREEN);

    private final TextOverlay textOverlay;

    public EmeraldPouchTierProperty(WynnItemStack item) {
        super(item);

        // parse tier
        Matcher emeraldPouchMatcher = WynnItemMatchers.emeraldPouchTierMatcher(item.getHoverName());
        String numeral = emeraldPouchMatcher.find() ? emeraldPouchMatcher.group(1) : "I";

        // convert from roman to arabic if necessary
        String text = ItemTextOverlayFeature.emeraldPouchTierRomanNumerals
                ? numeral
                : String.valueOf(MathUtils.integerFromRoman(numeral));

        TextRenderSetting style = TextRenderSetting.DEFAULT
                .withCustomColor(highlightColor)
                .withTextShadow(ItemTextOverlayFeature.emeraldPouchTierShadow);

        textOverlay = new TextOverlay(new TextRenderTask(text, style), -1, 1, 0.75f);
    }

    @Override
    public boolean isHighlightEnabled() {
        return ItemHighlightFeature.emeraldPouchHighlightEnabled;
    }

    @Override
    public CustomColor getHighlightColor() {
        return highlightColor;
    }

    @Override
    public boolean isTextOverlayEnabled() {
        return ItemTextOverlayFeature.emeraldPouchTierEnabled;
    }

    @Override
    public TextOverlay getTextOverlay() {
        return textOverlay;
    }
}
