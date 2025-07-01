package fun.noah.server.onlyup.shop;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.registry.Registry;

import java.util.Map;

public class PerkShop {

    public static void openPerkShop(Player player, Map<String, Integer> coinsMap) {
        Inventory inv = new Inventory(InventoryType.CHEST_2_ROW, "Perk Shop");

        ItemStack jumpBoost = ItemStack.builder(Material.RABBIT_FOOT)
                .customName(Component.text("§aJump Boost (15s) §8- §f25 Coins")).build();

        inv.setItemStack(0, jumpBoost);

        inv.addInventoryCondition((p, slot, clickType, result) -> {
            if (slot == 0) {
                int coins = coinsMap.getOrDefault(p.getUuid().toString(), 0);
                if (coins >= 25) {
                    PotionEffect potionEffect = PotionEffect.JUMP_BOOST;
                    int duration = 15 * 20;
                    byte amplifier = 1;
                    Potion potion = new Potion(potionEffect, amplifier, duration);
                    p.addEffect(potion);
                    coinsMap.put(p.getUuid().toString(), coins - 25);
                } else {
                    p.sendMessage(Component.text("§cNicht genügend Coins!"));
                }
            }
        });

        player.openInventory(inv);
    }

}
