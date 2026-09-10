package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import java.util.ArrayList;
import java.util.List;

public class CPS extends Module {

    private final List<Long> leftClicks = new ArrayList<>();
    private final List<Long> rightClicks = new ArrayList<>();

    public CPS() {
        super("CPS", "Tracks and displays your real-time clicks per second.", Category.Render);
    }

    /**
     * Call this inside your client's main left-click action or mouse click event loop.
     */
    public void registerLeftClick() {
        if (this.isEnabled()) {
            this.leftClicks.add(System.currentTimeMillis());
        }
    }

    /**
     * Call this inside your client's main right-click action or mouse click event loop.
     */
    public void registerRightClick() {
        if (this.isEnabled()) {
            this.rightClicks.add(System.currentTimeMillis());
        }
    }

    /**
     * Returns the current left clicks-per-second count.
     */
    public int getLeftCPS() {
        cleanupClicks(this.leftClicks);
        return this.leftClicks.size();
    }

    /**
     * Returns the current right clicks-per-second count.
     */
    public int getRightCPS() {
        cleanupClicks(this.rightClicks);
        return this.rightClicks.size();
    }

    /**
     * Purges logged timestamps older than 1 second to update frequency windows.
     */
    private void cleanupClicks(List<Long> clicks) {
        long timeLimit = System.currentTimeMillis() - 1000L;
        while (!clicks.isEmpty() && clicks.get(0) < timeLimit) {
            clicks.remove(0);
        }
    }
}
