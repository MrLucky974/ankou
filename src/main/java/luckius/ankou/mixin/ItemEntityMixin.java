package luckius.ankou.mixin;

import luckius.ankou.enchantment.ModEnchantments;
import luckius.ankou.enchantment.custom.PermananceEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract ItemStack getStack();

    @ModifyConstant(method = "tick()V", constant = @Constant(intValue = 6000))
    public int tick(int constant) {
        int level = EnchantmentHelper.getLevel(ModEnchantments.PERMANANCE, getStack());
        return PermananceEnchantment.getDespawnTimeInTicks(level);
    }
}
