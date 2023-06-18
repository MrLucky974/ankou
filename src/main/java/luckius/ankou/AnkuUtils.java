package luckius.ankou;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class AnkuUtils {
    public static final String LODESTONE_POS_KEY = "LodestonePos";
    public static final String LODESTONE_DIMENSION_KEY = "LodestoneDimension";
    public static final String LODESTONE_TRACKED_KEY = "LodestoneTracked";

    public static boolean isLodestoneDestroyed(NbtCompound nbt) {
        return nbt.contains(LODESTONE_TRACKED_KEY) && !nbt.getBoolean(LODESTONE_TRACKED_KEY);
    }

    public static boolean hasLodestone(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && (nbtCompound.contains(LODESTONE_DIMENSION_KEY) || nbtCompound.contains(LODESTONE_POS_KEY));
    }

    public static BlockPos getLodestonePosition(NbtCompound nbt) {
        return NbtHelper.toBlockPos(nbt.getCompound(LODESTONE_POS_KEY));
    }

    public static Optional<RegistryKey<World>> getLodestoneDimension(NbtCompound nbt) {
        return World.CODEC.parse(NbtOps.INSTANCE, nbt.get(LODESTONE_DIMENSION_KEY)).result();
    }

    public static void writeLodestoneNbt(RegistryKey<World> worldKey, BlockPos pos, NbtCompound nbt) {
        nbt.put(LODESTONE_POS_KEY, NbtHelper.fromBlockPos(pos));
        World.CODEC.encodeStart(NbtOps.INSTANCE, worldKey).resultOrPartial(Mod.LOGGER::error).ifPresent(nbtElement -> nbt.put(AnkuUtils.LODESTONE_DIMENSION_KEY, (NbtElement)nbtElement));
        nbt.putBoolean(LODESTONE_TRACKED_KEY, true);
    }
}
