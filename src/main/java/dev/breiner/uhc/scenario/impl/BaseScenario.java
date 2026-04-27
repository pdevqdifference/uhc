package dev.breiner.uhc.scenario.impl;

import dev.breiner.uhc.scenario.Scenario;

public abstract class BaseScenario implements Scenario {
    private boolean enabled;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
