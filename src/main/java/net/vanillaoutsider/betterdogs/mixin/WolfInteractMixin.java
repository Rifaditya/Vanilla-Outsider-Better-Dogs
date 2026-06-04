// Verified against: Wolf.java (26.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.util.WolfDebugLogger;
import net.vanillaoutsider.betterdogs.util.WolfParticleHandler;
import net.vanillaoutsider.betterdogs.util.WolfStatManager;
import net.vanillaoutsider.betterdogs.util.WolfInteractionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to handle player interactions, taming side-effects, and taming particles.
 */
@Mixin(Wolf.class)
public abstract class WolfInteractMixin extends TamableAnimal {

    protected WolfInteractMixin() {
        super(null, null);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        ItemStack itemStack = player.getItemInHand(hand);
        InteractionResult result = WolfInteractionHelper.handleMobInteract(wolf, player, hand, itemStack);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }

    @Inject(method = "applyTamingSideEffects", at = @At("HEAD"))
    private void betterdogs$onApplyTamingSideEffects(CallbackInfo ci) {
        if (!this.isTame() || this.level().isClientSide())
            return;

        Wolf wolf = (Wolf) (Object) this;
        if (wolf instanceof WolfExtensions ext) {
            if (!ext.betterdogs$hasPersonality()) {
                WolfPersonality personality = WolfPersonality.random(this.level());
                ext.betterdogs$setPersonality(personality);
                WolfDebugLogger.log(wolf, "Tame", "Assigned initial personality: " + personality.name());
            }

            WolfPersonality personality = ext.betterdogs$getPersonality();
            WolfStatManager.applyPersonalityStats(wolf, personality);
            // statsApplied flag will be set/re-applied in WolfMixin's tick handler if needed, 
            // but we also apply immediately here for responsiveness.

            betterdogs$playTameParticles(personality);
        }
    }

    @Override
    public void spawnTamingParticles(boolean success) {
        if (success) {
            for (int i = 0; i < 3; i++) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5,
                        this.getRandomZ(1.0), d, e, f);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.SMOKE, this.getRandomX(1.0), this.getRandomY() + 0.5,
                        this.getRandomZ(1.0), d, e, f);
            }
        }
    }

    @Unique
    private void betterdogs$playTameParticles(WolfPersonality personality) {
        WolfParticleHandler.playTameParticles((Wolf) (Object) this, personality);
    }
}
