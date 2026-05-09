package com.jacobean.cannibalism;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacobeanCannibalism implements ModInitializer {

	public static final String MOD_ID = "jacobean-cannibalism";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModPowers.register();
		LOGGER.info("Jacobean Cannibalism mod loaded. Bon appétit.");
	}
}