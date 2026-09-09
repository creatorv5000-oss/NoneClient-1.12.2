package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CPS extends Module {

    protected final Minecraft mc = Minecraft.getMinecraft();
    private final List<Long> clicks = new ArrayList<>();
    private boolean listening = false;

    private final AWTEventListener mouseListener = event -> {
        if (event.getID() == MouseEvent.MOUSE_PRESSED) {
            MouseEvent mouseEvent = (MouseEvent) event;
            if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                synchronized (clicks) {
                    clicks.add(System.currentTimeMillis());
                }
            }
        }
    };

    public CPS() {
        super(
            "CPS",
            "Displays your clicks per second",
            Category.Render
        );
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            if (listening) {
                Toolkit.getDefaultToolkit().removeAWTEventListener(mouseListener);
                listening = false;
            }
            return;
        }

        if (!listening) {
            Toolkit.getDefaultToolkit().addAWTEventListener(mouseListener, 16L);
            listening = true;
        }

        long time = System.currentTimeMillis();
        synchronized (clicks) {
            clicks.removeIf(click -> time - click > 1000);
        }
    }

    public int getCPS() {
        synchronized (clicks) {
            return clicks.size();
        }
    }
}
