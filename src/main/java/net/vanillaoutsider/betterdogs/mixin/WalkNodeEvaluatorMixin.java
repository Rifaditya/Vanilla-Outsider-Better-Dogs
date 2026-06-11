// Verified against: WalkNodeEvaluator.java (26.1.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkNodeEvaluator.class)
public abstract class WalkNodeEvaluatorMixin {

    @Shadow
    protected Mob mob;

    @Inject(method = "getPathType(Lnet/minecraft/world/level/pathfinder/PathfindingContext;III)Lnet/minecraft/world/level/pathfinder/PathType;", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onGetPathType(PathfindingContext context, int x, int y, int z, CallbackInfoReturnable<PathType> cir) {
        if (this.mob instanceof WolfExtensions ext) {
            BlockPos avoid = ext.betterdogs$getPathfindAvoidPos();
            if (avoid != null && avoid.getX() == x && avoid.getY() == y && avoid.getZ() == z) {
                cir.setReturnValue(PathType.BLOCKED);
            }
        }
    }
}
