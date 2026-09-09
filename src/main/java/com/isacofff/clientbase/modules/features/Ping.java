package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class Ping extends Module {

    public Ping() {
        super(
                "Ping",
                "Displays your current server ping",
                Category.Render
        );
    }

    @Override
    public void onUpdate() {
        // Ping display will be connected to the HUD render hook later.
    }
}
