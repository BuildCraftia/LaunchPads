package com.ItsAZZA.LaunchPads.events;

import com.ItsAZZA.LaunchPads.LaunchCache;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class OnPlayerTakeDamage implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (LaunchCache.check(event.getEntity().getUniqueId())) return;
        event.setCancelled(true);
    }
}
