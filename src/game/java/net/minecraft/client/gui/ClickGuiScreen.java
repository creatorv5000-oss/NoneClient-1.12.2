package net.minecraft.client.gui;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.modules.Module;
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
        
        drawRect(x, y, x + 510, y + 335, 0xFF121214);
        drawRect(x, y, x + 130, y + 335, 0xFF18181C);
        drawRect(x + 130, y, x + 131, y + 335, 0xFF242428);

        this.fontRenderer.drawString("NoneClient", x + 16, y + 16, 0xFFFFFFFF);

        int cy = y + 45;
        for (Category c : Category.values()) {
            boolean isCurrent = (c == category);
            
            if (isCurrent) {
                drawRect(x + 10, cy, x + 120, cy + 22, 0xFF24242A);
                drawRect(x + 10, cy, x + 12, cy + 22, 0xFF3880FF);
            }

            this.fontRenderer.drawString(
                    c.name(),
                    x + 20,
                    cy + 7,
                    isCurrent ? 0xFFFFFFFF : 0xFF7A7A85
            );
            cy += 28;
        }

        int sx = x + 145;
        int sy = y + 14;
        drawRect(sx, sy, sx + 340, sy + 22, searchFocused ? 0xFF202024 : 0xFF18181C);
        
        int searchBorder = searchFocused ? 0xFF3880FF : 0xFF242428;
        drawRect(sx, sy, sx + 340, sy + 1, searchBorder);
        drawRect(sx, sy + 21, sx + 340, sy + 22, searchBorder);
        drawRect(sx, sy, sx + 1, sy + 22, searchBorder);
        drawRect(sx + 339, sy, sx + 340, sy + 22, searchBorder);

        if (searchQuery.isEmpty() && !searchFocused) {
            this.fontRenderer.drawString("Search modules...", sx + 8, sy + 7, 0xFF55555F);
        } else {
            this.fontRenderer.drawString(searchQuery + (searchFocused && System.currentTimeMillis() % 1000 < 500 ? "_" : ""), sx + 8, sy + 7, 0xFFEEEEEE);
        }

        int my = y + 50;
        for (Module module : Client.manager.getModulesByCategory(category)) {
            if (!searchQuery.isEmpty() && !module.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                continue;
            }

            boolean enabled = module.isEnabled();
            
            drawRect(x + 145, my, x + 495, my + 40, enabled ? 0xFF1D2433 : 0xFF18181C);
            
            int borderColor = enabled ? 0xFF2B4066 : 0xFF242428;
            drawRect(x + 145, my, x + 495, my + 1, borderColor);
            drawRect(x + 145, my + 39, x + 495, my + 40, borderColor);
            drawRect(x + 145, my, x + 146, my + 40, borderColor);
            drawRect(x + 494, my, x + 495, my + 40, borderColor);

            this.fontRenderer.drawString(
                    module.getName(),
                    x + 160,
                    my + 16,
                    enabled ? 0xFF3880FF : 0xFFEEEEEE
            );

            int tX = x + 455;
            int tY = my + 14;
            int tWidth = 26;
            int tHeight = 12;

            drawRect(tX, tY, tX + tWidth, tY + tHeight, enabled ? 0xFF3880FF : 0xFF2D2D35);
            
            if (enabled) {
                drawRect(tX + tWidth - 10, tY - 1, tX + tWidth, tY + tHeight + 1, 0xFFFFFFFF);
            } else {
                drawRect(tX, tY - 1, tX + 10, tY + tHeight + 1, 0xFF7A7A85);
            }

            my += 48;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        int sx = x + 145;
        int sy = y + 14;
        searchFocused = (mouseX >= sx && mouseX <= sx + 340 && mouseY >= sy && mouseY <= sy + 22);

        int cy = y + 45;
        for (Category c : Category.values()) {
            if (mouseX >= x + 10 && mouseX <= x + 120 && mouseY >= cy && mouseY <= cy + 22) {
                category = c;
                return;
            }
            cy += 28;
        }

        int my = y + 50;
        for (Module module : Client.manager.getModulesByCategory(category)) {
            if (!searchQuery.isEmpty() && !module.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                continue;
            }

            if (mouseX >= x + 145 && mouseX <= x + 495 && mouseY >= my && mouseY <= my + 40) {
                module.toggle();
                return;
            }
            my += 48;
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
            }
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
