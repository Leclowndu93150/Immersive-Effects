package com.leclowndu93150.immersive_effects.handler;

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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = "immersive_effects", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class TorchTracker {
    private static final Map<BlockPos, TorchData> trackedTorches = new ConcurrentHashMap<>();
    
    private static class TorchData {
        final double x, y, z;
        final boolean isSoulTorch;
        long lastValidated = 0;
        
        TorchData(double x, double y, double z, boolean isSoulTorch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.isSoulTorch = isSoulTorch;
            this.lastValidated = System.currentTimeMillis();
        }
    }
    
    private static long lastValidationCheck = 0;
    private static final long VALIDATION_INTERVAL = 1000;
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;
        
        Level level = mc.level;
        long currentTime = System.currentTimeMillis();
        boolean shouldValidate = currentTime - lastValidationCheck > VALIDATION_INTERVAL;
        
        Iterator<Map.Entry<BlockPos, TorchData>> iterator = trackedTorches.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, TorchData> entry = iterator.next();
            BlockPos pos = entry.getKey();
            TorchData data = entry.getValue();
            
            if (!level.isLoaded(pos)) continue;

            if (shouldValidate && currentTime - data.lastValidated > VALIDATION_INTERVAL) {
                BlockState state = level.getBlockState(pos);
                if (!isTorchBlock(state)) {
                    iterator.remove();
                    continue;
                }
                data.lastValidated = currentTime;
            }

            LodestoneEffects.spawnTorchFlame(level, data.x, data.y, data.z, data.isSoulTorch);
        }
        
        if (shouldValidate) {
            lastValidationCheck = currentTime;
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
    
    public static void addTorch(BlockPos pos, Level level) {
        BlockPos immutablePos = pos.immutable();
        if (trackedTorches.containsKey(immutablePos)) return;
        
        BlockState state = level.getBlockState(pos);
        if (!isTorchBlock(state)) return;
        
        boolean isSoulTorch = state.is(Blocks.SOUL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH);
        double x, y, z;
        
        if (state.is(Blocks.WALL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH)) {
            Direction direction = state.getValue(WallTorchBlock.FACING);
            double d0 = (double)pos.getX() + 0.5D;
            double d1 = (double)pos.getY() + 0.7D;
            double d2 = (double)pos.getZ() + 0.5D;
            double d3 = 0.22D;
            double d4 = 0.27D;
            Direction direction1 = direction.getOpposite();
            
            x = d0 + d4 * (double)direction1.getStepX();
            y = d1 + d3;
            z = d2 + d4 * (double)direction1.getStepZ();
        } else {
            x = pos.getX() + 0.5;
            y = pos.getY() + 0.7;
            z = pos.getZ() + 0.5;
        }
        
        trackedTorches.put(immutablePos, new TorchData(x, y, z, isSoulTorch));
    }
    
    public static void removeTorch(BlockPos pos) {
        trackedTorches.remove(pos);
    }
    
    public static boolean isTracked(BlockPos pos) {
        return trackedTorches.containsKey(pos);
    }
    
    
    private static void removeChunkTorches(LevelChunk chunk) {
        trackedTorches.keySet().removeIf(pos -> {
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
}