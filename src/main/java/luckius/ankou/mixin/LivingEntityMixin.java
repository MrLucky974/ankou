package luckius.ankou.mixin;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import luckius.ankou.AnkuUtils;
import luckius.ankou.Mod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/*
Code inspired by : https://github.com/HyperPigeon/MoreTotems/blob/master/src/main/java/net/hyper_pigeon/moretotems/mixin/LivingEntityMixin.java
*/

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public final native boolean addStatusEffect(StatusEffectInstance statusEffectInstance);

    @Shadow
    public native boolean clearStatusEffects();

    @Shadow
    public native void setHealth(float health);

    @Inject(at = @At("HEAD"), method = "tryUseTotem", cancellable = true)
    private void tryUseTotem(DamageSource damageSource, CallbackInfoReturnable<Boolean> callback) {
        if (damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            callback.setReturnValue(false);
        }

        Entity entity = this;

        ItemStack itemStack = null;
        for (Hand hand : Hand.values()) {
            ItemStack itemStack2 = ((LivingEntity)entity).getStackInHand(hand);
            if (!itemStack2.isOf(Items.TOTEM_OF_UNDYING)) continue;
            itemStack = itemStack2.copy();
            itemStack2.decrement(1);
            break;
        }

        if (itemStack != null) {
            this.setHealth(1.0F);
            this.clearStatusEffects();
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 125, 2));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 350, 4));
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2));
            if (Mod.CONFIG.totemEffects.fireProtection() && damageSource.isIn(DamageTypeTags.IS_FIRE)) {
                this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 250, 0));
            }

            if (Mod.CONFIG.isLinkable() && AnkuUtils.hasLodestone(itemStack)) {
                NbtCompound nbt = itemStack.getNbt();

                if (AnkuUtils.isLodestoneDestroyed(nbt)) {
                    this.sendMessage(Text.translatable("item.ankou.totem_of_undying.lodestone_destroyed"));
                } else {
                    BlockPos blockPos = AnkuUtils.getLodestonePosition(nbt);

                    Optional<RegistryKey<World>> optional;
                    if ((optional = AnkuUtils.getLodestoneDimension(nbt)).isPresent()) {
                        ServerWorld world = getServer().getWorld(optional.get());
                        float yaw = entity.getYaw();
                        float pitch = entity.getPitch();
                        Vec3d velocity = entity.getVelocity();
                        Vec3d position = blockPos.add(0, 2, 0).toCenterPos();

                        FabricDimensions.teleport(entity, world, new TeleportTarget(position, Vec3d.ZERO, yaw, pitch));
                    }
                }
            }

            this.getWorld().sendEntityStatus(this, EntityStatuses.USE_TOTEM_OF_UNDYING);
        }

        callback.setReturnValue(itemStack != null);
    }
}
