package com.leclowndu93150.immersive_effects.commands;

import com.leclowndu93150.immersive_effects.render.LodestoneEffects;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlameTestCommand {
    private static final Set<BlockPos> TEST_TORCHES = new HashSet<>();
    private static boolean isSoulFlame = false;
    
    public static boolean isTestTorch(BlockPos pos) {
        return TEST_TORCHES.contains(pos);
    }
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("flametest")
                .then(Commands.argument("soul", BoolArgumentType.bool())
                        .executes(context -> {
                            isSoulFlame = BoolArgumentType.getBool(context, "soul");
                            return placeTorchAtLookTarget(context.getSource());
                        })
                )
                .executes(context -> {
                    isSoulFlame = false;
                    return placeTorchAtLookTarget(context.getSource());
                })
                .then(Commands.literal("clear")
                        .executes(context -> {
                            TEST_TORCHES.clear();
                            context.getSource().sendSuccess(() -> Component.literal("Cleared all test torches"), false);
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }
    
    private static int placeTorchAtLookTarget(CommandSourceStack source) {
        if (source.getEntity() == null) return 0;
        
        Vec3 eyePos = source.getEntity().getEyePosition();
        Vec3 lookVec = source.getEntity().getLookAngle();
        Vec3 targetPos = eyePos.add(lookVec.scale(5.0));
        
        ClipContext context = new ClipContext(eyePos, targetPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, source.getEntity());
        BlockHitResult result = source.getLevel().clip(context);
        
        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos targetBlock = result.getBlockPos().above();

            if (source.getLevel().getBlockState(targetBlock).isAir()) {
                BlockState torchState = isSoulFlame ? Blocks.SOUL_TORCH.defaultBlockState() : Blocks.TORCH.defaultBlockState();
                source.getLevel().setBlock(targetBlock, torchState, 3);
                TEST_TORCHES.add(targetBlock);
                
                source.sendSuccess(() -> Component.literal("Placed " + (isSoulFlame ? "soul " : "") + "torch at " + targetBlock + " (client-tick flames)"), false);
                return Command.SINGLE_SUCCESS;
            } else {
                source.sendFailure(Component.literal("Cannot place torch - block is not air"));
                return 0;
            }
        } else {
            source.sendFailure(Component.literal("No block in range to place torch on"));
            return 0;
        }
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.isPaused()) return;

        TEST_TORCHES.removeIf(pos -> {
            if (!mc.level.isLoaded(pos)) return false;
            
            BlockState state = mc.level.getBlockState(pos);
            boolean isTorch = state.is(Blocks.TORCH) || state.is(Blocks.SOUL_TORCH);
            
            if (isTorch) {
                boolean isSoul = state.is(Blocks.SOUL_TORCH);
                LodestoneEffects.spawnTorchFlame(mc.level, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, isSoul);
                return false;
            } else {
                return true;
            }
        });
    }
}