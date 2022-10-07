/*
 * Copyright © Wynntils 2022.
 * This file is released under AGPLv3. See LICENSE for full license details.
 */
package com.wynntils.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.WynntilsMod;
import com.wynntils.core.webapi.WebManager;
import com.wynntils.core.webapi.profiles.PlayerStatsProfile;
import com.wynntils.gui.render.FontRenderer;
import com.wynntils.gui.render.HorizontalAlignment;
import com.wynntils.gui.render.TextRenderSetting;
import com.wynntils.gui.render.TextRenderTask;
import com.wynntils.gui.render.Texture;
import com.wynntils.gui.render.VerticalAlignment;
import com.wynntils.gui.widgets.BackButton;
import com.wynntils.gui.widgets.PageSelectorButton;
import com.wynntils.gui.widgets.QuestsPageButton;
import com.wynntils.gui.widgets.ReloadButton;
import com.wynntils.mc.objects.CommonColors;
import com.wynntils.utils.MathUtils;
import com.wynntils.wynn.event.QuestBookReloadedEvent;
import com.wynntils.wynn.model.quests.QuestManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WynntilsFriendStatsScreen extends WynntilsMenuPagedScreenBase {
    private int currentPage = 0;

    private List<List<PlayerStatsProfile>> friends = new ArrayList<>();

    public WynntilsFriendStatsScreen() {
        super(new TranslatableComponent("screens.wynntils.wynntilsQuestBook.friendStats.name"));

        WynntilsMod.registerEventListener(this);
    }

    @Override
    public void onClose() {
        WynntilsMod.unregisterEventListener(this);
        super.onClose();
    }

    @Override
    protected void init() {
        // QuestManager.rescanDialogueHistory();

        this.addRenderableWidget(new BackButton(
                (int) ((Texture.QUEST_BOOK_BACKGROUND.width() / 2f - 16) / 2f),
                65,
                Texture.BACK_ARROW.width() / 2,
                Texture.BACK_ARROW.height(),
                new WynntilsMenuScreen()));
        this.addRenderableWidget(new ReloadButton(
                Texture.QUEST_BOOK_BACKGROUND.width() - 21,
                11,
                (int) (Texture.RELOAD_BUTTON.width() / 2 / 1.7f),
                (int) (Texture.RELOAD_BUTTON.height() / 1.7f),
                QuestManager::rescanDialogueHistory));
        this.addRenderableWidget(new PageSelectorButton(
                Texture.QUEST_BOOK_BACKGROUND.width() / 2 + 50 - Texture.FORWARD_ARROW.width() / 2,
                Texture.QUEST_BOOK_BACKGROUND.height() - 25,
                Texture.FORWARD_ARROW.width() / 2,
                Texture.FORWARD_ARROW.height(),
                false,
                this));
        this.addRenderableWidget(new PageSelectorButton(
                Texture.QUEST_BOOK_BACKGROUND.width() - 50,
                Texture.QUEST_BOOK_BACKGROUND.height() - 25,
                Texture.FORWARD_ARROW.width() / 2,
                Texture.FORWARD_ARROW.height(),
                true,
                this));
        this.addRenderableWidget(new QuestsPageButton(
                (int) (Texture.QUEST_BOOK_BACKGROUND.width() / 2f - 30),
                12,
                Texture.QUESTS_BUTTON.width(),
                Texture.QUESTS_BUTTON.height()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        final float translationX = getTranslationX();
        final float translationY = getTranslationY();

        for (GuiEventListener child : new ArrayList<>(this.children())) {
            if (child.isMouseOver(mouseX - translationX, mouseY - translationY)) {
                child.mouseClicked(mouseX - translationX, mouseY - translationY, button);
            }
        }

        return true;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackgroundTexture(poseStack);

        // Make 0, 0 the top left corner of the rendered quest book background
        poseStack.pushPose();
        final float translationX = getTranslationX();
        final float translationY = getTranslationY();
        poseStack.translate(translationX, translationY, 1f);

        renderTitle(poseStack, I18n.get("screens.wynntils.wynntilsQuestBook.friendStats.name"));

        renderVersion(poseStack);

        renderButtons(poseStack, mouseX, mouseY, partialTick);

        /*if (dialogues.isEmpty()) {
            renderNoDialoguesHelper(poseStack);
        } else {

        }*/

        renderCurrentPage(poseStack);
        renderDescription(poseStack, I18n.get("screens.wynntils.wynntilsDialogueHistory.description"));

        renderPageInfo(poseStack, getCurrentPage() + 1, getMaxPage() + 1);

        poseStack.popPose();

        renderTooltip(poseStack, mouseX, mouseY);
    }

    private void renderCurrentPage(PoseStack poseStack) {
        List<TextRenderTask> textRenderTaskList = new ArrayList<>();
        float maxWidth = Texture.QUEST_BOOK_BACKGROUND.width() / 2f - 20;

        if (!friends.isEmpty())
            for (PlayerStatsProfile line : friends.get(0)) {
                textRenderTaskList.add(new TextRenderTask(
                        line.getUsername(),
                        new TextRenderSetting(
                                maxWidth,
                                CommonColors.BLACK,
                                HorizontalAlignment.Left,
                                VerticalAlignment.Top,
                                FontRenderer.TextShadow.NORMAL)));
            }

        FontRenderer.getInstance()
                .renderTextsWithAlignment(
                        poseStack,
                        Texture.QUEST_BOOK_BACKGROUND.width() / 2f + 5,
                        30,
                        textRenderTaskList,
                        maxWidth,
                        Texture.QUEST_BOOK_BACKGROUND.height() - 50,
                        HorizontalAlignment.Left,
                        VerticalAlignment.Middle);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        setCurrentPage(getCurrentPage() + (delta > 0 ? -1 : 1));

        return true;
    }

    @SubscribeEvent
    public void onQuestsReloaded(QuestBookReloadedEvent.FriendStatsReloaded event) {
        this.setFriends(WebManager.getFriendStats());
    }

    private void setFriends(List<PlayerStatsProfile> friendStats) {
        this.friends = getListSplitIntoParts(friendStats, 12);
    }

    public static <T> List<List<T>> getListSplitIntoParts(List<T> list, int splitSize) {
        List<List<T>> splitList = new ArrayList<>();
        List<T> currentList = new ArrayList<>();

        for (T entry : list) {
            if (currentList.size() == splitSize) {
                splitList.add(currentList);
                currentList = new ArrayList<>();
            }

            currentList.add(entry);
        }

        if (!currentList.isEmpty()) {
            splitList.add(currentList);
        }

        return splitList;
    }

    private Widget hovered = null;

    private void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        /*if (this.hovered instanceof ReloadButton) {
            RenderUtils.drawTooltipAt(
                    poseStack,
                    mouseX,
                    mouseY,
                    100,
                    RELOAD_TOOLTIP,
                    FontRenderer.getInstance().getFont(),
                    true);
            return;
        }*/

        /*if (this.hovered instanceof QuestsPageButton) {
        List<Component> tooltipLines = List.of(
                new TextComponent("[>] ")
                        .withStyle(ChatFormatting.GOLD)
                        .append(new TranslatableComponent(
                                        "screens.wynntils.wynntilsDialogueHistory.questsPageButton.name")
                                .withStyle(ChatFormatting.BOLD)
                                .withStyle(ChatFormatting.GOLD)),
                new TranslatableComponent("screens.wynntils.wynntilsDialogueHistory.questsPageButton.description")
                        .withStyle(ChatFormatting.GRAY),
                new TextComponent(""),
                new TranslatableComponent("screens.wynntils.wynntilsMenu.leftClickToSelect")
                        .withStyle(ChatFormatting.GREEN));*/

        /*RenderUtils.drawTooltipAt(
                    poseStack,
                    mouseX,
                    mouseY,
                    100,
                    tooltipLines,
                    FontRenderer.getInstance().getFont(),
                    true);
        }*/
    }

    private void renderButtons(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.hovered = null;

        final float translationX = getTranslationX();
        final float translationY = getTranslationY();

        for (Widget renderable : new ArrayList<>(this.renderables)) {
            renderable.render(poseStack, (int) (mouseX - translationX), (int) (mouseY - translationY), partialTick);

            if (renderable instanceof AbstractButton button) {
                if (button.isMouseOver(mouseX - translationX, mouseY - translationY)) {
                    this.hovered = button;
                }
            }
        }
    }

    private static void renderNoDialoguesHelper(PoseStack poseStack) {
        FontRenderer.getInstance()
                .renderAlignedTextInBox(
                        poseStack,
                        I18n.get("screens.wynntils.wynntilsDialogueHistory.tryReload"),
                        Texture.QUEST_BOOK_BACKGROUND.width() / 2f + 15f,
                        Texture.QUEST_BOOK_BACKGROUND.width() - 15f,
                        0,
                        Texture.QUEST_BOOK_BACKGROUND.height(),
                        Texture.QUEST_BOOK_BACKGROUND.width() / 2f - 30f,
                        CommonColors.BLACK,
                        HorizontalAlignment.Center,
                        VerticalAlignment.Middle,
                        FontRenderer.TextShadow.NONE);
    }

    private void setDialogues(List<List<String>> dialogues) {
        // Optimize pages so they fit
        List<List<String>> splitDialogues = new ArrayList<>();
        List<String> currentPage = new ArrayList<>();
        float currentHeight = 0;

        float maxWidth = Texture.QUEST_BOOK_BACKGROUND.width() / 2f - 20;
        /*final float maxPageHeight = LINES_PER_PAGE * 9f;

        for (List<String> dialogueList : dialogues) {
            for (String dialogueLine : dialogueList) {
                float renderHeight = FontRenderer.getInstance().calculateRenderHeight(List.of(dialogueLine), maxWidth);

                if (currentHeight + renderHeight > maxPageHeight) {
                    splitDialogues.add(currentPage);
                    currentPage = new ArrayList<>();
                    currentHeight = renderHeight;
                    currentPage.add(dialogueLine);
                } else {
                    currentPage.add(dialogueLine);
                    currentHeight += renderHeight;
                }
            }
        }

        this.dialogues = splitDialogues;
        this.currentPage = 0;*/
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = MathUtils.clamp(currentPage, 0, getMaxPage());
    }

    @Override
    public int getMaxPage() {
        return Math.max(0, friends.size() - 1);
    }
}
