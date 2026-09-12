package com.isacofff.clientbase.gui;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.modules.Module;
import com.isacofff.clientbase.settings.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGuiScreen extends GuiScreen {

    private final int x = 80;
    private final int y = 50;
    private final int width = 480;
    private final int height = 320;

    private Category category = Category.Client;
    private String searchQuery = "";
    private boolean searchFocused = false;

    // Active slider drag state
    private Setting.NumberSetting draggingSetting = null;
    private int dragTrackX, dragTrackWidth;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Dim background
        this.drawRect(0, 0, width(), height(), 0x66000000);

        // Main glass panel
        this.drawRect(x, y, x + width, y + height, 0xCC1A1A20);
        this.drawRect(x, y, x + width, y + 1, 0x33FFFFFF); // top highlight edge
        this.drawRect(x, y, x + width, y + 30, 0xDD121216); // header bar

        this.fontRendererObj.drawString("NoneClient", x + 12, y + 10, 0xFFFFFFFF);

        // Search box
        int sx = x + width - 170;
        int sy = y + 6;
        this.drawRect(sx, sy, sx + 160, sy + 18, 0x44000000);
        this.drawRect(sx, sy + 17, sx + 160, sy + 18, searchFocused ? 0xFF3887FF : 0x22FFFFFF);

        String shown = searchQuery;
        if (shown.isEmpty() && !searchFocused) {
            shown = "Search...";
        } else if (searchFocused && System.currentTimeMillis() % 1000 < 500) {
            shown += "_";
        }
        this.fontRendererObj.drawString(shown, sx + 6, sy + 5,
                searchQuery.isEmpty() ? 0x66FFFFFF : 0xCCFFFFFF);

        // Category tabs
        int cx = x + 12;
        int cy = y + 38;
        for (Category c : Category.values()) {
            boolean selected = c == category;
            int w = this.fontRendererObj.getStringWidth(c.name());
            if (selected) {
                this.drawRect(cx, cy + 14, cx + w, cy + 16, 0xFF3887FF);
            }
            this.fontRendererObj.drawString(c.name(), cx, cy, selected ? 0xFFFFFFFF : 0x99FFFFFF);
            cx += w + 20;
        }

        this.drawRect(x, y + 62, x + width, y + 63, 0x22FFFFFF);

        // Module list
        int my = y + 74;
        int rowX = x + 12;
        int rowWidth = width - 24;

        for (Module module : Client.manager.getModulesByCategory(category)) {
            if (!searchQuery.isEmpty()
                    && !module.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                continue;
            }

            boolean enabled = module.isEnabled();

            // Row background (glass card)
            this.drawRect(rowX, my, rowX + rowWidth, my + 22, 0x33000000);
            this.drawRect(rowX, my, rowX + 2, my + 22, enabled ? 0xFF3887FF : 0x22FFFFFF);

            this.fontRendererObj.drawString(module.getName(), rowX + 10, my + 7,
                    enabled ? 0xFFFFFFFF : 0xAAFFFFFF);

            // Expand arrow
            String arrow = module.open ? "-" : "+";
            this.fontRendererObj.drawString(arrow, rowX + rowWidth - 40, my + 7, 0x99FFFFFF);

            // Toggle switch
            int tx = rowX + rowWidth - 26;
            int ty = my + 5;
            this.drawRect(tx, ty, tx + 20, ty + 10, enabled ? 0xFF3887FF : 0x22FFFFFF);
            if (enabled) {
                this.drawRect(tx + 11, ty - 1, tx + 21, ty + 11, 0xFFFFFFFF);
            } else {
                this.drawRect(tx - 1, ty - 1, tx + 9, ty + 11, 0x55FFFFFF);
            }

            my += 24;

            if (module.open) {
                for (Setting<?> setting : module.getSettings()) {
                    my = drawSetting(setting, rowX + 16, my, rowWidth - 16);
                }
                my += 4;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private int drawSetting(Setting<?> setting, int sx, int sy, int sw) {
        this.drawRect(sx, sy, sx + sw, sy + 18, 0x22000000);
        this.fontRendererObj.drawString(setting.getName(), sx + 6, sy + 5, 0xAAFFFFFF);

        if (setting instanceof Setting.BooleanSetting) {
            Setting.BooleanSetting b = (Setting.BooleanSetting) setting;
            int bx = sx + sw - 16;
            this.drawRect(bx, sy + 3, bx + 12, sy + 15, b.getValue() ? 0xFF3887FF : 0x33FFFFFF);
        } else if (setting instanceof Setting.NumberSetting) {
            Setting.NumberSetting n = (Setting.NumberSetting) setting;
            int trackX = sx + sw - 110;
            int trackW = 90;
            double percent = (n.getValue() - n.getMin()) / (n.getMax() - n.getMin());
            this.drawRect(trackX, sy + 8, trackX + trackW, sy + 10, 0x33FFFFFF);
            int knobX = trackX + (int) (percent * trackW);
            this.drawRect(knobX - 2, sy + 4, knobX + 2, sy + 14, 0xFF3887FF);
            this.fontRendererObj.drawString(String.format("%.1f", n.getValue()),
                    sx + sw - 150, sy + 5, 0x99FFFFFF);
        } else if (setting instanceof Setting.ModeSetting) {
            Setting.ModeSetting m = (Setting.ModeSetting) setting;
            this.fontRendererObj.drawString(m.getValue(), sx + sw - 60, sy + 5, 0xFF3887FF);
        }

        return sy + 20;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        int sx = x + width - 170;
        int sy = y + 6;
        searchFocused = mouseX >= sx && mouseX <= sx + 160 && mouseY >= sy && mouseY <= sy + 18;

        int cx = x + 12;
        int cy = y + 38;
        for (Category c : Category.values()) {
            int w = this.fontRendererObj.getStringWidth(c.name());
            if (mouseX >= cx && mouseX <= cx + w && mouseY >= cy && mouseY <= cy + 16) {
                category = c;
                return;
            }
            cx += w + 20;
        }

        int my = y + 74;
        int rowX = x + 12;
        int rowWidth = width - 24;

        for (Module module : Client.manager.getModulesByCategory(category)) {
            if (!searchQuery.isEmpty()
                    && !module.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                continue;
            }

            if (mouseY >= my && mouseY <= my + 22) {
                int expandX = rowX + rowWidth - 40;
                int toggleX = rowX + rowWidth - 26;

                if (mouseX >= toggleX - 4 && mouseX <= toggleX + 22) {
                    module.toggle();
                } else if (mouseX >= expandX - 4 && mouseX <= expandX + 14) {
                    module.open = !module.open;
                }
                return;
            }
            my += 24;

            if (module.open) {
                for (Setting<?> setting : module.getSettings()) {
                    int sw = rowWidth - 16;
                    int settingSx = rowX + 16;
                    if (mouseY >= my && mouseY <= my + 18) {
                        handleSettingClick(setting, mouseX, settingSx, sw, my);
                        return;
                    }
                    my += 20;
                }
                my += 4;
            }
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleSettingClick(Setting<?> setting, int mouseX, int sx, int sw, int sy) {
        if (setting instanceof Setting.BooleanSetting) {
            ((Setting.BooleanSetting) setting).toggle();
        } else if (setting instanceof Setting.NumberSetting) {
            Setting.NumberSetting n = (Setting.NumberSetting) setting;
            int trackX = sx + sw - 110;
            int trackW = 90;
            if (mouseX >= trackX && mouseX <= trackX + trackW) {
                draggingSetting = n;
                dragTrackX = trackX;
                dragTrackWidth = trackW;
                updateNumberSetting(n, mouseX);
            }
        } else if (setting instanceof Setting.ModeSetting) {
            ((Setting.ModeSetting) setting).cycle();
        }
    }

    private void updateNumberSetting(Setting.NumberSetting n, int mouseX) {
        double percent = (mouseX - dragTrackX) / (double) dragTrackWidth;
        percent = Math.max(0.0, Math.min(1.0, percent));
        double raw = n.getMin() + percent * (n.getMax() - n.getMin());
        double stepped = Math.round(raw / n.getIncrement()) * n.getIncrement();
        stepped = Math.max(n.getMin(), Math.min(n.getMax(), stepped));
        n.setValue(stepped);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (draggingSetting != null) {
            updateNumberSetting(draggingSetting, mouseX);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggingSetting = null;
        super.mouseReleased(mouseX, mouseY, state);
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
        } else if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private int width() {
        return this.width;
    }

    private int height() {
        return this.height;
    }
}
