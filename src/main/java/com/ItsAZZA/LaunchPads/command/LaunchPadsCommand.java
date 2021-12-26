package com.ItsAZZA.LaunchPads.command;

import com.ItsAZZA.LaunchPads.LaunchPadsMain;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LaunchPadsCommand implements CommandExecutor {
    LaunchPadsMain plugin = LaunchPadsMain.instance;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (!player.hasPermission("launchpads.command")) {
            player.sendMessage("§cNo permission: launchpads.command");
            return false;
        }

        if (args.length < 1) {
            sendUsage(player);
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                LaunchPadsMain.instance.reloadConfig();
                sender.sendMessage("§6Reloaded config!");
                return true;
            case "sound":
                setSound(player, args);
                return true;
            case "toggle":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /launchpads toggle <sound|particle>");
                    return true;
                }
                String value = args[1].toLowerCase();
                switch (value) {
                    case "sound": {
                        toggle(player, "sound");
                        return true;
                    }
                    case "particle": {
                        toggle(player, "particle");
                        return true;
                    }
                    case "iterations": {
                        toggle(player, "iterations");
                    }
                    default:
                        sender.sendMessage("§cUsage: /launchpads toggle <sound|particle|iterations>");
                }
                return true;
            case "particle":
                if (Bukkit.getServer().getVersion().contains("1.8")) {
                    player.sendMessage("§cParticles are not supported in 1.8");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /launchpads particle <iterations|delay|amount> <amount>");
                    return true;
                }
                String type = args[1].toLowerCase();
                switch (type) {
                    case "delay":
                    case "amount": {
                        int amount = Integer.parseInt(args[2]);
                        setParticleValue(player, type, amount);
                        return true;
                    }
                    case "iterations": {
                        int amount = Integer.parseInt(args[2]);
                        setParticleValue(player, "iterations.amount", amount);
                        return true;
                    }
                    case "set": {
                        setParticle(player, args[2].toUpperCase());
                        return true;
                    }
                    default:
                        sender.sendMessage("§cUsage: /launchpads particle <iterations|delay|amount|set> <value>");
                }
                return true;
            default:
                sendUsage(player);
                return true;
        }
    }

    private void setSound(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§cUsage: /launchpads sound <sound> [volume=1.0] [pitch=1.0]");
            return;
        }

        String sound = args[1].toUpperCase();
        try {
            Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            return;
        }

        double volume = 1.0;
        double pitch = 1.0;

        if (args.length == 4) {
            volume = Double.parseDouble(args[2]);
            pitch = Double.parseDouble(args[3]);
        }

        player.sendMessage("§eSet sound to " + sound + " at volume " + volume + " and pitch " + pitch);
        plugin.setConfig("sound.sound", sound);
        plugin.setConfig("sound.volume", volume);
        plugin.setConfig("sound.pitch", pitch);
        plugin.saveConfig();
    }

    private void toggle(Player player, String type) {
        String path = type + ".enabled";
        boolean value = plugin.getConfig().getBoolean(path);

        plugin.setConfig(path, !value);
        plugin.saveConfig();

        if (!value) {
            player.sendMessage("§aEnabled " + type);
        } else {
            player.sendMessage("§cDisabled " + type);
        }
    }

    private void setParticleValue(Player player, String type, long amount) {
        plugin.setConfig("particle." + type, amount);
        plugin.saveConfig();
        player.sendMessage("§eSet " + type + " value to " + amount);
    }

    private void setParticle(Player player, String particle) {
        try {
            Particle.valueOf(particle);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cCouldn't find a particle for " + particle);
            return;
        }

        plugin.setConfig("particle.particle", particle);
        plugin.saveConfig();
        player.sendMessage("§eSet particle to " + particle);
    }

    private void sendUsage(Player player) {
        player.sendMessage("§ePossible subcommands:\n" +
                "§f- /launchpads reload : Reload the config\n" +
                "§f- /launchpads sound <sound> [<volume=1.0> <pitch=1.0>] : Change the sound\n" +
                "§f- /launchpads particle <iterations|delay|amount|set> <value> : Change particle\n" +
                "§f- /launchpads toggle <particle|sound|iterations> : Toggle stuff");
    }
}
