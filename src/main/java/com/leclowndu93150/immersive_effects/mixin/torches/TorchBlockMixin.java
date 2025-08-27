package com.leclowndu93150.immersive_effects.mixin.torches;

import com.leclowndu93150.immersive_effects.handler.TorchTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TorchBlock.class)
public class TorchBlockMixin {

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (level.isClientSide()) {
            BlockPos immutablePos = pos.immutable();
            if (!TorchTracker.isTracked(immutablePos)) {
                TorchTracker.addTorch(immutablePos, level);
            }
        }
        ci.cancel();
    }
}
