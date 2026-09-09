package net.minecraft.client.gui;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatAllowedCharacters;

import java.io.IOException;

public class ClickGuiScreen extends GuiScreen {

    private int x = 80;
    private int y = 50;
    private Category category = Category.Client;
    private String searchQuery = "";
    private boolean searchFocused = false;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        drawRect(0, 0, width, height, 0x77000000);

        drawRect(x, y, x + 510, y + 335, 0xFF111214);
        drawRect(x, y, x + 130, y + 335, 0xFF181A1D);
        drawRect(x + 130, y, x + 131, y + 335, 0xFF282B30);

        Minecraft.getMinecraft().fontRendererObj.drawString("NoneClient", x + 15, y + 15, 0xFFFFFFFF);
        Minecraft.getMinecraft().fontRendererObj.drawString("1.12.2", x + 15, y + 27, 0xFF686D75);

        int cy = y + 48;

        for (Category c : Category.values()) {

            boolean selected = c == category;

            if (selected) {
                drawRect(x + 8, cy, x + 120, cy + 24, 0xFF24282E);
                drawRect(x + 8, cy, x + 10, cy + 24, 0xFF4C9AFF);
            }

            Minecraft.getMinecraft().fontRendererObj.drawString(
                    c.name(),
                    x + 19,
                    cy + 8,
                    selected ? 0xFFFFFFFF : 0xFF858A92
            );

            cy += 29;
        }

        int sx = x + 145;
        int sy = y + 14;

        drawRect(sx, sy, sx + 340, sy + 23, 0xFF191B1F);

        drawRect(
                sx,
                sy,
                sx + 340,
                sy + 1,
                searchFocused ? 0xFF4C9AFF : 0xFF292C31
        );

        String text = searchQuery;

        if (text.isEmpty() && !searchFocused) {
            text = "Search modules...";
        } else if (
                searchFocused &&
                System.currentTimeMillis() % 1000 < 500
        ) {
            text += "_";
        }

        Minecraft.getMinecraft().fontRendererObj.drawString(
                text,
                sx + 8,
                sy + 8,
                searchQuery.isEmpty() ? 0xFF666A70 : 0xFFE8E8E8
        );

        int my = y + 50;

        for (Module module :
                Client.manager.getModulesByCategory(category)) {

            if (
                    !searchQuery.isEmpty() &&
                    !module.getName()
                            .toLowerCase()
                            .contains(searchQuery.toLowerCase())
            ) {
                continue;
            }

            boolean enabled = module.isEnabled();

            drawRect(
                    x + 145,
                    my,
                    x + 495,
                    my + 40,
                    enabled ? 0xFF1D2632 : 0xFF181A1E
            );

            drawRect(
                    x + 145,
                    my,
                    x + 495,
                    my + 1,
                    enabled ? 0xFF31547E : 0xFF292C31
            );

            Minecraft.getMinecraft().fontRendererObj.drawString(
                    module.getName(),
                    x + 160,
                    my + 10,
                    enabled ? 0xFF4C9AFF : 0xFFE8E8E8
            );

            String description = module.getDescription();

            if (
                    description != null &&
                    !description.isEmpty() &&
                    !description.equals("- - -")
            ) {

                Minecraft.getMinecraft().fontRendererObj.drawString(
                        description,
                        x + 160,
                        my + 24,
                        0xFF666B73
                );
            }

            int tx = x + 455;
            int ty = my + 14;

            drawRect(
                    tx,
                    ty,
                    tx + 26,
                    ty + 12,
                    enabled ? 0xFF4C9AFF : 0xFF30343A
            );

            if (enabled) {
                drawRect(
                        tx + 16,
                        ty - 1,
                        tx + 26,
                        ty + 13,
                        0xFFFFFFFF
                );
            } else {
                drawRect(
                        tx,
                        ty - 1,
                        tx + 10,
                        ty + 13,
                        0xFF777C84
                );
            }

            my += 48;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(
            int mouseX,
            int mouseY,
            int button
    ) throws IOException {

        int sx = x + 145;
        int sy = y + 14;

        searchFocused =
                mouseX >= sx &&
                mouseX <= sx + 340 &&
                mouseY >= sy &&
                mouseY <= sy + 23;

        int cy = y + 48;

        for (Category c : Category.values()) {

            if (
                    mouseX >= x + 8 &&
                    mouseX <= x + 120 &&
                    mouseY >= cy &&
                    mouseY <= cy + 24
            ) {
                category = c;
                return;
            }

            cy += 29;
        }

        int my = y + 50;

        for (Module module :
                Client.manager.getModulesByCategory(category)) {

            if (
                    !searchQuery.isEmpty() &&
                    !module.getName()
                            .toLowerCase()
                            .contains(searchQuery.toLowerCase())
            ) {
                continue;
            }

            if (
                    mouseX >= x + 145 &&
                    mouseX <= x + 495 &&
                    mouseY >= my &&
                    mouseY <= my + 40
            ) {
                module.toggle();
                return;
            }

            my += 48;
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyTyped(
            char typedChar,
            int keyCode
    ) throws IOException {

        if (searchFocused) {

            if (keyCode == 1) {
                searchFocused = false;

            } else if (keyCode == 14) {

                if (!searchQuery.isEmpty()) {
                    searchQuery = searchQuery.substring(
                            0,
                            searchQuery.length() - 1
                    );
                }

            } else if (
                    ChatAllowedCharacters
                            .isAllowedCharacter(typedChar)
            ) {
                searchQuery += typedChar;
            }

        } else {

            if (keyCode == 1) {
                mc.displayGuiScreen(null);
                return;
            }

            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
