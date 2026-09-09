package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class ClickGui extends Module {

    public ClickGui() {
        super("ClickGui", "Opens the client ClickGUI", Category.Client);
    }

    @Override
    public void onEnable() {
        // The Minecraft keybind already opens ClickGuiScreen.
        // This module exists so ClickGui appears in the module manager.
    }

    @Override
    public void onDisable() {
    }
}
