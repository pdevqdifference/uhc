package dev.breiner.uhc.scenario;

public interface Scenario {
    String getName();

    boolean isEnabled();

    void setEnabled(boolean enabled);
}
