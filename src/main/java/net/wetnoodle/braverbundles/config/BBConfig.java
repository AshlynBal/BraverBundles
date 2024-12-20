package net.wetnoodle.braverbundles.config;

import com.mojang.datafixers.util.Pair;
import net.wetnoodle.braverbundles.BraverBundles;

public class BBConfig {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static boolean ALLAY_WHITELISTERS;
    public static boolean DISPENSERS_USE_BUNDLES;
    public static boolean BUNDLE_SCOOPING;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(BraverBundles.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("allays-use-bundles", true), "allays holding bundles can collect any items found inside.");
        configs.addKeyValuePair(new Pair<>("dispensers-use-bundles", true), "dispensers with a bundle can drop items found inside.");
        configs.addKeyValuePair(new Pair<>("bundle-scooping", true), "clicking on an item entity with a bundle sends the item to the bundle.");
    }

    private static void assignConfigs() {
        ALLAY_WHITELISTERS = CONFIG.getOrDefault("allays-use-bundles", true);
        DISPENSERS_USE_BUNDLES = CONFIG.getOrDefault("dispensers-use-bundles", true);
        BUNDLE_SCOOPING = CONFIG.getOrDefault("bundle-scooping", true);

        System.out.println("All " + configs.getConfigsList().size() + " have been set properly");
    }
}