package com.enderdevelopment.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vindicator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class VindivillagerListener implements Listener {

    private final Plugin plugin;
    private final TradeManager trades;

    public VindivillagerListener(Plugin plugin) {
        this.plugin = plugin;
        this.trades = new TradeManager(plugin);
    }

    public static void markAsVindivillager(Plugin plugin, Vindicator vindicator) {
        vindicator.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "vindivillager"),
                org.bukkit.persistence.PersistentDataType.BYTE,
                (byte) 1);
    }

    private boolean isVindivillager(Vindicator vindicator) {
        return vindicator.getPersistentDataContainer().has(
                new NamespacedKey(plugin, "vindivillager"),
                org.bukkit.persistence.PersistentDataType.BYTE);
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Vindicator vindicator)) {
            return;
        }
        if (!isVindivillager(vindicator)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();

        // Sound like a vindicator.
        player.playSound(player.getLocation(), Sound.ENTITY_VINDICATOR_AMBIENT, 1.0f, 1.0f);

        Inventory inventory = Bukkit.createInventory(null, 9, trades.getInventoryTitle());
        inventory.addItem(trades.getSlots());
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Inventory inventory = event.getInventory();
        if (inventory == null || !inventory.getType().name().equals("CHEST")) {
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        // Only process clicks within the vindivillager trade GUI we opened.
        if (event.getView().getTitle() == null
                || !event.getView().getTitle().equals(trades.getInventoryTitle())) {
            return;
        }
        event.setCancelled(true);

        TradeManager.Trade found = trades.getTradeFor(event.getCurrentItem());
        if (found == null) {
            return;
        }

        ItemStack result = found.result;

        // Charge dirt.
        if (!removeDirt(player, found.dirtCost)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            player.sendMessage(net.kyori.adventure.text.Component.text(
                    "Not enough dirt! Come back with more."));
            return;
        }

        player.getInventory().addItem(result.clone());
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
    }

    private boolean removeDirt(Player player, int amount) {
        int available = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.DIRT) {
                available += item.getAmount();
            }
        }
        if (available < amount) {
            return false;
        }
        int remaining = amount;
        for (ItemStack item : player.getInventory().getContents()) {
            if (remaining <= 0) {
                break;
            }
            if (item != null && item.getType() == Material.DIRT) {
                int take = Math.min(item.getAmount(), remaining);
                item.setAmount(item.getAmount() - take);
                remaining -= take;
            }
        }
        return true;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Vindicator vindicator) {
            if (isVindivillager(vindicator)) {
                event.getDrops().clear();
            }
        }
    }
}
