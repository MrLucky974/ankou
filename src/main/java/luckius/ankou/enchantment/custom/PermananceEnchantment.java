package luckius.ankou.enchantment.custom;

import luckius.ankou.Mod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class PermananceEnchantment extends Enchantment {
    private static double getDespawnTimeMultiplier(int n) {
        if (n == 0) {
            return 1.0;
        } else if (n % 2 == 0) {
            return n + 0.5;
        } else {
            return n + 1;
        }
    }

    public static int getDespawnTimeInTicks(int level) {
        // minutes to seconds (x60) to ticks (x20)
        return (int) (Mod.CONFIG.defaultDespawnDelay() * getDespawnTimeMultiplier(level));
    }

    public PermananceEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.VANISHABLE, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinPower(int level) {
        return level * 25;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level) + 50;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }
}
