package com.autosell;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AutoSellScreen extends Screen {

    private TextFieldWidget blockField;
    private TextFieldWidget delayField;

    public AutoSellScreen() {
        super(Text.literal("AutoSell"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int sy = this.height / 2 - 20;

        blockField = new TextFieldWidget(textRenderer, cx - 100, sy, 200, 20, Text.literal("Block String"));
        blockField.setMaxLength(256);
        String savedBlock = AutoSellConfig.getBlockId();
        blockField.setText(savedBlock);
        blockField.setSuggestion(savedBlock.isEmpty() ? "e.g. basalt" : "");
        blockField.setChangedListener(t -> blockField.setSuggestion(t.isEmpty() ? "e.g. basalt" : ""));
        addDrawableChild(blockField);

        delayField = new TextFieldWidget(textRenderer, cx - 100, sy + 40, 200, 20, Text.literal("Time Delay (ms)"));
        delayField.setMaxLength(10);
        long d = AutoSellConfig.getDelayMs();
        String savedDelay = d > 0 ? String.valueOf(d) : "";
        delayField.setText(savedDelay);
        delayField.setSuggestion(savedDelay.isEmpty() ? "e.g. 500" : "");
        delayField.setChangedListener(t -> delayField.setSuggestion(t.isEmpty() ? "e.g. 500" : ""));
        addDrawableChild(delayField);

        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), btn -> applyConfig()).dimensions(cx - 105, sy + 72, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Disable"), btn -> { AutoSellConfig.disable(); close(); }).dimensions(cx + 5, sy + 72, 100, 20).build());

        setInitialFocus(blockField);
    }

    private void applyConfig() {
        String block = blockField.getText().trim();
        long delay = 0;
        try { delay = Long.parseLong(delayField.getText().trim()); if (delay < 0) delay = 0; } catch (NumberFormatException ignored) {}
        AutoSellConfig.set(block, delay);
        close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int cx = this.width / 2;
        int sy = this.height / 2 - 20;
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("AutoSell"), cx, sy - 75, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Made by TTCL"), cx, sy - 60, 0xFFAAAAAA);
        context.drawTextWithShadow(textRenderer, Text.literal("Block ID:"), cx - 100, sy - 13, 0xFFFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal("Delay (ms):"), cx - 100, sy + 27, 0xFFFFFFFF);
    }

    @Override
    public boolean shouldPause() { return true; }
}
