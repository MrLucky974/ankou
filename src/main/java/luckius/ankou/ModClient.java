package luckius.ankou;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;

public class ModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Mod.LOGGER.info("Initializing client!");
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		ModelPredicateProviderRegistry.register(Items.TOTEM_OF_UNDYING, new Identifier("dimension"), (itemStack, clientWorld, livingEntity, i) -> {
			if (livingEntity == null) {
				return 0.0F;
			}

			if (AnkuUtils.hasLodestone(itemStack)) {
				NbtCompound nbt = itemStack.getNbt();

				Optional<RegistryKey<World>> optional;
				if ((optional = AnkuUtils.getLodestoneDimension(nbt)).isPresent()) {
					RegistryKey<World> worldRegistryKey = optional.get();

					if (World.OVERWORLD.equals(worldRegistryKey)) {
						return 0.0F;
					} else if (World.NETHER.equals(worldRegistryKey)) {
						return 0.1F;
					} else if (World.END.equals(worldRegistryKey)) {
						return 0.2F;
					}
				}
			}

			return 0.0F;
		});
	}
}