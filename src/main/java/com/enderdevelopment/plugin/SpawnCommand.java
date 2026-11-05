package com.enderdevelopment.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.plugin.Plugin;

public class SpawnCommand implements CommandExecutor {

    private final Plugin plugin;

    public SpawnCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can spawn a vindivillager.");
            return true;
        }

        Vindicator vindicator = player.getWorld().spawn(
                player.getLocation(),
                Vindicator.class,
                mob -> {
                    mob.setCustomName(ChatColor.RED + "Vindivillager");
                    mob.setCustomNameVisible(true);
                    mob.setPersistent(true);
                    mob.setRemoveWhenFarAway(false);
                });

        VindivillagerListener.markAsVindivillager(plugin, vindicator);
        player.sendMessage(ChatColor.GREEN + "A Vindivillager has been summoned. Right-click it to trade!");
        return true;
    }
}
