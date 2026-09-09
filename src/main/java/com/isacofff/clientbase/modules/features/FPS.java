package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class FPS extends Module {

    public FPS() {
        super(
                "FPS",
                "Displays your current frames per second",
                Category.Render
        );
    }

    @Override
    public void onUpdate() {
        // FPS display will be connected to the HUD render hook later.
    }
}
