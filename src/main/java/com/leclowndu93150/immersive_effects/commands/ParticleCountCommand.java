package com.leclowndu93150.immersive_effects.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.lodestar.lodestone.systems.particle.world.LodestoneWorldParticle;

import java.util.Map;
import java.util.Queue;

public class ParticleCountCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("immersiveeffects")
                .then(Commands.literal("count")
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            int count = getClientParticleCount();
                            if (count >= 0) {
                                source.sendSuccess(() -> Component.literal("§aImmersive Effects particles currently active: §e" + count), false);
                            } else {
                                source.sendSuccess(() -> Component.literal("§cUnable to count particles"), false);
                            }
                            
                            return 1;
                        }));
        
        dispatcher.register(command);
    }

    private static int getClientParticleCount() {
        int count = 0;
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.particleEngine == null) {
                return -1;
            }

            Map<ParticleRenderType, Queue<Particle>> particles = mc.particleEngine.particles;

            for (Queue<Particle> queue : particles.values()) {
                if (queue != null) {
                    for (Particle particle : queue) {
                        if (particle instanceof LodestoneWorldParticle) {
                            count++;
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return count;
    }
}