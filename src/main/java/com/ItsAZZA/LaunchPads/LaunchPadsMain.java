package com.ItsAZZA.LaunchPads;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class LaunchPadsMain extends JavaPlugin {
    public static LaunchPadsMain instance;

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(new OnStep(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnSignPlace(), this);
        this.getCommand("launchpads").setExecutor(new LaunchPadsCommand());
        this.saveDefaultConfig();
        instance = this;
    }

    public void setConfig(String path, Object value) {
        this.getConfig().set(path, value);
    }
}
