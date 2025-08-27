package com.leclowndu93150.immersive_effects.render;

import com.leclowndu93150.immersive_effects.registration.ImmersiveParticles;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.systems.easing.Easing;
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder;
import team.lodestar.lodestone.systems.particle.data.GenericParticleData;
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData;
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData;
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType;
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions;

import java.awt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LodestoneEffects {
    private static final Logger LOGGER = LoggerFactory.getLogger(LodestoneEffects.class);

    public static void createEnderPearlTrailEffect(ThrownEnderpearl enderPearl, float deltaTime) {
        float posX = (float) Mth.lerp(deltaTime, enderPearl.xo, enderPearl.getX());
        float posY = (float) Mth.lerp(deltaTime, enderPearl.yo, enderPearl.getY());
        float posZ = (float) Mth.lerp(deltaTime, enderPearl.zo, enderPearl.getZ());

        Vec3 motion = enderPearl.getDeltaMovement();
        if (motion.length() > 0.05) {
            spawnEnderPearlTrailParticles(enderPearl, posX, posY, posZ, motion);
        }
    }

    private static void spawnEnderPearlTrailParticles(ThrownEnderpearl enderPearl, float x, float y, float z, Vec3 velocity) {
        for (int i = 0; i < 3; i++) {
            double trailDistance = (i + 1) * 0.4;
            double trailX = x - velocity.x * trailDistance;
            double trailY = y - velocity.y * trailDistance;
            double trailZ = z - velocity.z * trailDistance;

            float trailScale = 0.15f - i * 0.03f;
            float alpha = 0.8f - i * 0.15f;

            WorldParticleBuilder.create(new WorldParticleOptions(ImmersiveParticles.ENDER_PEARL_TRAIL.get()))
                    .setSpinData(SpinParticleData.create((float) (enderPearl.level().random.nextGaussian() * 0.2f)).build())
                    .setScaleData(GenericParticleData.create(trailScale, 0f).setEasing(Easing.EXPO_OUT).build())
                    .setTransparencyData(GenericParticleData.create(alpha, 0f).setEasing(Easing.QUAD_OUT).build())
                    .setColorData(
                            ColorParticleData.create(new Color(0x22CFFF), new Color(0x8A2BE2))
                                    .setEasing(Easing.SINE_IN_OUT)
                                    .build()
                    )
                    .enableNoClip()
                    .setLifetime(12 + enderPearl.level().random.nextInt(8))
                    .spawn(
                            enderPearl.level(),
                            trailX + enderPearl.level().random.nextGaussian() * 0.08,
                            trailY + (enderPearl.getBbHeight() * 0.5) + enderPearl.level().random.nextGaussian() * 0.08,
                            trailZ + enderPearl.level().random.nextGaussian() * 0.08
                    );
        }
    }

    public static void spawnTorchFlame(Level level, double x, double y, double z, boolean isSoulTorch) {
        
        Color hotColor = isSoulTorch ? new Color(0xFFFFFF) : new Color(0xFFFF99);
        Color warmColor = isSoulTorch ? new Color(0x00BFFF) : new Color(0xFFAA00);
        Color coolColor = isSoulTorch ? new Color(0x4169E1) : new Color(0xFF4400);

        double yOffset = y - 0.1875;

        if (level.random.nextFloat() < 0.3f) {
            float angle = level.random.nextFloat() * 6.28f;
            float distance = level.random.nextFloat() * 0.06f;
            float offsetX = (float) Math.cos(angle) * distance;
            float offsetZ = (float) Math.sin(angle) * distance;
            float offsetY = level.random.nextFloat() * 0.02f;

            float scale = 0.16f + level.random.nextFloat() * 0.08f;
            int lifetime = 25 + level.random.nextInt(10);

            WorldParticleBuilder.create(new WorldParticleOptions(ImmersiveParticles.TORCH_FLAME.get()))
                    .setRenderType(LodestoneWorldParticleRenderType.ADDITIVE)
                    .setScaleData(GenericParticleData.create(scale, scale * 0.8f)
                            .setEasing(Easing.SINE_OUT)
                            .build())
                    .setTransparencyData(GenericParticleData.create(0.9f, 0.3f)
                            .setEasing(Easing.QUAD_OUT)
                            .build())
                    .setColorData(ColorParticleData.create(warmColor, coolColor)
                            .setCoefficient(1.2f)
                            .setEasing(Easing.QUAD_OUT)
                            .build())
                    .setLifetime(lifetime)
                    .addMotion(offsetX * 0.005f, 0.015f + level.random.nextFloat() * 0.008f, offsetZ * 0.005f)
                    .addTickActor(particle -> {
                        double currentY = particle.getYMotion();
                        if (particle.getAge() > 5 && currentY < 0.03f) {
                            particle.setParticleSpeed(particle.getXMotion() * 0.98, currentY * 1.02, particle.getZMotion() * 0.98);
                        }
                    })
                    .enableNoClip()
                    .spawn(level, x + offsetX, yOffset + offsetY, z + offsetZ);
        }

        if (level.random.nextFloat() < 0.2f) {
            float angle = level.random.nextFloat() * 6.28f;
            float distance = level.random.nextFloat() * 0.04f;
            float offsetX = (float) Math.cos(angle) * distance;
            float offsetZ = (float) Math.sin(angle) * distance;

            float scale = 0.08f + level.random.nextFloat() * 0.04f;
            int lifetime = 15 + level.random.nextInt(8);

            WorldParticleBuilder.create(new WorldParticleOptions(ImmersiveParticles.TORCH_FLAME.get()))
                    .setRenderType(LodestoneWorldParticleRenderType.ADDITIVE)
                    .setScaleData(GenericParticleData.create(scale, 0f)
                            .setEasing(Easing.EXPO_OUT)
                            .build())
                    .setTransparencyData(GenericParticleData.create(0.6f, 0f)
                            .setEasing(Easing.EXPO_OUT)
                            .build())
                    .setColorData(ColorParticleData.create(coolColor, coolColor)
                            .build())
                    .setLifetime(lifetime)
                    .addMotion(offsetX * 0.003f, 0.02f + level.random.nextFloat() * 0.01f, offsetZ * 0.003f)
                    .enableNoClip()
                    .spawn(level, x + offsetX, yOffset + 0.2f, z + offsetZ);
        }

        if (level.random.nextFloat() < 0.15f) {
            float emberX = (level.random.nextFloat() - 0.5f) * 0.1f;
            float emberZ = (level.random.nextFloat() - 0.5f) * 0.1f;

            WorldParticleBuilder.create(new WorldParticleOptions(ImmersiveParticles.TORCH_FLAME.get()))
                    .setRenderType(LodestoneWorldParticleRenderType.ADDITIVE)
                    .setScaleData(GenericParticleData.create(0.12f, 0f)
                            .setEasing(Easing.EXPO_OUT)
                            .build())
                    .setTransparencyData(GenericParticleData.create(1f, 0f)
                            .setEasing(Easing.EXPO_OUT)
                            .build())
                    .setColorData(ColorParticleData.create(hotColor, coolColor)
                            .setEasing(Easing.LINEAR)
                            .build())
                    .setLifetime(40 + level.random.nextInt(20))
                    .addMotion(emberX * 0.005f, 0.02f, emberZ * 0.005f)
                    .enableNoClip()
                    .spawn(level, x, yOffset + 0.1f, z);
        }
    }
}