package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class ViaVersion extends Module {

    private int activeProtocol = 340; // Default 1.12.2 Protocol

    public ViaVersion() {
        super("ViaVersion", "Manages network protocol handshake versions.", Category.Render);
    }

    /**
     * Intercepts connection checks to spoof the protocol.
     * Common values: 1.16.5 = 754, 1.20.1 = 763, 1.21 = 767
  */
    public int getClientProtocol(int originalProtocol) {
        if (!this.isEnabled()) {
            return originalProtocol;
        }
        return this.activeProtocol;
    }

    public void setProtocolVersion(String versionStr) {
        if (versionStr.equals("1.21")) {
            this.activeProtocol = 767;
        } else if (versionStr.equals("1.20")) {
            this.activeProtocol = 763;
        } else {
            this.activeProtocol = 340;
        }
    }
}
