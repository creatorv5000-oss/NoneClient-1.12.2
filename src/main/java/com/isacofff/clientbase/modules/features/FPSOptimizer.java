package net.lax1dude.eaglercraft;

public class FPSOptimizer {

    private static boolean isOptimized = false;
    private static long lastFlushTime = System.currentTimeMillis();

    // Static blocks run automatically as soon as the class is loaded by the game engine
    static {
        initializeEngineOverrides();
    }

    public static void initializeEngineOverrides() {
        if (isOptimized) return;
        
        // Force high performance rendering paths
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("forge.forceNoStencil", "true");
        
        isOptimized = true;
    }

    public static void processRenderTick() {
        long currentTime = System.currentTimeMillis();
        // Automatically flush the memory cache every 5 seconds to prevent drops
        if (currentTime - lastFlushTime > 5000) {
            System.gc(); 
            lastFlushTime = currentTime;
        }
    }
}
