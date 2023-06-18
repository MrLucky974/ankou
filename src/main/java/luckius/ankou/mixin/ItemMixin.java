package luckius.ankou.mixin;

import luckius.ankou.AnkuUtils;
import luckius.ankou.Mod;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At("HEAD"), method = "appendTooltip")
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (Mod.CONFIG.isLinkable() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
            tooltip.add(AnkuUtils.hasLodestone(stack) ? Text.translatable("item.ankou.totem_of_undying.linked") : Text.translatable("item.ankou.totem_of_undying.linkable"));

            if (context.isAdvanced()) {
                if (AnkuUtils.hasLodestone(stack)) {
                    NbtCompound nbt = stack.getNbt();

                    if (AnkuUtils.isLodestoneDestroyed(nbt))
                        return;

                    BlockPos blockPos = AnkuUtils.getLodestonePosition(nbt);
                    Optional<RegistryKey<World>> optional;

                    tooltip.add(Text.translatable("item.ankou.totem_of_undying.linked_position", blockPos.getX(), blockPos.getY(), blockPos.getZ()));

                    if ((optional = AnkuUtils.getLodestoneDimension(nbt)).isPresent())
                        tooltip.add(Text.translatable("item.ankou.totem_of_undying.linked_dimension", optional.get().getValue()));
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = context.getStack();

        if (Mod.CONFIG.isLinkable() && itemStack.getItem() == Items.TOTEM_OF_UNDYING) {
            BlockPos blockPos = context.getBlockPos();
            World world = context.getWorld();

            if (world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) {
                world.playSound(null, blockPos, SoundEvents.ITEM_LODESTONE_COMPASS_LOCK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                PlayerEntity playerEntity = context.getPlayer();

                ItemStack itemStack2 = new ItemStack(Items.TOTEM_OF_UNDYING, 1);
                NbtCompound nbtCompound = itemStack.hasNbt() ? itemStack.getNbt().copy() : new NbtCompound();
                itemStack2.setNbt(nbtCompound);
                if (!playerEntity.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
                AnkuUtils.writeLodestoneNbt(world.getRegistryKey(), blockPos, nbtCompound);
                if (!playerEntity.getInventory().insertStack(itemStack2)) {
                    playerEntity.dropItem(itemStack2, false);
                }

                cir.setReturnValue(ActionResult.success(world.isClient));
            }
        }

        cir.setReturnValue(ActionResult.PASS);
    }

    @Inject(at = @At("HEAD"), method = "inventoryTick")
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
            if (Mod.CONFIG.isLinkable()) {
                if (!world.isClient) {
                    if (AnkuUtils.hasLodestone(stack)) {
                        NbtCompound nbtCompound = stack.getOrCreateNbt();
                        if (AnkuUtils.isLodestoneDestroyed(nbtCompound)) {
                            return;
                        }

                        Optional<RegistryKey<World>> optional = AnkuUtils.getLodestoneDimension(nbtCompound);
                        if (optional.isPresent() && optional.get() == world.getRegistryKey() && nbtCompound.contains(AnkuUtils.LODESTONE_POS_KEY)) {
                            BlockPos blockPos = NbtHelper.toBlockPos(nbtCompound.getCompound(AnkuUtils.LODESTONE_POS_KEY));
                            if (!world.isInBuildLimit(blockPos) || !((ServerWorld) world).getPointOfInterestStorage().hasTypeAt(PointOfInterestTypes.LODESTONE, blockPos)) {
                                Mod.LOGGER.info("Lodestone has been destroyed");
                                nbtCompound.putBoolean(AnkuUtils.LODESTONE_TRACKED_KEY, false);
                                nbtCompound.remove(AnkuUtils.LODESTONE_POS_KEY);
                            }
                        }
                    }
                }
            } else {
                if (AnkuUtils.hasLodestone(stack)) {
                    NbtCompound nbtCompound = stack.getOrCreateNbt();

                    nbtCompound.remove(AnkuUtils.LODESTONE_TRACKED_KEY);
                    nbtCompound.remove(AnkuUtils.LODESTONE_POS_KEY);
                    nbtCompound.remove(AnkuUtils.LODESTONE_DIMENSION_KEY);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "hasGlint", cancellable = true)
    public void hasGlint(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (Mod.CONFIG.isLinkable() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
            NbtCompound nbt = stack.getNbt();
            cir.setReturnValue(AnkuUtils.hasLodestone(stack) && !AnkuUtils.isLodestoneDestroyed(nbt));
        }
    }
}
