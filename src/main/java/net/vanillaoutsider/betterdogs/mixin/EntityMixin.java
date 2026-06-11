// Verified against: Entity.java (26.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onPush(Entity entity, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self.level().isClientSide()) {
            return;
        }

        if (self instanceof Wolf thisWolf && (Object) thisWolf instanceof WolfExtensions thisExt && thisWolf.isTame() &&
            entity instanceof Wolf otherWolf && (Object) otherWolf instanceof WolfExtensions otherExt && otherWolf.isTame()) {
            
            boolean thisSitting = thisWolf.isOrderedToSit();
            boolean thisGuarding = thisExt.betterdogs$isGuardMode();
            double xa = otherWolf.getX() - thisWolf.getX();
            double za = otherWolf.getZ() - thisWolf.getZ();
            boolean thisNearDanger = false;
            
            if (net.dasik.social.api.gamerule.DynamicGameRuleManager.getBoolean(thisWolf.level(), BetterDogsGameRules.BD_CLIFF_SAFETY)) {
                thisNearDanger = net.vanillaoutsider.betterdogs.util.WolfSafetyHelper.isDangerousPushDirection(thisWolf, -xa, -za);
            }
            boolean thisImmune = thisSitting || thisGuarding || thisNearDanger;

            boolean otherSitting = otherWolf.isOrderedToSit();
            boolean otherGuarding = otherExt.betterdogs$isGuardMode();
            boolean otherNearDanger = false;
            
            if (net.dasik.social.api.gamerule.DynamicGameRuleManager.getBoolean(otherWolf.level(), BetterDogsGameRules.BD_CLIFF_SAFETY)) {
                otherNearDanger = net.vanillaoutsider.betterdogs.util.WolfSafetyHelper.isDangerousPushDirection(otherWolf, xa, za);
            }
            boolean otherImmune = otherSitting || otherGuarding || otherNearDanger;

            if (thisImmune || otherImmune) {
                if (!thisImmune) {
                    thisWolf.getNavigation().stop();
                    thisExt.betterdogs$setPushWaitTimer(60);
                }
                if (!otherImmune) {
                    otherWolf.getNavigation().stop();
                    otherExt.betterdogs$setPushWaitTimer(60);
                }
                ci.cancel();
            }
        }
    }

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
