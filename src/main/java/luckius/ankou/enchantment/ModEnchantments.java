package luckius.ankou.enchantment;

import io.wispforest.owo.registration.annotations.IterationIgnored;
import luckius.ankou.enchantment.custom.PermananceEnchantment;
import luckius.ankou.enchantment.custom.SoulboundEnchantment;
import net.minecraft.enchantment.Enchantment;

public class ModEnchantments implements EnchantmentRegistryContainer {
    public static final Enchantment PERMANANCE = new PermananceEnchantment();

    @IterationIgnored
    public static final Enchantment SOULBOUND = new SoulboundEnchantment();
}
