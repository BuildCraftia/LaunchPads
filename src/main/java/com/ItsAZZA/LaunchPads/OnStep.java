package com.ItsAZZA.LaunchPads;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

class OnStep implements Listener {

    @EventHandler
    public void onStep(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        if (!block.getType().name().contains("_PLATE")) return;

        LaunchPadsMain plugin = LaunchPadsMain.instance;
        Configuration config = plugin.getConfig();
        int blockLocation = config.getInt("signYOffset", -2);

        Block dataBlock = block.getRelative(0, blockLocation, 0);
        if (!(dataBlock.getState() instanceof Sign)) return;

        Sign sign = (Sign) dataBlock.getState();
        String label = sign.getLine(0);
        if (!label.equalsIgnoreCase("[launch]")) return;

        Player player = event.getPlayer();

        Double x = getDoubleOrNull(sign.getLine(1));
        Double y = getDoubleOrNull(sign.getLine(2));
        Double z = getDoubleOrNull(sign.getLine(3));

        if (x == null || y == null || z == null) {
            player.sendMessage("§cError! Please check the number values on lines 2-4");
            return;
        }

        boolean soundsEnabled = config.getBoolean("sound.enabled");

        String sound = config.getString("sound.sound");
        Sound bukkitSound = getBukkitSoundOrNull(sound);

        if (bukkitSound == null) {
            player.sendMessage("§cError: Could not find sound for " + sound);
            return;
        }

        float volume = (float) config.getDouble("sound.volume");
        float pitch = (float) config.getDouble("sound.pitch");

        Vector velocity = player.getVelocity();
        velocity.setX(x);
        velocity.setY(y);
        velocity.setZ(z);

        // Fix a problem pre-1.13 not launching players upwards...
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setVelocity(velocity);
            }
        }.runTaskLater(plugin, 1L);

        long delay = config.getLong("particle.delay");

        if (soundsEnabled && sound != null) {
            player.playSound(player.getLocation(), bukkitSound, volume, pitch);
        }

        if (config.getBoolean("particle.enabled")) {
            if (Bukkit.getVersion().contains("1.8")) return; // No particles for 1.8 :(

            String particleString = config.getString("particle.particle");
            int amount = config.getInt("particle.amount");

            Particle particle;

            try {
                particle = Particle.valueOf(particleString);
            } catch (IllegalArgumentException e) {
                player.sendMessage("§cError: Could not find particle for " + particleString);
                return;
            }

            // If particle requires data, we're just returning for now (future feature?)
            if (particle.getDataType() != Void.class) return;

            if (config.getBoolean("particle.iterations.enabled")) {
                new BukkitRunnable() {
                    private int i = 0;
                    private final int iterations = config.getInt("particle.iterations.amount");

                    @Override
                    public void run() {
                        i++;
                        Location location = player.getLocation();
                        World world = location.getWorld();
                        assert world != null;
                        world.spawnParticle(particle, location, amount);

                        if (i >= iterations) {
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 1L, delay);
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Vector velocity = player.getVelocity();
                        if (velocity.getY() > 0) {
                            Location location = player.getLocation();
                            World world = location.getWorld();
                            assert world != null;
                            world.spawnParticle(particle, location, amount);
                        } else {
                            cancel();
                        }
                    }
                }.runTaskTimer(plugin, 1L, delay);
            }
        }
    }

    private Double getDoubleOrNull(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private Sound getBukkitSoundOrNull(String string) {
        try {
            return Sound.valueOf(string);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}