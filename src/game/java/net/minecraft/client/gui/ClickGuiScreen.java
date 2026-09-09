package net.minecraft.client.gui;

import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.Category;
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

public class ClickGuiScreen extends GuiScreen {

    /* =========================
       COLORS
       ========================= */

    private static final int BG = 0x99000000;

    private static final int WINDOW = 0xFF15171A;
    private static final int SIDEBAR = 0xFF111316;
    private static final int HEADER = 0xFF191C20;

    private static final int CARD = 0xFF1B1E22;
    private static final int CARD_HOVER = 0xFF22262B;
    private static final int CARD_ENABLED = 0xFF202932;

    private static final int BORDER = 0xFF292D33;

    private static final int ACCENT = 0xFF5AA9FF;

    private static final int TEXT = 0xFFF2F3F5;
    private static final int TEXT_SECONDARY = 0xFFAAAFB7;
    private static final int TEXT_MUTED = 0xFF686D75;

    private static final int SETTINGS = 0xFF171A1E;
    private static final int SLIDER = 0xFF353A42;

    /* =========================
       SIZE
       ========================= */

    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 325;

    private static final int SIDEBAR_WIDTH = 105;
    private static final int HEADER_HEIGHT = 36;

    private static final int CARD_HEIGHT = 38;
    private static final int CARD_GAP = 6;

    private static final int MOVE_SPEED = 8;

    /* =========================
       POSITION
       ========================= */

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

    /* =========================
       DRAW
       ========================= */

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
                BG
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

    /* =========================
       WINDOW
       ========================= */

    private void drawWindow(
            int mouseX,
            int mouseY
    ) {

        int x = windowX;
        int y = windowY;

        /* Main window */

        drawRect(
                x,
                y,
                x + WINDOW_WIDTH,
                y + WINDOW_HEIGHT,
                WINDOW
        );

        /* Sidebar */

        drawRect(
                x,
                y,
                x + SIDEBAR_WIDTH,
                y + WINDOW_HEIGHT,
                SIDEBAR
        );

        /* Header */

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

        /* Logo */

        fontRendererObj.drawString(
                "NONE",
                x + 12,
                y + 12,
                TEXT
        );

        fontRendererObj.drawString(
                "CLIENT",
                x + 12,
                y + 23,
                TEXT_MUTED
        );

        /* Header version */

        fontRendererObj.drawString(
                "1.12.2",
                x + SIDEBAR_WIDTH + 14,
                y + 13,
                TEXT_SECONDARY
        );

        drawCategories(mouseX, mouseY);

        drawModules(mouseX, mouseY);
    }

    /* =========================
       CATEGORIES
       ========================= */

    private void drawCategories(
            int mouseX,
            int mouseY
    ) {

        int x = windowX + 7;
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
                            SIDEBAR_WIDTH - 14,
                            28
                    );

            if (selected) {

                drawRect(
                        x,
                        y,
                        x + SIDEBAR_WIDTH - 14,
                        y + 28,
                        0xFF20252A
                );

                drawRect(
                        x,
                        y,
                        x + 2,
                        y + 28,
                        ACCENT
                );

            } else if (hovered) {

                drawRect(
                        x,
                        y,
                        x + SIDEBAR_WIDTH - 14,
                        y + 28,
                        0xFF191C20
                );
            }

            fontRendererObj.drawString(
                    category.name(),
                    x + 10,
                    y + 9,
                    selected
                            ? TEXT
                            : TEXT_SECONDARY
            );

