// Verified against: Entity.java (26.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "killedEntity", at = @At("HEAD"))
    private void betterdogs$onKilledEntity(ServerLevel level, LivingEntity entity, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Wolf wolf && (Object) this instanceof WolfExtensions ext) {
            if (wolf.isTame() && ext.betterdogs$isGuardMode() && ext.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
                LivingEntity owner = wolf.getOwner();
                if (owner instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    net.vanillaoutsider.betterdogs.BetterDogs.ON_PATROL.trigger(serverPlayer);
                }
            }
        }
    }
}
