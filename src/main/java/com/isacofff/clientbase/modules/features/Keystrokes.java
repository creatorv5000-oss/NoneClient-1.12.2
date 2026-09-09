package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class Keystrokes extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public Keystrokes() {
        super(
            "Keystrokes",
            "Displays your movement keys on screen",
            Category.Render
        );
    }

    public void render(ScaledResolution resolution) {
        if (mc.thePlayer == null) {
            return;
        }

        FontRenderer font = mc.fontRendererObj;

        int x = 10;
        int y = resolution.getScaledHeight() - 90;

        int size = 24;
        int gap = 2;

        drawKey(
            font,
            "W",
            x + size + gap,
            y,
            mc.gameSettings.keyBindForward.isKeyDown()
        );

        drawKey(
            font,
            "A",
            x,
            y + size + gap,
            mc.gameSettings.keyBindLeft.isKeyDown()
        );

        drawKey(
            font,
            "S",
            x + size + gap,
            y + size + gap,
            mc.gameSettings.keyBindBack.isKeyDown()
        );

        drawKey(
            font,
            "D",
            x + (size + gap) * 2,
            y + size + gap,
            mc.gameSettings.keyBindRight.isKeyDown()
        );
    }

    private void drawKey(
        FontRenderer font,
        String text,
        int x,
        int y,
        boolean pressed
    ) {
        int background = pressed
            ? 0xFF555555
            : 0xAA222222;

        Gui.drawRect(
            x,
            y,
            x + 24,
            y + 24,
            background
        );

        int textWidth = font.getStringWidth(text);

        font.drawString(
            text,
            x + (24 - textWidth) / 2,
            y + 8,
            0xFFFFFFFF
        );
    }
}
