package com.leclowndu93150.immersive_effects.mixin.torches;

import com.leclowndu93150.immersive_effects.render.LodestoneEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WallTorchBlock.class)
public class WallTorchBlockMixin {

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (level.isClientSide()) {
            boolean isSoulTorch = state.is(Blocks.SOUL_WALL_TORCH);

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
        }
        ci.cancel();
    }
}