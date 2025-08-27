package com.leclowndu93150.immersive_effects.registration;

import com.leclowndu93150.immersive_effects.ImmersiveEffects;
import com.leclowndu93150.immersive_effects.particles.EnderPearlTrailParticle;
import com.leclowndu93150.immersive_effects.particles.TorchFlameParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ImmersiveParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, ImmersiveEffects.MOD_ID);

    public static final Supplier<EnderPearlTrailParticle> ENDER_PEARL_TRAIL = PARTICLE_TYPES.register("ender_pearl_trail", EnderPearlTrailParticle::new);
    public static final Supplier<TorchFlameParticle> TORCH_FLAME = PARTICLE_TYPES.register("torch_flame", TorchFlameParticle::new);

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ENDER_PEARL_TRAIL.get(), EnderPearlTrailParticle.Factory::new);
        event.registerSpriteSet(TORCH_FLAME.get(), TorchFlameParticle.Factory::new);
    }

    private static Supplier<SimpleParticleType> registerParticle(String name) {
        return registerParticle(name, false);
    }

    private static Supplier<SimpleParticleType> registerParticle(String name, boolean alwaysShow) {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(alwaysShow));
    }
}
