package com.enderdevelopment.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class TradeManager {

    public static class Trade {
        public final ItemStack result;
        public final int dirtCost;

        public Trade(ItemStack result, int dirtCost) {
            this.result = result;
            this.dirtCost = dirtCost;
        }
    }

    private final Plugin plugin;
    private final List<Trade> trades = new ArrayList<>();
    private final String inventoryTitle = "Vindivillager's Wares";

    public TradeManager(Plugin plugin) {
        this.plugin = plugin;
        buildTrades();
    }

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    private void buildTrades() {
        trades.add(new Trade(buildItem(Material.DIAMOND, "Diamond", 1), 1));
        trades.add(new Trade(buildItem(Material.NETHERITE_INGOT, "Netherite Ingot", 1), 8));
        trades.add(new Trade(buildItem(Material.EMERALD, "Emerald", 16), 2));
        trades.add(new Trade(buildEnchanted(Material.DIAMOND_SWORD, "Sharpness", 5, "Blade of the Vindicator"), 16));
        trades.add(new Trade(buildEnchanted(Material.NETHERITE_PICKAXE, "Efficiency", 5, "Dirt Digger's Pick"), 24));
        trades.add(new Trade(buildEnchanted(Material.BOW, "Power", 5, "Crossbone Bow"), 20));
        trades.add(new Trade(buildEnchanted(Material.ELYTRA, "Unbreaking", 3, "Sky Wings"), 64));
        trades.add(new Trade(buildEnchanted(Material.SHIELD, "Unbreaking", 3, "Bastion Shield"), 12));
        trades.add(new Trade(new ItemStack(Material.BEACON, 1), 32));
    }

    private ItemStack buildItem(Material material, String displayName, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName).color(NamedTextColor.AQUA));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildEnchanted(Material material, String enchantKey, int level, String displayName) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName).color(NamedTextColor.LIGHT_PURPLE));
            Enchantment enchant = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchantKey.toLowerCase()));
            if (enchant != null) {
                meta.addEnchant(enchant, level, true);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack[] getSlots() {
        ItemStack[] slots = new ItemStack[9];
        for (int i = 0; i < trades.size() && i < slots.length; i++) {
            slots[i] = trades.get(i).result.clone();
        }
        return slots;
    }

    public Trade getTradeFor(ItemStack clicked) {
        for (Trade trade : trades) {
            if (trade.result.isSimilar(clicked)) {
                return trade;
            }
        }
        return null;
    }
}
