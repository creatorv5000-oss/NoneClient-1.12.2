package net.minecraft.client.gui;

import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.modules.Module;
import com.isacofff.clientbase.settings.Setting;
import com.isacofff.clientbase.settings.Setting.BooleanSetting;
import com.isacofff.clientbase.settings.Setting.ModeSetting;
import com.isacofff.clientbase.settings.Setting.NumberSetting;
import com.isacofff.clientbase.Category;

import net.lax1dude.eaglercraft.Keyboard;
import net.lax1dude.eaglercraft.KeyboardConstants;
import net.lax1dude.eaglercraft.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGuiScreen extends GuiScreen {

    /*
     * ============================================================
     * NONECLIENT - CLEAN / FEATHER-INSPIRED CLICK GUI
     * ============================================================
     */

    private static final int BG = 0xF20D0F12;

    private static final int WINDOW = 0xFF17191D;
    private static final int SIDEBAR = 0xFF121417;
    private static final int TOPBAR = 0xFF1B1E23;

    private static final int CARD = 0xFF1D2025;
    private static final int CARD_HOVER = 0xFF252930;
    private static final int CARD_ENABLED = 0xFF222A33;

    private static final int BORDER = 0xFF2B2F36;

    private static final int ACCENT = 0xFF5AA9FF;
    private static final int ACCENT_DARK = 0xFF397FC4;

    private static final int TEXT = 0xFFF1F3F5;
    private static final int TEXT_SECONDARY = 0xFFA5AAB2;
    private static final int TEXT_MUTED = 0xFF6F747D;

    private static final int SETTING_BG = 0xFF191C21;
    private static final int SLIDER_BG = 0xFF343941;

    private static final int WINDOW_WIDTH = 620;
    private static final int WINDOW_HEIGHT = 390;

    private static final int SIDEBAR_WIDTH = 125;
    private static final int TOPBAR_HEIGHT = 42;

    private static final int CARD_HEIGHT = 42;
    private static final int CARD_GAP = 7;

    private static final int MOVE_SPEED = 8;

    private int windowX;
    private int windowY;

    private int selectedCategory = 0;

    private boolean draggingWindow = false;
    private int dragX;
    private int dragY;

    private Module selectedModule;

    private NumberSetting draggingSlider;

    private final ArrayList<Category> categories =
            new ArrayList<>();

    public ClickGuiScreen() {

        for (Category category : Category.values()) {
            categories.add(category);
        }

        windowX = (this.width - WINDOW_WIDTH) / 2;
        windowY = (this.height - WINDOW_HEIGHT) / 2;

        if (windowX < 10) {
            windowX = 10;
        }

        if (windowY < 10) {
            windowY = 10;
        }
    }

    /*
     * ============================================================
     * DRAW
     * ============================================================
     */

    @Override
    public void drawScreen(
            int mouseX,
            int mouseY,
            float partialTicks
    ) {

        drawRect(
                0,
                0,
                this.width,
                this.height,
                BG
        );

        if (draggingWindow) {

            windowX =
                    mouseX - dragX;

            windowY =
                    mouseY - dragY;
        }

        drawMainWindow(
                mouseX,
                mouseY
        );

        super.drawScreen(
                mouseX,
                mouseY,
                partialTicks
        );
    }

    /*
     * ============================================================
     * MAIN WINDOW
     * ============================================================
     */

    private void drawMainWindow(
            int mouseX,
            int mouseY
    ) {

        int x = windowX;
        int y = windowY;

        /*
         * Main background
         */

        drawRect(
                x,
                y,
                x + WINDOW_WIDTH,
                y + WINDOW_HEIGHT,
                WINDOW
        );

        /*
         * Sidebar
         */

        drawRect(
                x,
                y,
                x + SIDEBAR_WIDTH,
                y + WINDOW_HEIGHT,
                SIDEBAR
        );

        /*
         * Top bar
         */

        drawRect(
                x + SIDEBAR_WIDTH,
                y,
                x + WINDOW_WIDTH,
                y + TOPBAR_HEIGHT,
                TOPBAR
        );

        drawOutline(
                x,
                y,
                x + WINDOW_WIDTH,
                y + WINDOW_HEIGHT,
                BORDER
        );

        /*
         * Logo
         */

        fontRendererObj.drawString(
                "NONECLIENT",
                x + 16,
                y + 15,
                TEXT
        );

        fontRendererObj.drawString(
                "1.12.2",
                x + SIDEBAR_WIDTH + 15,
                y + 15,
                TEXT_SECONDARY
        );

        /*
         * Sidebar categories
         */

        drawCategories(
                mouseX,
                mouseY
        );

        /*
         * Main content
         */

        drawModules(
                mouseX,
                mouseY
        );

        /*
         * Selected module settings
         */

        if (selectedModule != null) {

            drawSettings(
                    selectedModule,
                    mouseX,
                    mouseY
            );
        }
    }

    /*
     * ============================================================
     * CATEGORIES
     * ============================================================
     */

    private void drawCategories(
            int mouseX,
            int mouseY
    ) {

        int x =
                windowX + 8;

        int y =
                windowY + TOPBAR_HEIGHT + 12;

        for (int i = 0; i < categories.size(); i++) {

            Category category =
                    categories.get(i);

            boolean selected =
                    i == selectedCategory;

            boolean hovered =
                    isHover(
                            mouseX,
                            mouseY,
                            x,
                            y,
                            SIDEBAR_WIDTH - 16,
                            30
                    );

            if (selected) {

                drawRect(
                        x,
                        y,
                        x + SIDEBAR_WIDTH - 16,
                        y + 30,
                        0xFF24282E
                );

                drawRect(
                        x,
                        y,
                        x + 3,
                        y + 30,
                        ACCENT
                );

            } else if (hovered) {

                drawRect(
                        x,
                        y,
                        x + SIDEBAR_WIDTH - 16,
                        y + 30,
                        0xFF1C2025
                );
            }

            fontRendererObj.drawString(
                    category.name(),
                    x + 12,
                    y + 10,
                    selected
                            ? TEXT
                            : TEXT_SECONDARY
            );

            y += 34;
        }
    }

    /*
     * ============================================================
     * MODULES
     * ============================================================
     */

    private void drawModules(
            int mouseX,
            int mouseY
    ) {

        if (categories.isEmpty()) {
            return;
        }

        Category category =
                categories.get(selectedCategory);

        int contentX =
                windowX + SIDEBAR_WIDTH + 15;

        int contentY =
                windowY + TOPBAR_HEIGHT + 15;

        int contentWidth =
                WINDOW_WIDTH
                        - SIDEBAR_WIDTH
                        - 30;

        /*
         * Header
         */

        fontRendererObj.drawString(
                category.name(),
                contentX,
                contentY,
                TEXT
        );

        fontRendererObj.drawString(
                "Modules",
                contentX,
                contentY + 15,
                TEXT_MUTED
        );

        contentY += 32;

        ArrayList<Module> modules =
                Client.INSTANCE.manager
                        .getModulesByCategory(category);

        for (Module module : modules) {

            drawModuleCard(
                    module,
                    contentX,
                    contentY,
                    contentWidth,
                    mouseX,
                    mouseY
            );

            contentY +=
                    CARD_HEIGHT + CARD_GAP;

            /*
             * Don't draw outside the window.
             */

            if (contentY >
                    windowY + WINDOW_HEIGHT - 20) {

                break;
            }
        }
    }

    /*
     * ============================================================
     * MODULE CARD
     * ============================================================
     */

    private void drawModuleCard(
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
                        CARD_HEIGHT
                );

        boolean enabled =
                module.isEnabled();

        boolean selected =
                module == selectedModule;

        int background =
                CARD;

        if (hovered) {
            background =
                    CARD_HOVER;
        }

        if (enabled) {
            background =
                    CARD_ENABLED;
        }

        drawRect(
                x,
                y,
                x + width,
                y + CARD_HEIGHT,
                background
        );

        if (selected) {

            drawRect(
                    x,
                    y,
                    x + 2,
                    y + CARD_HEIGHT,
                    ACCENT
            );
        }

        /*
         * Module name
         */

        fontRendererObj.drawString(
                module.getName(),
                x + 13,
                y + 10,
                enabled
                        ? TEXT
                        : TEXT_SECONDARY
        );

        /*
         * Description
         */

        String description =
                module.getDescription();

        if (description != null
                && !description.isEmpty()) {

            fontRendererObj.drawString(
                    description,
                    x + 13,
                    y + 25,
                    TEXT_MUTED
            );
        }

        /*
         * Small enable indicator
         */

        int indicatorX =
                x + width - 20;

        int indicatorY =
                y + 15;

        if (enabled) {

            drawRect(
                    indicatorX,
                    indicatorY,
                    indicatorX + 8,
                    indicatorY + 8,
                    ACCENT
            );

        } else {

            drawOutline(
                    indicatorX,
                    indicatorY,
                    indicatorX + 8,
                    indicatorY + 8,
                    0xFF50555D
            );
        }
    }

    /*
     * ============================================================
     * SETTINGS PANEL
     * ============================================================
     */

    private void drawSettings(
            Module module,
            int mouseX,
            int mouseY
    ) {

        if (module.getSettings() == null
                || module.getSettings().isEmpty()) {

            return;
        }

        int x =
                windowX
                        + SIDEBAR_WIDTH
                        + 205;

        int y =
                windowY + TOPBAR_HEIGHT + 15;

        int width =
                WINDOW_WIDTH
                        - SIDEBAR_WIDTH
                        - 220;

        /*
         * Settings background
         */

        drawRect(
                x - 8,
                y - 8,
                x + width + 8,
                windowY + WINDOW_HEIGHT - 10,
                SETTING_BG
        );

        fontRendererObj.drawString(
                module.getName(),
                x,
                y,
                TEXT
        );

        fontRendererObj.drawString(
                "Settings",
                x,
                y + 15,
                TEXT_MUTED
        );

        y += 35;

        for (Setting<?> setting :
                module.getSettings()) {

            drawSetting(
                    setting,
                    x,
                    y,
                    width,
                    mouseX,
                    mouseY
            );

            y += 38;

            if (y >
                    windowY + WINDOW_HEIGHT - 35) {

                break;
            }
        }
    }

    /*
     * ============================================================
     * SETTINGS
     * ============================================================
     */

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
                        x,
                        y,
                        width,
                        32
                );

        if (hovered) {

            drawRect(
                    x - 4,
                    y - 2,
                    x + width + 4,
                    y + 32,
                    0xFF20242A
            );
        }

        /*
         * Boolean
         */

        if (setting instanceof BooleanSetting) {

            BooleanSetting bs =
                    (BooleanSetting) setting;

            fontRendererObj.drawString(
                    bs.getName(),
                    x,
                    y + 10,
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
                    y + 10,
                    bs.getValue()
                            ? ACCENT
                            : TEXT_MUTED
            );

            return;
        }

        /*
         * Mode
         */

        if (setting instanceof ModeSetting) {

            ModeSetting ms =
                    (ModeSetting) setting;

            fontRendererObj.drawString(
                    ms.getName(),
                    x,
                    y + 10,
                    TEXT_SECONDARY
            );

            String value =
                    String.valueOf(
                            ms.getValue()
                    );

            int valueWidth =
                    fontRendererObj
                            .getStringWidth(value);

            fontRendererObj.drawString(
                    value,
                    x + width - valueWidth,
                    y + 10,
                    TEXT
            );

            return;
        }

        /*
         * Number
         */

        if (setting instanceof NumberSetting) {

            NumberSetting number =
                    (NumberSetting) setting;

            fontRendererObj.drawString(
                    number.getName(),
                    x,
                    y + 2,
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
                    y + 2,
                    TEXT
            );

            int barX =
                    x;

            int barY =
                    y + 22;

            int barWidth =
                    width;

            drawRect(
                    barX,
                    barY,
                    barX + barWidth,
                    barY + 3,
                    SLIDER_BG
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
                            (
                                    percent
                                            * barWidth
                            );

            drawRect(
                    barX,
                    barY,
                    barX + fill,
                    barY + 3,
                    ACCENT
            );
        }
    }

    /*
     * ============================================================
     * MOUSE
     * ============================================================
     */

    @Override
    protected void mouseClicked(
            int mouseX,
            int mouseY,
            int mouseButton
    ) throws IOException {

        /*
         * Window title dragging
         */

        if (isHover(
                mouseX,
                mouseY,
                windowX,
                windowY,
                WINDOW_WIDTH,
                TOPBAR_HEIGHT
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

        /*
         * Categories
         */

        int categoryX =
                windowX + 8;

        int categoryY =
                windowY + TOPBAR_HEIGHT + 12;

        for (int i = 0;
             i < categories.size();
             i++) {

            if (isHover(
                    mouseX,
                    mouseY,
                    categoryX,
                    categoryY,
                    SIDEBAR_WIDTH - 16,
                    30
            )) {

                selectedCategory = i;

                selectedModule = null;

                return;
            }

            categoryY += 34;
        }

        /*
         * Modules
         */

        if (categories.isEmpty()) {
            return;
        }

        Category category =
                categories.get(selectedCategory);

        int contentX =
                windowX + SIDEBAR_WIDTH + 15;

        int contentY =
                windowY + TOPBAR_HEIGHT + 47;

        int contentWidth =
                WINDOW_WIDTH
                        - SIDEBAR_WIDTH
                        - 30;

        ArrayList<Module> modules =
                Client.INSTANCE.manager
                        .getModulesByCategory(category);

        for (Module module : modules) {

            if (isHover(
                    mouseX,
                    mouseY,
                    contentX,
                    contentY,
                    contentWidth,
                    CARD_HEIGHT
            )) {

                if (mouseButton == 0) {

                    module.toggle();

                    selectedModule =
                            module;
                }

                if (mouseButton == 1) {

                    selectedModule =
                            module;
                }

                return;
            }

            contentY +=
                    CARD_HEIGHT + CARD_GAP;
        }

        /*
         * Settings
         */

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

    /*
     * ============================================================
     * SETTINGS CLICK
     * ============================================================
     */

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

        int x =
                windowX
                        + SIDEBAR_WIDTH
                        + 205;

        int y =
                windowY
                        + TOPBAR_HEIGHT
                        + 50;

        int width =
                WINDOW_WIDTH
                        - SIDEBAR_WIDTH
                        - 220;

        for (Setting<?> setting :
                module.getSettings()) {

            if (isHover(
                    mouseX,
                    mouseY,
                    x - 4,
                    y - 2,
                    width + 8,
                    32
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

            y += 38;
        }
    }

    /*
     * ============================================================
     * DRAG
     * ============================================================
     */

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

            int sliderX =
                    windowX
                            + SIDEBAR_WIDTH
                            + 205;

            int sliderWidth =
                    WINDOW_WIDTH
                            - SIDEBAR_WIDTH
                            - 220;

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

    /*
     * ============================================================
     * SLIDER
     * ============================================================
     */

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
                        + percent
                        * (
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

    /*
     * ============================================================
     * UPDATE
     * ============================================================
     */

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

        /*
         * Keep window on screen.
         */

        if (windowX < 0) {
            windowX = 0;
        }

        if (windowY < 0) {
            windowY = 0;
        }

        if (windowX + WINDOW_WIDTH > width) {

            windowX =
                    width - WINDOW_WIDTH;
        }

        if (windowY + WINDOW_HEIGHT > height) {

            windowY =
                    height - WINDOW_HEIGHT;
        }
    }

    /*
     * ============================================================
     * KEYBOARD
     * ============================================================
     */

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

    /*
     * ============================================================
     * GUI
     * ============================================================
     */

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /*
     * ============================================================
     * HELPERS
     * ============================================================
     */

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
}
