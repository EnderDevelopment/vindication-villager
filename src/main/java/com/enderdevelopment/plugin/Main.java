package com.enderdevelopment.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("vindivillager").setExecutor(new SpawnCommand(this));
        Bukkit.getPluginManager().registerEvents(new VindivillagerListener(this), this);
        getLogger().info("Vindivillager enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Vindivillager disabled.");
    }
}
