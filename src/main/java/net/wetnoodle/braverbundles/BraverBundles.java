package net.wetnoodle.braverbundles;

import net.fabricmc.api.ModInitializer;

import net.wetnoodle.braverbundles.config.BBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BraverBundles implements ModInitializer {
    public static final String MOD_ID = "braverbundles";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        BBConfig.registerConfigs();
    }
}