package com.leclowndu93150.immersive_effects.handler;

import com.leclowndu93150.immersive_effects.commands.FlameTestCommand;
import com.leclowndu93150.immersive_effects.render.LodestoneEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "immersive_effects", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class TorchTracker {
    private static final Set<BlockPos> trackedTorches = ConcurrentHashMap.newKeySet();
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;
        
        Level level = mc.level;
        
        for (BlockPos pos : trackedTorches) {
            if (!level.isLoaded(pos)) continue;
            
            BlockState state = level.getBlockState(pos);
            if (!isTorchBlock(state)) {
                continue;
            }
            
            if (FlameTestCommand.isTestTorch(pos)) {
                continue;
            }
            
            animateTorchAt(level, pos, state);
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel().isClientSide() && event.getChunk() instanceof LevelChunk chunk) {
            removeChunkTorches(chunk);
        }
    }
    
    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        trackedTorches.clear();
    }
    
    @SubscribeEvent
    public static void onPlayerChangedDimension(ClientPlayerNetworkEvent.Clone event) {
        trackedTorches.clear();
    }
    
    public static void addTorch(BlockPos pos) {
        trackedTorches.add(pos.immutable());
    }
    
    public static void removeTorch(BlockPos pos) {
        trackedTorches.remove(pos);
    }
    
    public static boolean isTracked(BlockPos pos) {
        return trackedTorches.contains(pos);
    }
    
    
    private static void removeChunkTorches(LevelChunk chunk) {
        trackedTorches.removeIf(pos -> {
            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;
            return chunkX == chunk.getPos().x && chunkZ == chunk.getPos().z;
        });
    }
    
    private static boolean isTorchBlock(BlockState state) {
        return state.is(Blocks.TORCH) || 
               state.is(Blocks.WALL_TORCH) || 
               state.is(Blocks.SOUL_TORCH) || 
               state.is(Blocks.SOUL_WALL_TORCH);
    }
    
    private static void animateTorchAt(Level level, BlockPos pos, BlockState state) {
        boolean isSoulTorch = state.is(Blocks.SOUL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH);
        
        if (state.is(Blocks.WALL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH)) {
            Direction direction = state.getValue(WallTorchBlock.FACING);
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + 0.7D;
            double d2 = (double)pos.getZ() + 0.5D;
            double d3 = 0.22D;
            double d4 = 0.27D;
            Direction direction1 = direction.getOpposite();
            
            double finalX = d0 + d4 * (double)direction1.getStepX();
            double finalY = d1 + d3;
            double finalZ = d2 + d4 * (double)direction1.getStepZ();
            
            LodestoneEffects.spawnTorchFlame(level, finalX, finalY, finalZ, isSoulTorch);
        } else {
            LodestoneEffects.spawnTorchFlame(level, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, isSoulTorch);
        }
    }
}