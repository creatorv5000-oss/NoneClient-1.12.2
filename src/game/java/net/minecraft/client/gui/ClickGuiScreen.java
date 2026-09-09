package net.minecraft.client.gui;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.modules.Module;

import java.io.IOException;

public class ClickGuiScreen extends GuiScreen {

private int x = 80;
private int y = 50;
private Category category = Category.Client;

@Override
public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawRect(0, 0, width, height, 0x99000000);
    drawRect(x, y, x + 500, y + 300, 0xFF181A1D);
    drawRect(x, y, x + 120, y + 300, 0xFF111315);

    fontRendererObj.drawString("NoneClient", x + 15, y + 15, 0xFFFFFFFF);

    int cy = y + 45;

    for (Category c : Category.values()) {
        if (c == category) {
            drawRect(x + 8, cy - 3, x + 112, cy + 20, 0xFF252A30);
        }

        fontRendererObj.drawString(
                c.name(),
                x + 15,
                cy + 4,
                c == category ? 0xFF6EA8E5 : 0xFFAAAAAA
        );

        cy += 30;
    }

    int my = y + 45;

    for (Module module : Client.manager.getModulesByCategory(category)) {
        drawRect(
                x + 135,
                my,
                x + 485,
                my + 35,
                module.isEnabled() ? 0xFF222A32 : 0xFF202225
        );

        fontRendererObj.drawString(
                module.getName(),
                x + 147,
                my + 7,
                0xFFFFFFFF
        );

        if (module.isEnabled()) {
            fontRendererObj.drawString(
                    "ON",
                    x + 455,
                    my + 7,
                    0xFF6EA8E5
            );
        }

        my += 42;
    }

    super.drawScreen(mouseX, mouseY, partialTicks);
}

@Override
protected void mouseClicked(int mouseX, int mouseY, int button)
        throws IOException {

    int cy = y + 42;

    for (Category c : Category.values()) {
        if (mouseX >= x + 8 && mouseX <= x + 112 &&
                mouseY >= cy && mouseY <= cy + 25) {
            category = c;
            return;
        }

        cy += 30;
    }

    int my = y + 45;

    for (Module module : Client.manager.getModulesByCategory(category)) {
        if (mouseX >= x + 135 && mouseX <= x + 485 &&
                mouseY >= my && mouseY <= my + 35) {
            module.toggle();
            return;
        }

        my += 42;
    }

    super.mouseClicked(mouseX, mouseY, button);
}

@Override
protected void keyTyped(char typedChar, int keyCode) {
    if (keyCode == 1) {
        mc.displayGuiScreen(null);
    }
}

@Override
public boolean doesGuiPauseGame() {
    return false;
}
```

}
