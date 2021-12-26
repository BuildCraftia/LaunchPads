package com.ItsAZZA.LaunchPads;

import com.ItsAZZA.LaunchPads.command.LaunchPadsCommand;
import com.ItsAZZA.LaunchPads.events.OnPlayerTakeDamage;
import com.ItsAZZA.LaunchPads.events.OnSignPlace;
import com.ItsAZZA.LaunchPads.events.OnStep;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class LaunchPadsMain extends JavaPlugin {
    public static LaunchPadsMain instance;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(new OnStep(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnSignPlace(), this);
        if (getConfig().getBoolean("falldamage.prevent")) {
            Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerTakeDamage(), this);
        }
        this.getCommand("launchpads").setExecutor(new LaunchPadsCommand());
        new Metrics(this, 10774);
    }

    public void setConfig(String path, Object value) {
        this.getConfig().set(path, value);
    }
}
