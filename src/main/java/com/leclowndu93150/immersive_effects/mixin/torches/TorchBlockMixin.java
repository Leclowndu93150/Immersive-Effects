package com.leclowndu93150.immersive_effects.mixin.torches;

import com.leclowndu93150.immersive_effects.commands.FlameTestCommand;
import com.leclowndu93150.immersive_effects.render.LodestoneEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
            if (FlameTestCommand.isTestTorch(pos)) {
                ci.cancel();
                return;
            }

            System.out.println("Animating torch at " + pos.immutable());
            
            boolean isSoulTorch = state.is(Blocks.SOUL_TORCH) || state.is(Blocks.SOUL_WALL_TORCH);
            LodestoneEffects.spawnTorchFlame(level, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, isSoulTorch);
        }
        ci.cancel();
    }
}
