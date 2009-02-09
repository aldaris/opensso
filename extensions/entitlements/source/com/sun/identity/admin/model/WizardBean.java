package com.sun.identity.admin.model;

import java.io.Serializable;

public class WizardBean implements Serializable {
    private boolean[] active = new boolean[16];

    public WizardBean() {
        for (int i = 0; i < 16; i++) {
            active[i] = false;
        }
        active[0] = true;
    }

    /**
     * @return the expanded
     */
    public boolean[] getActive() {
        return active;
    }

    /**
     * @param expanded the expanded to set
     */
    public void setActive(boolean[] active) {
        this.active = active;
    }

}
