package net.minecraft.client.gui;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatAllowedCharacters;

import java.io.IOException;

public class ClickGuiScreen extends Guiscreen {

    private int x = 80;
    private int y = 50;
    private Category Category = Category.General;
    private String searchQuery = "";
    private boolean searchFocused = false;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Lunar Client style semi-transparent dark backdrop shader overlay
        drawRect(0, 0, width, height, 0x66000000);

        // Core visual panels: Main Lunar Window Container Frame (540 width, 360 height)
        drawRect(x, y, x + 540, y + 360, 0xFA1E1E24); // Dark-charcoal background main window
        drawRect(x, y, x + 540, y + 42, 0xFF18181C);  // Upper branding navigation header bar

        // Brand Typography: Clean Lunar Client header text layout
        mc.fontRendererObj.drawString("Lunar Client", x + 16, y + 12, 0xFFFFFFFF);
        mc.fontRendererObj.drawString("(1.12.2 / NoneClient)", x + 88, y + 13, 0x66FFFFFF);

        // Search Input Box (Lunar right-aligned capsule style setup)
        int sx = x + 340;
        int sy = y + 10;
        drawRect(sx, sy, sx + 180, sy + 22, 0x44000000);
        drawRect(sx, sy + 21, sx + 180, sy + 22, searchFocused ? 0xFF3887FF : 0x11FFFFFF);

        String text = searchQuery;
        if (text.isEmpty() && !searchFocused) {
            text = "Search modules...";
        } else if (searchFocused && System.currentTimeMillis() % 1000 < 500) {
            text += "_";
        }
        mc.fontRendererObj.drawString(text, sx + 8, sy + 7, searchQuery.isEmpty() ? 0x44FFFFFF : 0xCCFFFFFF);

        // Render Tabs Horizontal Navigation Category Bar (Lunar top-row style)
        int cx = x + 16;
        int cy = y + 48;

        for (Category c : Category.values()) {
            boolean selected = c == category;
            int stringWidth = mc.fontRendererObj.getStringWidth(c.name());

            // Render bottom highlight border lines underneath active categories
            if (selected) {
                drawRect(cx, cy + 18, cx + stringWidth, cy + 20, 0xFF3887FF);
            }

            mc.fontRendererObj.drawString(
                    c.name(),
                    cx,
                    cy + 6,
                    selected ? 0xFF3887FF : 0x88FFFFFF
            );

            cx += stringWidth + 24;
        }

        // Distinct separating border array mapping line
        drawRect(x, y + 74, x + 540, y + 75, 0x11FFFFFF);

        // Main Module Grid Section Layout
        int mx = x + 20;
        int my = y + 90;
        int columnCount = 0;

        for (Module module : Client.manager.getModulesByCategory(category)) {
            if (!searchQuery.isEmpty() && !module.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                continue;
            }

            boolean enabled = module.isEnabled();

            // Lunar clean card cell housing mapping bounds
            drawRect(mx, my, mx + 240, my + 46, 0x44000000);
            drawRect(mx, my, mx + 3, my + 46, enabled ? 0xFF3887FF : 0x22FFFFFF);

            // Module Label Text strings
            mc.fontRendererObj.drawString(module.getName(), mx + 14, my + 10, enabled ? 0xFFFFFFFF : 0x99FFFFFF);

            String description = module.getDescription();
            if (description != null && !description.isEmpty() && !description.equals("- - -")) {
                mc.fontRendererObj.drawString(description, mx + 14, my + 24, 0x44FFFFFF);
            }

            // Slider toggle positioning parameters
            int tx = mx + 195;
            int ty = my + 16;
            drawRect(tx, ty, tx + 30, ty + 12, enabled ? 0xFF3887FF : 0x22FFFFFF);

            if (enabled) {
                drawRect(tx + 18, ty - 1, tx + 29, ty + 13, 0xFFFFFFFF);
            } else {
                drawRect(tx + 1, ty - 1, tx + 12, ty + 13, 0x55FFFFFF);
            }

            // Multi-column row wrapper processing configurations
            columnCount++;
            if (columnCount >= 2) {
                columnCount = 0;
                mx = x + 20;
                my += 58;
            } else {
                mx += 260;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        int sx = x + 340;
        int sy = y + 10;
        searchFocused = mouseX >= sx && mouseX <= sx + 180 && mouseY >= sy && mouseY <= sy + 22;

        int cx = x + 16;
        int cy = y + 48;

        for (Category c : Category.values()) {
            int stringWidth = mc.fontRendererObj.getStringWidth(c.name());
            if (mouseX >= cx && mouseX <= cx + stringWidth && mouseY >= cy && mouseY <= cy + 24) {
                category = c;
                return;
            }
            cx += stringWidth + 24;
        }

        int mx = x + 20;
        int my = y + 90;
        int columnCount = 0;

        for (Module module : Client.manager.getModulesByCategory(category)) {
            if (!searchQuery.isEmpty() && !module.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                continue;
            }

            if (mouseX >= mx && mouseX <= mx + 240 && mouseY >= my && mouseY <= my + 46) {
                module.toggle();
                return;
            }

            columnCount++;
            if (columnCount >= 2) {
                columnCount = 0;
                mx = x + 20;
                my += 58;
            } else {
                mx += 260;
            }
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