            y += 32;
        }
    }

    /* =========================
       MODULES
       ========================= */

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
                        + 14;

        int y =
                windowY
                        + HEADER_HEIGHT
                        + 13;

        int availableWidth =
                WINDOW_WIDTH
                        - SIDEBAR_WIDTH
                        - 28;

        /* Title */

        fontRendererObj.drawString(
                category.name(),
                x,
                y,
                TEXT
        );

        fontRendererObj.drawString(
                "Modules",
                x,
                y + 13,
                TEXT_MUTED
        );

        y += 30;

        ArrayList<Module> modules =
                Client.INSTANCE.manager
                        .getModulesByCategory(category);

        /*
         * Two-column layout
         */

        int columnGap = 6;

        int cardWidth =
                (availableWidth - columnGap) / 2;

        int leftX = x;
        int rightX = x + cardWidth + columnGap;

        for (int i = 0; i < modules.size(); i++) {

            Module module = modules.get(i);

            int column = i % 2;
            int row = i / 2;

            int cardX =
                    column == 0
                            ? leftX
                            : rightX;

            int cardY =
                    y + row *
                            (CARD_HEIGHT + CARD_GAP);

            if (cardY + CARD_HEIGHT >
                    windowY + WINDOW_HEIGHT - 8) {

                break;
            }

            drawModuleCard(
                    module,
                    cardX,
                    cardY,
                    cardWidth,
                    mouseX,
                    mouseY
            );
        }

        /*
         * Settings panel
         */

        if (selectedModule != null) {

            drawSettings(
                    selectedModule,
                    mouseX,
                    mouseY
            );
        }
    }

    /* =========================
       MODULE CARD
       ========================= */

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

        int background = CARD;

        if (hovered) {
            background = CARD_HOVER;
        }

        if (enabled) {
            background = CARD_ENABLED;
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

        /* Name */

        fontRendererObj.drawString(
                module.getName(),
                x + 9,
                y + 9,
                enabled
                        ? TEXT
                        : TEXT_SECONDARY
        );

        /* Description */

        String description =
                module.getDescription();

        if (description != null
                && !description.equals("- - -")
                && !description.isEmpty()) {

            String shortDescription =
                    description;

            if (shortDescription.length() > 16) {
                shortDescription =
                        shortDescription.substring(0, 16)
                                + "...";
            }

            fontRendererObj.drawString(
                    shortDescription,
                    x + 9,
                    y + 23,
                    TEXT_MUTED
            );
        }

        /* Toggle indicator */

        int indicatorX =
                x + width - 15;

        int indicatorY =
                y + 15;

        if (enabled) {

            drawRect(
                    indicatorX,
                    indicatorY,
                    indicatorX + 7,
                    indicatorY + 7,
                    ACCENT
            );

        } else {

            drawOutline(
                    indicatorX,
                    indicatorY,
                    indicatorX + 7,
                    indicatorY + 7,
                    0xFF4A4F57
            );
        }
    }

    /* =========================
       SETTINGS
       ========================= */

    private void drawSettings(
            Module module,
            int mouseX,
            int mouseY
    ) {

        if (module.getSettings() == null
                || module.getSettings().isEmpty()) {

            return;
        }

        /*
         * Settings panel is a compact
         * overlay on the right side.
         */

        int width = 145;

        int x =
                windowX
                        + WINDOW_WIDTH
                        - width
                        - 7;

        int y =
                windowY
                        + HEADER_HEIGHT
                        + 7;

        drawRect(
                x,
                y,
                x + width,
                windowY + WINDOW_HEIGHT - 7,
                SETTINGS
        );

        drawOutline(
                x,
                y,
                x + width,
                windowY + WINDOW_HEIGHT - 7,
                BORDER
        );

        fontRendererObj.drawString(
                module.getName(),
                x + 9,
                y + 10,
                TEXT
        );

        fontRendererObj.drawString(
                "SETTINGS",
                x + 9,
                y + 22,
                TEXT_MUTED
        );

        y += 36;

        for (Setting<?> setting :
                module.getSettings()) {

            drawSetting(
                    setting,
                    x + 9,
                    y,
                    width - 18,
                    mouseX,
                    mouseY
            );

            y += 37;

            if (y >
                    windowY + WINDOW_HEIGHT - 30) {

                break;
            }
        }
    }

    /* =========================
       SETTING DRAW
       ========================= */

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
                        x - 3,
                        y - 3,
                        width + 6,
                        30
                );

        if (hovered) {

            drawRect(
                    x - 3,
                    y - 3,
                    x + width + 3,
                    y + 27,
                    0xFF20242A
            );
        }

        /* Boolean */

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

        /* Mode */

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

        /* Number */

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
                    SLIDER
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

    /* =========================
       CLICK
       ========================= */

    @Override
    protected void mouseClicked(
            int mouseX,
            int mouseY,
            int mouseButton
    ) throws IOException {

        /*
         * Header drag
         */

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

        /*
         * Categories
         */

        int categoryX =
                windowX + 7;

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
                    SIDEBAR_WIDTH - 14,
                    28
            )) {

                selectedCategory = i;
                selectedModule = null;

                return;
            }

            categoryY += 32;
        }

        /*
         * Modules
         */

        if (categories.isEmpty()) {
            return;
        }

        Category category =
                categories.get(selectedCategory);

        int x =
                windowX
                        + SIDEBAR_WIDTH
                        + 14;

        int y =
                windowY
                        + HEADER_HEIGHT
                        + 43;

        int availableWidth =
                WINDOW_WIDTH
                        - SIDEBAR_WIDTH
                        - 28;

        int columnGap = 6;

        int cardWidth =
                (availableWidth - columnGap) / 2;

        ArrayList<Module> modules =
                Client.INSTANCE.manager
                        .getModulesByCategory(category);

        for (int i = 0; i < modules.size(); i++) {

            Module module = modules.get(i);

            int column = i % 2;
            int row = i / 2;

            int cardX =
                    column == 0
                            ? x
                            : x + cardWidth + columnGap;

            int cardY =
                    y + row *
                            (CARD_HEIGHT + CARD_GAP);

            if (isHover(
                    mouseX,
                    mouseY,
                    cardX,
                    cardY,
                    cardWidth,
                    CARD_HEIGHT
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

    /* =========================
       SETTINGS CLICK
       ========================= */

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

        int panelWidth = 145;

        int x =
                windowX
                        + WINDOW_WIDTH
                        - panelWidth
                        - 7
                        + 9;

        int y =
                windowY
                        + HEADER_HEIGHT
                        + 43;

        int width =
                panelWidth - 18;

        for (Setting<?> setting :
                module.getSettings()) {

            if (isHover(
                    mouseX,
                    mouseY,
                    x - 3,
                    y - 3,
                    width + 6,
                    30
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

            y += 37;
        }
    }

    /* =========================
       DRAG
       ========================= */

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

            int panelWidth = 145;

            int sliderX =
                    windowX
                            + WINDOW_WIDTH
                            - panelWidth
                            - 7
                            + 9;

            int sliderWidth =
                    panelWidth - 18;

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

    /* =========================
       SLIDER
       ========================= */

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

    /* =========================
       UPDATE
       ========================= */

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

            windowX =
                    width - WINDOW_WIDTH;
        }

        if (windowY + WINDOW_HEIGHT > height) {

            windowY =
                    height - WINDOW_HEIGHT;
        }
    }

    /* =========================
       KEYBOARD
       ========================= */

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

    /* =========================
       GUI
       ========================= */

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /* =========================
       HELPERS
       ========================= */

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
