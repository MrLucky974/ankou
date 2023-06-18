package luckius.ankou;

import net.fabricmc.api.ModInitializer;
import luckius.ankou.ModConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mod implements ModInitializer {
	public static final String MOD_ID = "ankou";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ModConfig CONFIG = ModConfig.createAndLoad();

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing \""+ MOD_ID +"\" mod!");
	}
}