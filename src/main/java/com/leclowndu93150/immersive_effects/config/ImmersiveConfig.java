package com.leclowndu93150.immersive_effects.config;

import com.leclowndu93150.immersive_effects.ImmersiveEffects;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;


@Mod.EventBusSubscriber(modid = ImmersiveEffects.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ImmersiveConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue ENDER_PEARL_TRAIL;
    private static final ForgeConfigSpec.BooleanValue HONEY_DRIP;

    static {
        BUILDER.push("general");

        ENDER_PEARL_TRAIL = BUILDER
                .comment("Enable or disable the ender pearl trail effect.")
                .define("enderPearlTrail", true);

        HONEY_DRIP = BUILDER
                .comment("Enable or disable the honey drip particles.")
                .define("honeyDrip", true);

        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean enderPearlTrail;
    public static boolean honeyDrip;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enderPearlTrail = ENDER_PEARL_TRAIL.get();
        honeyDrip = HONEY_DRIP.get();
    }

    public static boolean isEnderPearlTrailEnabled() {return enderPearlTrail;}
    public static boolean isHoneyDripEnabled() {return honeyDrip;}
}
