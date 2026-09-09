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

    /* =========================
       COLORS
       ========================= */

    private static final int BACKGROUND = 0xCC101216;
    private static final int PANEL = 0xF0181B20;
    private static final int PANEL_HEADER = 0xFF20242B;

    private static final int OUTLINE = 0xFF30353D;

    private static final int MODULE_OFF = 0xFF1B1F25;
    private static final int MODULE_ON = 0xFF293E52;

    private static final int SETTING_BG = 0xFF15181D;

    private static final int ACCENT = 0xFF55AAFF;
    private static final int ACCENT_DARK = 0xFF367DBA;

    private static final int TEXT = 0xFFE8E8E8;
    private static final int TEXT_SECONDARY = 0xFF9EA4AD;
    private static final int TEXT_DISABLED = 0xFF70757D;

    private static final int SLIDER_BG = 0xFF30343B;
    private static final int SLIDER_FILL = 0xFF55AAFF;

    private static final int TOOLTIP_BG = 0xF0101216;

    private static final int PANEL_WIDTH = 130;
    private static final int HEADER_HEIGHT = 22;
    private static final int MODULE_HEIGHT = 18;
    private static final int SETTING_HEIGHT = 18;

    private static final int PANEL_GAP = 8;

    private static final int MOVE_SPEED = 10;

    private final ArrayList<Panel> panels = new ArrayList<>();

    public ClickGuiScreen() {

        int x = 10;
        int y = 10;

        for (Category category : Category.values()) {

            panels.add(new Panel(category, x, y));

            x += PANEL_WIDTH + PANEL_GAP;

            // Prevent panels from going off-screen.
            if (x + PANEL_WIDTH > 800) {
                x = 10;
                y += 260;
            }
        }
    }

    /* =========================
       SCREEN
       ========================= */

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        // Dark background
        drawRect(
                0,
                0,
                this.width,
                this.height,
                BACKGROUND
        );

        for (Panel panel : panels) {
            panel.draw(mouseX, mouseY);
        }

        String description = null;

        for (int i = panels.size() - 1; i >= 0; i--) {

            String desc = panels.get(i).getHoveredDescription(mouseX, mouseY);

            if (desc != null) {
                description = desc;
                break;
            }
        }

        if (description != null) {
            drawDescription(
                    description,
                    mouseX,
                    mouseY
            );
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {

        int moveX = 0;
        int moveY = 0;

        if (Keyboard.isKeyDown(KeyboardConstants.KEY_UP)) {
            moveY -= MOVE_SPEED;
        }

        if (Keyboard.isKeyDown(KeyboardConstants.KEY_DOWN)) {
            moveY += MOVE_SPEED;
        }

        if (Keyboard.isKeyDown(KeyboardConstants.KEY_LEFT)) {
            moveX -= MOVE_SPEED;
        }

        if (Keyboard.isKeyDown(KeyboardConstants.KEY_RIGHT)) {
            moveX += MOVE_SPEED;
        }

        if (moveX != 0 || moveY != 0) {

            for (Panel panel : panels) {
                panel.x += moveX;
                panel.y += moveY;
            }
        }
    }

    /* =========================
       MOUSE
       ========================= */

    @Override
    protected void mouseClicked(
            int mouseX,
            int mouseY,
            int mouseButton
    ) throws IOException {

        for (Panel panel : panels) {
            panel.mouseClicked(
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

    @Override
    protected void mouseClickMove(
            int mouseX,
            int mouseY,
            int clickedMouseButton,
            long timeSinceLastClick
    ) {

        for (Panel panel : panels) {

            panel.mouseDragged(
                    mouseX,
                    mouseY,
                    clickedMouseButton
            );
        }

        super.mouseClickMove(
                mouseX,
                mouseY,
                clickedMouseButton,
                timeSinceLastClick
        );
    }

    @Override
    protected void keyTyped(
            char typedChar,
            int keyCode
    ) {

        if (keyCode == KeyboardConstants.KEY_ESCAPE) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /* =========================
       TOOLTIP
       ========================= */

    private void drawDescription(
            String text,
            int mouseX,
            int mouseY
    ) {

        if (text == null || text.isEmpty()) {
            return;
        }

        int padding = 5;

        int textWidth =
                fontRendererObj.getStringWidth(text);

        int boxX = mouseX + 10;
        int boxY = mouseY + 10;

        // Keep tooltip on screen.
        if (boxX + textWidth + 10 > width) {
            boxX = width - textWidth - 10;
        }

        if (boxY + 20 > height) {
            boxY = height - 20;
        }

        drawRect(
                boxX,
                boxY,
                boxX + textWidth + padding * 2,
                boxY + 18,
                TOOLTIP_BG
        );

        drawOutline(
                boxX,
                boxY,
                boxX + textWidth + padding * 2,
                boxY + 18,
                OUTLINE
        );

        fontRendererObj.drawString(
                text,
                boxX + padding,
                boxY + 5,
                TEXT
        );
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

    /* =========================
       PANEL
       ========================= */

    public class Panel {

        public Category category;

        public int x;
        public int y;

        public int width = PANEL_WIDTH;

        public boolean dragging = false;

        public int dragX;
        public int dragY;

        public boolean open = true;

        private NumberSetting draggingSlider;

        private int sliderX;
        private int sliderWidth;

        public Panel(
                Category category,
                int x,
                int y
        ) {

            this.category = category;

            this.x = x;
            this.y = y;
        }

        private boolean hasSettings(Module module) {

            return module.getSettings() != null
                    && !module.getSettings().isEmpty();
        }

        /* =========================
           DRAW
           ========================= */

        public void draw(
                int mouseX,
                int mouseY
        ) {

            if (!Mouse.isButtonDown(0)) {

                dragging = false;
                draggingSlider = null;
            }

            if (dragging) {

                x = mouseX - dragX;
                y = mouseY - dragY;
            }

            int totalHeight = getTotalHeight();

            // Panel background
            drawRect(
                    x,
                    y,
                    x + width,
                    y + totalHeight,
                    PANEL
            );

            // Header
            drawRect(
                    x,
                    y,
                    x + width,
                    y + HEADER_HEIGHT,
                    PANEL_HEADER
            );

            // Accent line
            drawRect(
                    x,
                    y,
                    x + 3,
                    y + HEADER_HEIGHT,
                    ACCENT
            );

            drawOutline(
                    x,
                    y,
                    x + width,
                    y + totalHeight,
                    OUTLINE
            );

            // Category name
            fontRendererObj.drawString(
                    category.name(),
                    x + 9,
                    y + 7,
                    TEXT
            );

            // Collapse symbol
            String symbol = open ? "-" : "+";

            fontRendererObj.drawString(
                    symbol,
                    x + width - 10,
                    y + 7,
                    TEXT_SECONDARY
            );

            if (!open) {
                return;
            }

            int offset = HEADER_HEIGHT;

            for (Module module :
                    Client.INSTANCE.manager.getModulesByCategory(category)) {

                drawModule(
                        module,
                        x,
                        y + offset,
                        mouseX,
                        mouseY
                );

                offset += MODULE_HEIGHT;

                if (module.open && hasSettings(module)) {

                    for (Setting<?> setting :
                            module.getSettings()) {

                        drawSetting(
                                setting,
                                x,
                                y + offset,
                                mouseX,
                                mouseY
                        );

                        offset += SETTING_HEIGHT;
                    }
                }
            }
        }

        /* =========================
           MODULE
           ========================= */

        private void drawModule(
                Module module,
                int x,
                int y,
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

            int background =
                    module.isEnabled()
                            ? MODULE_ON
                            : MODULE_OFF;

            if (hovered) {

                background =
                        module.isEnabled()
                                ? 0xFF304960
                                : 0xFF242930;
            }

            drawRect(
                    x,
                    y,
                    x + width,
                    y + MODULE_HEIGHT,
                    background
            );

            // Enabled indicator
            if (module.isEnabled()) {

                drawRect(
                        x,
                        y,
                        x + 3,
                        y + MODULE_HEIGHT,
                        ACCENT
                );
            }

            fontRendererObj.drawString(
                    module.getName(),
                    x + 8,
                    y + 5,
                    module.isEnabled()
                            ? TEXT
                            : TEXT_SECONDARY
            );

            // Settings indicator
            if (hasSettings(module)) {

                String symbol =
                        module.open ? "v" : ">";

                fontRendererObj.drawString(
                        symbol,
                        x + width - 11,
                        y + 5,
                        TEXT_SECONDARY
                );
            }
        }

        /* =========================
           SETTINGS
           ========================= */

        private void drawSetting(
                Setting<?> setting,
                int x,
                int y,
                int mouseX,
                int mouseY
        ) {

            drawRect(
                    x,
                    y,
                    x + width,
                    y + SETTING_HEIGHT,
                    SETTING_BG
            );

            boolean hovered =
                    isHover(
                            mouseX,
                            mouseY,
                            x,
                            y,
                            width,
                            SETTING_HEIGHT
                    );

            if (hovered) {

                drawRect(
                        x,
                        y,
                        x + width,
                        y + SETTING_HEIGHT,
                        0xFF20242A
                );
            }

            if (setting instanceof BooleanSetting) {

                BooleanSetting bs =
                        (BooleanSetting) setting;

                String value =
                        bs.getValue()
                                ? "ON"
                                : "OFF";

                int color =
                        bs.getValue()
                                ? ACCENT
                                : TEXT_DISABLED;

                fontRendererObj.drawString(
                        bs.getName(),
                        x + 8,
                        y + 5,
                        TEXT_SECONDARY
                );

                fontRendererObj.drawString(
                        value,
                        x + width - 30,
                        y + 5,
                        color
                );

                return;
            }

            if (setting instanceof ModeSetting) {

                ModeSetting ms =
                        (ModeSetting) setting;

                String value =
                        String.valueOf(ms.getValue());

                fontRendererObj.drawString(
                        ms.getName(),
                        x + 8,
                        y + 5,
                        TEXT_SECONDARY
                );

                int valueWidth =
                        fontRendererObj.getStringWidth(value);

                fontRendererObj.drawString(
                        value,
                        x + width - valueWidth - 7,
                        y + 5,
                        TEXT
                );

                return;
            }

            if (setting instanceof NumberSetting) {

                NumberSetting number =
                        (NumberSetting) setting;

                String value =
                        String.valueOf(number.getValue());

                fontRendererObj.drawString(
                        number.getName(),
                        x + 8,
                        y + 3,
                        TEXT_SECONDARY
                );

                int valueWidth =
                        fontRendererObj.getStringWidth(value);

                fontRendererObj.drawString(
                        value,
                        x + width - valueWidth - 7,
                        y + 3,
                        TEXT
                );

                int barX = x + 7;
                int barY = y + 14;
                int barWidth = width - 14;

                drawRect(
                        barX,
                        barY,
                        barX + barWidth,
                        barY + 2,
                        SLIDER_BG
                );

                double range =
                        number.getMax()
                                - number.getMin();

                double percent = 0;

                if (range != 0) {

                    percent =
                            (number.getValue()
                                    - number.getMin())
                                    / range;
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
                        (int) (
                                percent
                                        * barWidth
                        );

                drawRect(
                        barX,
                        barY,
                        barX + fill,
                        barY + 2,
                        SLIDER_FILL
                );
            }
        }

        /* =========================
           CLICK
           ========================= */

        public void mouseClicked(
                int mouseX,
                int mouseY,
                int mouseButton
        ) {

            // Header
            if (isHover(
                    mouseX,
                    mouseY,
                    x,
                    y,
                    width,
                    HEADER_HEIGHT
            )) {

                if (mouseButton == 0) {

                    dragging = true;

                    dragX = mouseX - x;
                    dragY = mouseY - y;
                }

                if (mouseButton == 1) {

                    dragging = false;
                    open = !open;
                }

                return;
            }

            if (!open) {
                return;
            }

            int offset = HEADER_HEIGHT;

            for (Module module :
                    Client.INSTANCE.manager.getModulesByCategory(category)) {

                // Module
                if (isHover(
                        mouseX,
                        mouseY,
                        x,
                        y + offset,
                        width,
                        MODULE_HEIGHT
                )) {

                    if (mouseButton == 0) {

                        module.toggle();
                    }

                    if (mouseButton == 1) {

                        module.open =
                                !module.open;
                    }

                    offset += MODULE_HEIGHT;

                    return;
                }

                offset += MODULE_HEIGHT;

                // Settings
                if (module.open && hasSettings(module)) {

                    for (Setting<?> setting :
                            module.getSettings()) {

                        if (isHover(
                                mouseX,
                                mouseY,
                                x,
                                y + offset,
                                width,
                                SETTING_HEIGHT
                        )) {

                            if (setting instanceof BooleanSetting
                                    && mouseButton == 0) {

                                ((BooleanSetting) setting).toggle();
                            }

                            if (setting instanceof ModeSetting
                                    && mouseButton == 0) {

                                ((ModeSetting) setting).cycle();
                            }

                            if (setting instanceof NumberSetting
                                    && mouseButton == 0) {

                                draggingSlider =
                                        (NumberSetting) setting;

                                sliderX = x;
                                sliderWidth = width;

                                setSliderValue(mouseX);
                            }

                            return;
                        }

                        offset += SETTING_HEIGHT;
                    }
                }
            }
        }

        /* =========================
           DRAG
           ========================= */

        public void mouseDragged(
                int mouseX,
                int mouseY,
                int mouseButton
        ) {

            if (draggingSlider != null
                    && mouseButton == 0) {

                setSliderValue(mouseX);
            }

            if (dragging
                    && mouseButton == 0) {

                x = mouseX - dragX;
                y = mouseY - dragY;
            }
        }

        /* =========================
           SLIDER
           ========================= */

        private void setSliderValue(
                int mouseX
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

        /* =========================
           HOVER DESCRIPTION
           ========================= */

        public String getHoveredDescription(
                int mouseX,
                int mouseY
        ) {

            if (!open) {
                return null;
            }

            int offset = HEADER_HEIGHT;

            for (Module module :
                    Client.INSTANCE.manager.getModulesByCategory(category)) {

                if (isHover(
                        mouseX,
                        mouseY,
                        x,
                        y + offset,
                        width,
                        MODULE_HEIGHT
                )) {

                    return module.getDescription();
                }

                offset += MODULE_HEIGHT;

                if (module.open && hasSettings(module)) {

                    for (Setting<?> setting :
                            module.getSettings()) {

                        if (isHover(
                                mouseX,
                                mouseY,
                                x,
                                y + offset,
                                width,
                                SETTING_HEIGHT
                        )) {

                            return setting.getName();
                        }

                        offset += SETTING_HEIGHT;
                    }
                }
            }

            return null;
        }

        /* =========================
           HEIGHT
           ========================= */

        private int getTotalHeight() {

            if (!open) {
                return HEADER_HEIGHT;
            }

            int total =
                    HEADER_HEIGHT;

            for (Module module :
                    Client.INSTANCE.manager.getModulesByCategory(category)) {

                total += MODULE_HEIGHT;

                if (module.open
                        && hasSettings(module)) {

                    total +=
                            module.getSettings().size()
                                    * SETTING_HEIGHT;
                }
            }

            return total;
        }

        /* =========================
           HOVER
           ========================= */

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
}
