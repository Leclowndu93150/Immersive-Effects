package com.leclowndu93150.immersive_effects;

import com.leclowndu93150.immersive_effects.config.ImmersiveConfig;
import com.leclowndu93150.immersive_effects.registration.ImmersiveParticles;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ImmersiveEffects.MODID)
public class ImmersiveEffects {

    public static final String MODID = "immersive_effects";
    public static final String MOD_ID = "immersive_effects";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ImmersiveEffects() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ImmersiveParticles.PARTICLE_TYPES.register(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ImmersiveConfig.SPEC);
    }

}

