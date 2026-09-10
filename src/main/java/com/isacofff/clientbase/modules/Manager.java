package com.isacofff.clientbase.modules;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.features.ArmorHUD;
import com.isacofff.clientbase.modules.features.CPS;
import com.isacofff.clientbase.modules.features.ClickGui;
import com.isacofff.clientbase.modules.features.Coordinates;
import com.isacofff.clientbase.modules.features.ExampleModule;
import com.isacofff.clientbase.modules.features.FPS;
import com.isacofff.clientbase.modules.features.FullBright;
import com.isacofff.clientbase.modules.features.Hitboxes;
import com.isacofff.clientbase.modules.features.Keystrokes;
import com.isacofff.clientbase.modules.features.Ping;
import com.isacofff.clientbase.modules.features.ToggleSprint;
import com.isacofff.clientbase.modules.features.ViaBlocks;
import com.isacofff.clientbase.modules.features.ViaVersion;
import com.isacofff.clientbase.modules.features.Zoom;

import java.util.ArrayList;

public class Manager {

    public final ArrayList<Module> modules = new ArrayList<>();
    
    //Add the modules here for them to appear in the ClickGui
    public void init() {
        modules.add(new ClickGui());
        modules.add(new FullBright());
        modules.add(new ExampleModule());
        
        // Active PvP Client Mod Layout
        modules.add(new ArmorHUD());
        modules.add(new CPS());
        modules.add(new Coordinates());
        modules.add(new FPS());
        modules.add(new Hitboxes());
        modules.add(new Keystrokes());
        modules.add(new Ping());
        modules.add(new ToggleSprint());
        modules.add(new Zoom());

        // Multi-Protocol 1.21 Feature Suite (ViaItems removed)
        modules.add(new ViaVersion());
        modules.add(new ViaBlocks());
    }

    public void onTick() {
        for (Module m : modules) {
            if (m.isEnabled()) {
                m.onUpdate();
            }
        }
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public <T extends Module> T getModule(Class<T> classs) {
        for (Module m : modules) {
            if (classs.isInstance(m)) return classs.cast(m);
        }
        return null;
    }

    public Module getModuleByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    public ArrayList<Module> getModulesByCategory(Category c) {
        ArrayList<Module> list = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory() == c) list.add(m);
        }
        return list;
    }
}
