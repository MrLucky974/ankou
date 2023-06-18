package luckius.ankou.enchantment;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public interface EnchantmentRegistryContainer extends AutoRegistryContainer<Enchantment> {
    @Override
    default Registry<Enchantment> getRegistry() {
        return Registries.ENCHANTMENT;
    }

    @Override
    default Class<Enchantment> getTargetFieldType() {
        return Enchantment.class;
    }
}
