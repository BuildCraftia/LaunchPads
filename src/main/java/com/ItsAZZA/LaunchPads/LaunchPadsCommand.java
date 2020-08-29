package com.ItsAZZA.LaunchPads;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

public class LaunchPadsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if(!player.hasPermission("launchpads.command")) {
            player.sendMessage("§cNo permission: launchpads.command");
            return false;
        }

        if(args.length < 1) {
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
            case "togglesound":
                toggleSound(player);
                return true;
            default:
                sendUsage(player);
                return true;
        }
    }

    private void setSound(Player player, String[] args) {
        LaunchPadsMain plugin = LaunchPadsMain.instance;

        if(args.length < 2) {
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
        if(args.length == 4) {
                volume = Double.parseDouble(args[2]);
                pitch = Double.parseDouble(args[3]);
        }

        plugin.setConfig("sound.sound", sound);
        plugin.setConfig("sound.volume", volume);
        plugin.setConfig("sound.pitch", pitch);
        plugin.saveConfig();
    }

    private void toggleSound(Player player) {
        LaunchPadsMain plugin = LaunchPadsMain.instance;
        Configuration config = plugin.getConfig();
        boolean value = config.getBoolean("sound.enabled");
        plugin.setConfig("sound.enabled", !value);
        plugin.saveConfig();
        if (!value) {
            player.sendMessage("§aEnabled sound effect!");
        } else {
            player.sendMessage("§cDisabled sound effect!");
        }
    }

    private void sendUsage (Player player) {
        player.sendMessage("§ePossible subcommands:\n" +
                "§f- /launchpads reload : Reload the config\n" +
                "§f- /launchpads sound <sound> [<volume=1.0> <pitch=1.0>] : Change the sound\n" +
                "§f- /launchpads togglesound : Toggle the sound");
    }
}
