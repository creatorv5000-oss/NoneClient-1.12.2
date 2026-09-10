package net.lax1dude.eaglercraft.internal;

public class MainClass {

    public static void main(String[] args) {
        // Activates the FPS optimization variables before launching the game engine
        net.lax1dude.eaglercraft.FPSOptimizer.initializeEngineOverrides();
        
        LWJGVMEntryPoint.main_(args);
    }
}
