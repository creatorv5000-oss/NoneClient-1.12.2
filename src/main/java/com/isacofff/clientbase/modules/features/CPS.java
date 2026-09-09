package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import java.util.ArrayList;
import java.util.List;

public class CPS extends Module {

    protected final Minecraft mc = Minecraft.getMinecraft();
    private final List<Long> clicks = new ArrayList<>();
    private boolean wasPressed = false;

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
            return;
        }

        boolean isPressed = Mouse.isButtonDown(0);
        if (isPressed && !wasPressed) {
            clicks.add(System.currentTimeMillis());
        }
        wasPressed = isPressed;

        long time = System.currentTimeMillis();
        clicks.removeIf(click -> time - click > 1000);
    }

    public int getCPS() {
        return clicks.size();
    }
}
