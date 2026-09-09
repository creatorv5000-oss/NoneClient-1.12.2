package net.minecraft.client.gui;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.modules.Module;
import com.isacofff.clientbase.settings.Setting;
import com.isacofff.clientbase.settings.Setting.BooleanSetting;
import com.isacofff.clientbase.settings.Setting.ModeSetting;
import com.isacofff.clientbase.settings.Setting.NumberSetting;

import net.lax1dude.eaglercraft.Keyboard;
import net.lax1dude.eaglercraft.KeyboardConstants;
import net.lax1dude.eaglercraft.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGuiScreen extends GuiScreen {private static final int BACKGROUND = 0x88000000;

private static final int WINDOW = 0xFF181A1D;
private static final int SIDEBAR = 0xFF141619;
private static final int HEADER = 0xFF1B1E22;

private static final int PANEL = 0xFF1E2125;
private static final int PANEL_HOVER = 0xFF24282D;
private static final int PANEL_ENABLED = 0xFF202A33;

private static final int BORDER = 0xFF2B2F35;

private static final int ACCENT = 0xFF6EA8E5;

private static final int TEXT = 0xFFF1F2F3;
private static final int TEXT_SECONDARY = 0xFFB0B4BA;
private static final int TEXT_MUTED = 0xFF747980;

private static final int WINDOW_WIDTH = 520;
private static final int WINDOW_HEIGHT = 340;

private static final int SIDEBAR_WIDTH = 112;
private static final int HEADER_HEIGHT = 42;

private static final int MODULE_HEIGHT = 48;
private static final int MODULE_GAP = 7;

private static final int MOVE_SPEED = 8;

private int windowX;
private int windowY;

private int selectedCategory = 0;

private boolean draggingWindow;
private int dragX;
private int dragY;

private Module selectedModule;

private NumberSetting draggingSlider;

private final ArrayList<Category> categories =
        new ArrayList<Category>();

public ClickGuiScreen() {

    for (Category category : Category.values()) {
        categories.add(category);
    }

    windowX = (width - WINDOW_WIDTH) / 2;
    windowY = (height - WINDOW_HEIGHT) / 2;

    if (windowX < 5) {
        windowX = 5;
    }

    if (windowY < 5) {
        windowY = 5;
    }
}

@Override
public void drawScreen(
        int mouseX,
        int mouseY,
        float partialTicks
) {

    drawRect(
            0,
            0,
            width,
            height,
            BACKGROUND
    );

    if (draggingWindow) {
        windowX = mouseX - dragX;
        windowY = mouseY - dragY;
    }

    drawWindow(mouseX, mouseY);

    super.drawScreen(
            mouseX,
            mouseY,
            partialTicks
    );
}

private void drawWindow(
        int mouseX,
        int mouseY
) {

    int x = windowX;
    int y = windowY;

    drawRect(
            x,
            y,
            x + WINDOW_WIDTH,
            y + WINDOW_HEIGHT,
            WINDOW
    );

    drawRect(
            x,
            y,
            x + SIDEBAR_WIDTH,
            y + WINDOW_HEIGHT,
            SIDEBAR
    );

    drawRect(
            x + SIDEBAR_WIDTH,
            y,
            x + WINDOW_WIDTH,
            y + HEADER_HEIGHT,
            HEADER
    );

    drawOutline(
            x,
            y,
            x + WINDOW_WIDTH,
            y + WINDOW_HEIGHT,
            BORDER
    );

    fontRendererObj.drawString(
            "NONE",
            x + 14,
            y + 11,
            TEXT
    );

    fontRendererObj.drawString(
            "CLIENT",
            x + 14,
            y + 23,
            TEXT_MUTED
    );

    fontRendererObj.drawString(
            "NoneClient",
            x + SIDEBAR_WIDTH + 15,
            y + 10,
            TEXT
    );

    fontRendererObj.drawString(
            "1.12.2",
            x + SIDEBAR_WIDTH + 15,
            y + 24,
            TEXT_MUTED
    );

    drawCategories(mouseX, mouseY);
    drawModules(mouseX, mouseY);
}

private void drawCategories(
        int mouseX,
        int mouseY
) {

    int x = windowX + 8;
    int y = windowY + HEADER_HEIGHT + 10;

    for (int i = 0; i < categories.size(); i++) {

        Category category = categories.get(i);

        boolean selected =
                i == selectedCategory;

        boolean hovered =
                isHover(
                        mouseX,
                        mouseY,
                        x,
                        y,
                        SIDEBAR_WIDTH - 16,
                        31
                );

        if (selected) {

            drawRect(
                    x,
                    y,
                    x + SIDEBAR_WIDTH - 16,
                    y + 31,
                    0xFF20252A
            );

            drawRect(
                    x,
                    y,
                    x + 2,
                    y + 31,
                    ACCENT
            );

        } else if (hovered) {

            drawRect(
                    x,
                    y,
                    x + SIDEBAR_WIDTH - 16,
                    y + 31,
                    0xFF1A1D21
            );
        }

        fontRendererObj.drawString(
                category.name(),
                x + 11,
                y + 10,
                selected
                        ? TEXT
                        : TEXT_SECONDARY
        );

        y += 36;
    }
}

private void drawModules(
        int mouseX,
        int mouseY
) {

    if (categories.isEmpty()) {
        return;
    }

    Category category =
            categories.get(selectedCategory);

    int x =
            windowX
                    + SIDEBAR_WIDTH
                    \+ 16;

    int y =
            windowY
                    + HEADER_HEIGHT
                    \+ 13;

    int availableWidth =
            WINDOW_WIDTH
                    - SIDEBAR_WIDTH
                    - 32;

    fontRendererObj.drawString(
            category.name(),
            x,
            y,
            TEXT
    );

    fontRendererObj.drawString(
            "Client features",
            x,
            y + 14,
            TEXT_MUTED
    );

    y += 34;

    ArrayList<Module> modules =
            Client.INSTANCE.manager
                    .getModulesByCategory(category);

    int cardWidth = availableWidth;

    for (int i = 0; i < modules.size(); i++) {

        Module module = modules.get(i);

        int cardY =
                y + i *
                        (MODULE_HEIGHT + MODULE_GAP);

        if (cardY + MODULE_HEIGHT >
                windowY + WINDOW_HEIGHT - 10) {
            break;
        }

        drawModule(
                module,
                x,
                cardY,
                cardWidth,
                mouseX,
                mouseY
        );
    }

    if (selectedModule != null) {

        drawSettings(
                selectedModule,
                mouseX,
                mouseY
        );
    }
}

private void drawModule(
        Module module,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY
) {

    boolean hovered =
            isHover(
                    mouseX,
                    mouseY,
                    x,
                    y,
                    width,
                    MODULE_HEIGHT
            );

    boolean enabled =
            module.isEnabled();

    boolean selected =
            module == selectedModule;

    int background = PANEL;

    if (hovered) {
        background = PANEL_HOVER;
    }

    if (enabled) {
        background = PANEL_ENABLED;
    }

    drawRect(
            x,
            y,
            x + width,
            y + MODULE_HEIGHT,
            background
    );

    if (selected) {

        drawRect(
                x,
                y,
                x + 2,
                y + MODULE_HEIGHT,
                ACCENT
        );
    }

    fontRendererObj.drawString(
            module.getName(),
            x + 12,
            y + 10,
            TEXT
    );

    String description =
            module.getDescription();

    if (description != null
            && !description.equals("- - -")
            && !description.isEmpty()) {

        String shortDescription =
                description;

        if (shortDescription.length() > 42) {

            shortDescription =
                    shortDescription.substring(0, 42)
                            + "...";
        }

        fontRendererObj.drawString(
                shortDescription,
                x + 12,
                y + 26,
                TEXT_MUTED
        );
    }

    int switchX =
            x + width - 32;

    int switchY =
            y + 16;

    drawRect(
            switchX,
            switchY,
            switchX + 20,
            switchY + 10,
            enabled
                    ? 0xFF526F8E
                    : 0xFF34383E
    );

    if (enabled) {

        drawRect(
                switchX + 11,
                switchY + 2,
                switchX + 18,
                switchY + 9,
                ACCENT
        );

    } else {

        drawRect(
                switchX + 2,
                switchY + 2,
                switchX + 9,
                switchY + 9,
                0xFF777C83
        );
    }
}

private void drawSettings(
        Module module,
        int mouseX,
        int mouseY
) {

    if (module.getSettings() == null
            || module.getSettings().isEmpty()) {

        return;
    }

    int panelWidth = 180;

    int x =
            windowX
                    + WINDOW_WIDTH
                    - panelWidth
                    - 8;

    int y =
            windowY
                    + HEADER_HEIGHT
                    \+ 8;

    drawRect(
            x,
            y,
            x + panelWidth,
            windowY + WINDOW_HEIGHT - 8,
            0xFF191C20
    );

    drawOutline(
            x,
            y,
            x + panelWidth,
            windowY + WINDOW_HEIGHT - 8,
            BORDER
    );

    fontRendererObj.drawString(
            module.getName(),
            x + 11,
            y + 10,
            TEXT
    );

    fontRendererObj.drawString(
            "SETTINGS",
            x + 11,
            y + 23,
            TEXT_MUTED
    );

    y += 39;

    for (Setting<?> setting :
            module.getSettings()) {

        drawSetting(
                setting,
                x + 11,
                y,
                panelWidth - 22,
                mouseX,
                mouseY
        );

        y += 39;

        if (y >
                windowY + WINDOW_HEIGHT - 32) {

            break;
        }
    }
}

private void drawSetting(
        Setting<?> setting,
        int x,
        int y,
        int width,
        int mouseX,
        int mouseY
) {

    boolean hovered =
            isHover(
                    mouseX,
                    mouseY,
                    x - 4,
                    y - 4,
                    width + 8,
                    31
            );

    if (hovered) {

        drawRect(
                x - 4,
                y - 4,
                x + width + 4,
                y + 27,
                0xFF20242A
        );
    }

    if (setting instanceof BooleanSetting) {

        BooleanSetting bs =
                (BooleanSetting) setting;

        fontRendererObj.drawString(
                bs.getName(),
                x,
                y + 7,
                TEXT_SECONDARY
        );

        String value =
                bs.getValue()
                        ? "ON"
                        : "OFF";

        int valueWidth =
                fontRendererObj
                        .getStringWidth(value);

        fontRendererObj.drawString(
                value,
                x + width - valueWidth,
                y + 7,
                bs.getValue()
                        ? ACCENT
                        : TEXT_MUTED
        );

        return;
    }

    if (setting instanceof ModeSetting) {

        ModeSetting ms =
                (ModeSetting) setting;

        fontRendererObj.drawString(
                ms.getName(),
                x,
                y + 7,
                TEXT_SECONDARY
        );

        String value =
                String.valueOf(ms.getValue());

        int valueWidth =
                fontRendererObj
                        .getStringWidth(value);

        fontRendererObj.drawString(
                value,
                x + width - valueWidth,
                y + 7,
                TEXT
        );

        return;
    }

    if (setting instanceof NumberSetting) {

        NumberSetting number =
                (NumberSetting) setting;

        fontRendererObj.drawString(
                number.getName(),
                x,
                y,
                TEXT_SECONDARY
        );

        String value =
                String.valueOf(
                        number.getValue()
                );

        int valueWidth =
                fontRendererObj
                        .getStringWidth(value);

        fontRendererObj.drawString(
                value,
                x + width - valueWidth,
                y,
                TEXT
        );

        int barY = y + 17;

        drawRect(
                x,
                barY,
                x + width,
                barY + 3,
                0xFF353A40
        );

        double range =
                number.getMax()
                        - number.getMin();

        double percent = 0;

        if (range != 0) {

            percent =
                    (
                            number.getValue()
                                    - number.getMin()
                    ) / range;
        }

        percent =
                Math.max(
                        0,
                        Math.min(
                                1,
                                percent
                        )
                );

        int fill =
                (int)
                        (percent * width);

        drawRect(
                x,
                barY,
                x + fill,
                barY + 3,
                ACCENT
        );
    }
}

@Override
protected void mouseClicked(
        int mouseX,
        int mouseY,
        int mouseButton
) throws IOException {

    if (isHover(
            mouseX,
            mouseY,
            windowX,
            windowY,
            WINDOW_WIDTH,
            HEADER_HEIGHT
    )) {

        if (mouseButton == 0) {

            draggingWindow = true;

            dragX =
                    mouseX - windowX;

            dragY =
                    mouseY - windowY;
        }

        return;
    }

    int categoryX =
            windowX + 8;

    int categoryY =
            windowY + HEADER_HEIGHT + 10;

    for (int i = 0;
         i < categories.size();
         i++) {

        if (isHover(
                mouseX,
                mouseY,
                categoryX,
                categoryY,
                SIDEBAR_WIDTH - 16,
                31
        )) {

            selectedCategory = i;
            selectedModule = null;

            return;
        }

        categoryY += 36;
    }

    if (categories.isEmpty()) {
        return;
    }

    Category category =
            categories.get(selectedCategory);

    int x =
            windowX
                    + SIDEBAR_WIDTH
                    \+ 16;

    int y =
            windowY
                    + HEADER_HEIGHT
                    \+ 47;

    int availableWidth =
            WINDOW_WIDTH
                    - SIDEBAR_WIDTH
                    - 32;

    ArrayList<Module> modules =
            Client.INSTANCE.manager
                    .getModulesByCategory(category);

    for (int i = 0; i < modules.size(); i++) {

        Module module = modules.get(i);

        int cardY =
                y + i *
                        (MODULE_HEIGHT + MODULE_GAP);

        if (cardY + MODULE_HEIGHT >
                windowY + WINDOW_HEIGHT - 10) {

            break;
        }

        if (isHover(
                mouseX,
                mouseY,
                x,
                cardY,
                availableWidth,
                MODULE_HEIGHT
        )) {

            if (mouseButton == 0) {

                module.toggle();
                selectedModule = module;

            } else if (mouseButton == 1) {

                selectedModule = module;
            }

            return;
        }
    }

    if (selectedModule != null) {

        clickSettings(
                selectedModule,
                mouseX,
                mouseY,
                mouseButton
        );
    }

    super.mouseClicked(
            mouseX,
            mouseY,
            mouseButton
    );
}

private void clickSettings(
        Module module,
        int mouseX,
        int mouseY,
        int mouseButton
) {

    if (module.getSettings() == null
            || module.getSettings().isEmpty()) {

        return;
    }

    int panelWidth = 180;

    int x =
            windowX
                    + WINDOW_WIDTH
                    - panelWidth
                    - 8
                    \+ 11;

    int y =
            windowY
                    + HEADER_HEIGHT
                    \+ 8
                    \+ 39;

    int width =
            panelWidth - 22;

    for (Setting<?> setting :
            module.getSettings()) {

        if (isHover(
                mouseX,
                mouseY,
                x - 4,
                y - 4,
                width + 8,
                31
        )) {

            if (setting instanceof BooleanSetting
                    && mouseButton == 0) {

                ((BooleanSetting) setting).toggle();
                return;
            }

            if (setting instanceof ModeSetting
                    && mouseButton == 0) {

                ((ModeSetting) setting).cycle();
                return;
            }

            if (setting instanceof NumberSetting
                    && mouseButton == 0) {

                draggingSlider =
                        (NumberSetting) setting;

                setSliderValue(
                        mouseX,
                        x,
                        width
                );

                return;
            }
        }

        y += 39;
    }
}

@Override
protected void mouseClickMove(
        int mouseX,
        int mouseY,
        int clickedMouseButton,
        long timeSinceLastClick
) {

    if (draggingWindow
            && clickedMouseButton == 0) {

        windowX =
                mouseX - dragX;

        windowY =
                mouseY - dragY;
    }

    if (draggingSlider != null
            && clickedMouseButton == 0) {

        int panelWidth = 180;

        int sliderX =
                windowX
                        + WINDOW_WIDTH
                        - panelWidth
                        - 8
                        \+ 11;

        int sliderWidth =
                panelWidth - 22;

        setSliderValue(
                mouseX,
                sliderX,
                sliderWidth
        );
    }

    super.mouseClickMove(
            mouseX,
            mouseY,
            clickedMouseButton,
            timeSinceLastClick
    );
}

private void setSliderValue(
        int mouseX,
        int sliderX,
        int sliderWidth
) {

    if (draggingSlider == null) {
        return;
    }

    double percent =
            (mouseX - sliderX)
                    / (double) sliderWidth;

    percent =
            Math.max(
                    0,
                    Math.min(
                            1,
                            percent
                    )
            );

    double rawValue =
            draggingSlider.getMin()
                    + percent *
                    (
                            draggingSlider.getMax()
                                    - draggingSlider.getMin()
                    );

    double increment =
            draggingSlider.getIncrement();

    double value;

    if (increment > 0) {

        double steps =
                (
                        rawValue
                                - draggingSlider.getMin()
                ) / increment;

        value =
                draggingSlider.getMin()
                        + Math.round(steps)
                        * increment;

    } else {

        value = rawValue;
    }

    value =
            Math.round(
                    value * 10000.0
            ) / 10000.0;

    value =
            Math.max(
                    draggingSlider.getMin(),
                    Math.min(
                            draggingSlider.getMax(),
                            value
                    )
            );

    draggingSlider.setValue(value);
}

@Override
public void updateScreen() {

    if (!Mouse.isButtonDown(0)) {

        draggingWindow = false;
        draggingSlider = null;
    }

    int moveX = 0;
    int moveY = 0;

    if (Keyboard.isKeyDown(
            KeyboardConstants.KEY_UP
    )) {
        moveY -= MOVE_SPEED;
    }

    if (Keyboard.isKeyDown(
            KeyboardConstants.KEY_DOWN
    )) {
        moveY += MOVE_SPEED;
    }

    if (Keyboard.isKeyDown(
            KeyboardConstants.KEY_LEFT
    )) {
        moveX -= MOVE_SPEED;
    }

    if (Keyboard.isKeyDown(
            KeyboardConstants.KEY_RIGHT
    )) {
        moveX += MOVE_SPEED;
    }

    windowX += moveX;
    windowY += moveY;

    if (windowX < 0) {
        windowX = 0;
    }

    if (windowY < 0) {
        windowY = 0;
    }

    if (windowX + WINDOW_WIDTH > width) {
        windowX = width - WINDOW_WIDTH;
    }

    if (windowY + WINDOW_HEIGHT > height) {
        windowY = height - WINDOW_HEIGHT;
    }
}

@Override
protected void keyTyped(
        char typedChar,
        int keyCode
) {

    if (keyCode ==
            KeyboardConstants.KEY_ESCAPE) {

        mc.displayGuiScreen(null);
    }
}

@Override
public boolean doesGuiPauseGame() {
    return false;
}

private void drawOutline(
        int x1,
        int y1,
        int x2,
        int y2,
        int color
) {

    drawRect(
            x1,
            y1,
            x2,
            y1 + 1,
            color
    );

    drawRect(
            x1,
            y2 - 1,
            x2,
            y2,
            color
    );

    drawRect(
            x1,
            y1,
            x1 + 1,
            y2,
            color
    );

    drawRect(
            x2 - 1,
            y1,
            x2,
            y2,
            color
    );
}

private boolean isHover(
        int mouseX,
        int mouseY,
        int x,
        int y,
        int width,
        int height
) {

    return mouseX >= x
            && mouseX <= x + width
            && mouseY >= y
            && mouseY <= y + height;
}
