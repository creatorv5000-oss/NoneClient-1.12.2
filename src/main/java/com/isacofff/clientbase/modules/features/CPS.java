package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class CPS extends Module {

    public CPS() {
        super(
                "CPS",
                "Displays your clicks per second",
                Category.Render
        );
    }

    @Override
    public void onUpdate() {
        // CPS tracking and HUD rendering will be connected later.
    }
}
