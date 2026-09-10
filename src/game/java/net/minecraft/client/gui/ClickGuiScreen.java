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
        // Feather Client premium dark glass backing overlay panel layout
        drawRect(0, 0, width, height, 0x440A0A0C);

        // Core visual panels: Main Container Frame (530 width, 350 height)
        drawRect(x, y, x + 530, y + 350, 0xDD121316); // Translucent Main Body
        drawRect(x, y, x + 140, y + 350, 0xF016181C); // Left Sidebar Base Layer

        // Sleek subtle column dividing lines (1px)
        drawRect(x + 140, y, x + 141, y + 350, 0x1F7F8C9D);

        // Brand Typography: Feather style minimal branding layout
        Minecraft.getMinecraft().fontRendererObj.drawString("NoneClient", x + 18, y + 16, 0xFFFFFFFF);
        Minecraft.getMinecraft().fontRendererObj.drawString("v1.12.2", x + 18, y + 28, 0x55FFFFFF);

        int cy = y + 54;

        // Render Tabs Sidebar List Loop Layer
        for (Category c : Category.values()) {
            boolean selected = c == category;

            // Render modern flat hover accent bar blocks if selected
            if (selected) {
                drawRect(x + 10, cy, x + 130, cy + 22, 0x1A4C9AFF); // Soft highlight backing capsule
                drawRect(x + 10, cy + 4, x + 12, cy + 18, 0xFF4C9AFF); // Left vertical micro accent pillar
            }

            Minecraft.getMinecraft().fontRendererObj.drawString(
                    c.name(),
                    x + 22,
                    cy + 7,
                    selected ? 0xFF4C9AFF : 0x88FFFFFF
            );

            cy += 26;
        }

        // Search Input Box Positioning Configurations
        int sx = x + 155;
        int sy = y + 16;

        // Background input housing
        drawRect(sx, sy, sx + 355, sy + 22, 0x33000000);
        // Clean micro indicator active box underlines
        drawRect(sx, sy + 21, sx + 355, sy + 22, searchFocused ? 0xFF4C9AFF : 0x22FFFFFF);

        String text = searchQuery;
        if (text.isEmpty() && !searchFocused) {
            text = "Search mods...";
        } else if (searchFocused && System.currentTimeMillis() % 1000 < 500) {
            text += "_";
        }

        Minecraft.getMinecraft().fontRendererObj.drawString(
                text,
                sx + 8,
                sy + 7,
                searchQuery.isEmpty() ? 0x44FFFFFF : 0xCCFFFFFF
        );

        // Main Module List Rendering Stream
        int my = y + 54;

        for (Module module : Client.manager.getModulesByCategory(category)) {
            if (!searchQuery.isEmpty() && !module.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                continue;
            }

            boolean enabled = module.isEnabled();

            // Feather UI clean capsule grid frames layout
            drawRect(x + 155, my, x + 510, my + 38, 0x22FFFFFF); // Soft outer cell glow mapping container
            drawRect(x + 155, my, x + 156, my + 38, enabled ? 0xFF4C9AFF : 0x33FFFFFF); // Dynamic feature state highlights

            // Module Label Text
            Minecraft.getMinecraft().fontRendererObj.drawString(
                    module.getName(),
                    x + 168,
                    my + 9,
                    enabled ? 0xFFFFFFFF : 0xAAFFFFFF
            );

            String description = module.getDescription();
            if (description != null && !description.isEmpty() && !description.equals("- - -")) {
                Minecraft.getMinecraft().fontRendererObj.drawString(
                        description,
                        x + 168,
                        my + 22,
                        0x44FFFFFF
                );
            }

            // High Tech Rounded Pill Switch Simulation Positioning
            int tx = x + 465;
            int ty = my + 14;

            // Render Switch Slider Background Housing Channel
            drawRect(tx, ty, tx + 30, ty + 12, enabled ? 0x444C9AFF : 0x22FFFFFF);

            // Render Active Thumb Slider Knobs
            if (enabled) {
                drawRect(tx + 18, ty - 1, tx + 29, ty + 13, 0xFF4C9AFF);
            } else {
                drawRect(tx + 1, ty - 1, tx + 12, ty + 13, 0x66FFFFFF);
            }

            my += 44;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        int sx = x + 155;
        int sy = y + 16;

        searchFocused = mouseX >= sx && mouseX <= sx + 355 && mouseY >= sy && mouseY <= sy + 22;

        int cy = y + 54;

        for (Category c : Category.values()) {
            if (mouseX >= x + 10 && mouseX <= x + 130 && mouseY >= cy && mouseY <= cy + 22) {
                category = c;
                return;
            }
            cy += 26;
        }

        int my = y + 54;

        for (Module module : Client.manager.getModulesByCategory(category)) {
            if (!searchQuery.isEmpty() && !module.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                continue;
            }

            if (mouseX >= x + 155 && mouseX <= x + 510 && mouseY >= my && mouseY <= my + 38) {
                module.toggle();
                return;
            }
            my += 44;
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (searchFocused) {
            if (keyCode == 1) {
                searchFocused = false;
            } else if (keyCode == 14) {
                if (!searchQuery.isEmpty()) {
                    searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
                }
            } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
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
