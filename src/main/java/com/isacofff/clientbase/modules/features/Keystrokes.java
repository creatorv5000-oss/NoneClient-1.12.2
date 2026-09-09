package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class Keystrokes extends Module {

    public Keystrokes() {
        super(
                "Keystrokes",
                "Displays your movement keys on screen",
                Category.Render
        );
    }

    @Override
    public void onUpdate() {
        // HUD rendering will be added through the GUI/render hook.
    }
}
