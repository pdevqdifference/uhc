package dev.breiner.uhc.scenario;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.*;

public final class ScenarioManager {
    private final Plugin plugin;
    private final Map<String, Scenario> scenariosByKey = new LinkedHashMap<>();

    public ScenarioManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(Scenario scenario) {
        String key = scenario.getName().toLowerCase(Locale.ROOT);
        scenariosByKey.put(key, scenario);
    }

    public Collection<Scenario> getAll() {
        return Collections.unmodifiableCollection(scenariosByKey.values());
    }

    public Scenario get(String name) {
        if (name == null) return null;
        return scenariosByKey.get(name.toLowerCase(Locale.ROOT));
    }

    public boolean enable(String name) {
        Scenario scenario = get(name);
        if (scenario == null) return false;
        if (scenario.isEnabled()) return true;
        scenario.setEnabled(true);
        if (scenario instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) scenario, plugin);
        }
        return true;
    }

    public boolean disable(String name) {
        Scenario scenario = get(name);
        if (scenario == null) return false;
        if (!scenario.isEnabled()) return true;
        scenario.setEnabled(false);
        if (scenario instanceof Listener) {
            HandlerList.unregisterAll((Listener) scenario);
        }
        return true;
    }

    public void disableAll() {
        for (Scenario scenario : scenariosByKey.values()) {
            if (scenario.isEnabled()) {
                scenario.setEnabled(false);
                if (scenario instanceof Listener) {
                    HandlerList.unregisterAll((Listener) scenario);
                }
            }
        }
    }
}
