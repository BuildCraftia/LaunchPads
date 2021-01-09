package com.ItsAZZA.LaunchPads;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class OnSignPlace implements Listener {
    @EventHandler
    public void onSignEdit(SignChangeEvent event) {
        String string = event.getLine(0);
        if (string == null) return;

        if (string.equalsIgnoreCase("[launch]")) {
            if (!event.getPlayer().hasPermission("launchpads.create")) {
                event.getPlayer().sendMessage("§cYou don't have a permission to create launch signs!");
                event.setCancelled(true);
            }
        }
    }
}
