package net.lax1dude.eaglercraft.internal;

public class MainClass {

    public static void main(String[] args) {
        // 1. Activates your custom 100+ FPS optimizer systems first
        net.lax1dude.eaglercraft.FPSOptimizer.initializeEngineOverrides();
        
        // 2. Safely forces the 1.12.2 client to look up and pull down the 26.1.2 backport pack properties
        System.setProperty("eaglercraft.defaultResourcePack", "https://allorigins.win");
        
        LWJGVMEntryPoint.main_(args);
    }
}
