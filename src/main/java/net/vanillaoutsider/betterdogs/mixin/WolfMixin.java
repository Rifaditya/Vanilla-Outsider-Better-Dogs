// Verified against: Wolf.java (26.1.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfCombatHooks;
import net.vanillaoutsider.betterdogs.util.WolfDebugLogger;
import net.vanillaoutsider.betterdogs.util.WolfStatManager;
import net.minecraft.core.particles.TrailParticleOption;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for Wolf entity to add personality system and core survival enhancements.
 * Uses Fabric Data Attachment API for persistence.
 */
@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal implements WolfExtensions {

    @Unique
    private int betterdogs$healTimer = 0;

    @Unique
    private boolean betterdogs$statsApplied = false;

    protected WolfMixin() {
        super(null, null);
    }

    // ========== WolfExtensions Implementation (delegates to persistence) ==========

    @Override
    public WolfPersonality betterdogs$getPersonality() {
        return WolfPersistentData.getPersistedPersonality((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setPersonality(WolfPersonality personality) {
        WolfPersistentData.setPersistedPersonality((Wolf) (Object) this, personality);
    }

    @Override
    public boolean betterdogs$hasPersonality() {
        return WolfPersistentData.hasPersistedPersonality((Wolf) (Object) this);
    }

    @Override
    public int betterdogs$getLastDamageTime() {
        return WolfPersistentData.getPersistedLastDamageTime((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setLastDamageTime(int time) {
        WolfPersistentData.setPersistedLastDamageTime((Wolf) (Object) this, time);
    }

    @Override
    public boolean betterdogs$isSubmissive() {
        return WolfPersistentData.isPersistedSubmissive((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setSubmissive(boolean submissive) {
        WolfPersistentData.setPersistedSubmissive((Wolf) (Object) this, submissive);
    }

    @Override
    public String betterdogs$getBloodFeudTarget() {
        return WolfPersistentData.getPersistedBloodFeudTarget((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setBloodFeudTarget(String targetUuid) {
        WolfPersistentData.setPersistedBloodFeudTarget((Wolf) (Object) this, targetUuid);
    }

    @Override
    public boolean betterdogs$hasBloodFeud() {
        return WolfPersistentData.hasBloodFeud((Wolf) (Object) this);
    }

    @Override
    public long betterdogs$getLastMischiefDay() {
        return WolfPersistentData.getPersistedLastMischiefDay((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setLastMischiefDay(long day) {
        WolfPersistentData.setPersistedLastMischiefDay((Wolf) (Object) this, day);
    }

    @Override
    public boolean betterdogs$isAdoptable() {
        return WolfPersistentData.isPersistedAdoptable((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setAdoptable(boolean adoptable) {
        WolfPersistentData.setPersistedAdoptable((Wolf) (Object) this, adoptable);
    }

    // ========== Range Stats (v4.3.1) ==========

    @Override
    public float betterdogs$getHealthBonus() {
        return WolfPersistentData.getPersistedHealthBonus((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setHealthBonus(float hp) {
        WolfPersistentData.setPersistedHealthBonus((Wolf) (Object) this, hp);
    }

    @Override
    public float betterdogs$getDamageMod() {
        return WolfPersistentData.getPersistedDamageMod((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setDamageMod(float dmg) {
        WolfPersistentData.setPersistedDamageMod((Wolf) (Object) this, dmg);
    }

    @Override
    public float betterdogs$getSpeedMod() {
        return WolfPersistentData.getPersistedSpeedMod((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setSpeedMod(float speed) {
        WolfPersistentData.setPersistedSpeedMod((Wolf) (Object) this, speed);
    }

    @Override
    public boolean betterdogs$areStatsRolled() {
        return WolfPersistentData.arePersistedStatsRolled((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setStatsRolled(boolean rolled) {
        WolfPersistentData.setPersistedStatsRolled((Wolf) (Object) this, rolled);
    }

    // ========== Dunce Cap (Transient Disciplinary State) ==========

    @Unique
    private boolean betterdogs$isBeingDisciplined = false;

    @Override
    public boolean betterdogs$isBeingDisciplined() {
        return this.betterdogs$isBeingDisciplined;
    }

    @Override
    public void betterdogs$setBeingDisciplined(boolean isBeingDisciplined) {
        this.betterdogs$isBeingDisciplined = isBeingDisciplined;
    }

    @Unique
    private void betterdogs$applyPersonalityStats(WolfPersonality personality) {
        WolfStatManager.applyPersonalityStats((Wolf) (Object) this, personality);
    }

    @Inject(method = "applyTamingSideEffects", at = @At("TAIL"))
    private void betterdogs$onApplyTamingSideEffectsTail(CallbackInfo ci) {
        if (this.isTame() && !this.level().isClientSide()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$tickHandler(CallbackInfo ci) {
        if (!betterdogs$statsApplied && betterdogs$hasPersonality()) {
            betterdogs$applyPersonalityStats(betterdogs$getPersonality());
            betterdogs$statsApplied = true;
        }

        if (!this.isTame())
            return;

        Wolf wolf = (Wolf) (Object) this;

        if (!wolf.level().isClientSide() && this.tickCount % 20 == 0 && betterdogs$isGuardMode()) {
            WolfPersonality personality = betterdogs$getPersonality();
            if (wolf.level() instanceof ServerLevel serverLevel) {
                double px = wolf.getRandomX(0.5);
                double py = wolf.getRandomY() + 0.5;
                double pz = wolf.getRandomZ(0.5);
                if (personality == WolfPersonality.AGGRESSIVE) {
                    serverLevel.sendParticles(new net.minecraft.core.particles.DustParticleOptions(0xFF3333, 0.6f), px, py, pz, 1, 0, 0.05, 0, 0.0);
                } else if (personality == WolfPersonality.PACIFIST) {
                    serverLevel.sendParticles(new net.minecraft.core.particles.DustParticleOptions(0x00FF88, 0.6f), px, py, pz, 1, 0, 0.05, 0, 0.0);

                    // Watchdog Grace Buff (Regeneration and Resistance to owner/allies within 6 blocks of wolf OR guard post)
                    if (net.dasik.social.api.gamerule.DynamicGameRuleManager.getBoolean(serverLevel, net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_PACIFIST_GUARD_BUFFS)) {
                        double buffRangeSqr = 36.0; // 6 blocks
                        net.minecraft.world.entity.player.Player owner = this.getOwner() instanceof net.minecraft.world.entity.player.Player ? (net.minecraft.world.entity.player.Player) this.getOwner() : null;
                        if (owner != null) {
                            if (owner.isAlive()) {
                                boolean isNearWolf = wolf.distanceToSqr(owner) <= buffRangeSqr;
                                net.minecraft.core.BlockPos post = betterdogs$getGuardPos();
                                boolean isNearPost = post != null && owner.distanceToSqr(post.getX() + 0.5, post.getY() + 0.5, post.getZ() + 0.5) <= buffRangeSqr;
                                
                                if (isNearWolf || isNearPost) {
                                    owner.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 80, 0, true, true));
                                    owner.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.RESISTANCE, 80, 0, true, true));
                                }
                            }

                            // Buff allied wolves within 6 blocks of this wolf
                            java.util.List<Wolf> allies = serverLevel.getEntitiesOfClass(Wolf.class, wolf.getBoundingBox().inflate(6.0), w -> w.isTame() && w.getOwner() == owner);
                            for (Wolf ally : allies) {
                                ally.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 80, 0, true, true));
                                ally.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.RESISTANCE, 80, 0, true, true));
                            }
                        }
                    }
                } else {
                    serverLevel.sendParticles(new net.minecraft.core.particles.DustParticleOptions(0xFFD700, 0.6f), px, py, pz, 1, 0, 0.05, 0, 0.0);
                }
            }
        }

        if (!wolf.level().isClientSide() && this.tickCount % 40 == 0 && betterdogs$isAdoptable()) {
            if (wolf.level() instanceof ServerLevel serverLevel) {
                RandomSource random = serverLevel.getRandom();
                for (int i = 0; i < 4; ++i) {
                    Vec3 source = wolf.position().add(random.nextDouble() * 0.6 - 0.3, random.nextDouble() * 0.5, random.nextDouble() * 0.6 - 0.3);
                    Vec3 destination = wolf.position().add(random.nextDouble() * 0.8 - 0.4, wolf.getEyeHeight() + 0.5 + random.nextDouble() * 0.5, random.nextDouble() * 0.8 - 0.4);
                    TrailParticleOption trail = new TrailParticleOption(destination, 0xFF99BB, random.nextInt(20) + 15);
                    serverLevel.sendParticles(trail, true, true, source.x, source.y, source.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }

        int lastDamageTime = betterdogs$getLastDamageTime();
        if (this.tickCount - lastDamageTime > BetterDogsConfig.get().getCombatHealDelayTicks()
                && this.getHealth() < this.getMaxHealth()) {
            betterdogs$healTimer++;
            if (betterdogs$healTimer >= BetterDogsConfig.get().getPassiveHealIntervalTicks()) {
                this.heal((float) BetterDogsConfig.get().getPassiveHealAmount());
                betterdogs$healTimer = 0;
            }
        } else {
            betterdogs$healTimer = 0;
        }
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onActuallyHurt(ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        betterdogs$setLastDamageTime(this.tickCount);
        WolfDebugLogger.log((Wolf)(Object)this, "Hurt", "Source: " + source.getMsgId() + ", Amount: " + amount);

        if (betterdogs$isAdoptable()) {
            betterdogs$setAdoptable(false);
            LivingEntity owner = this.getOwner();
            if (owner instanceof net.minecraft.world.entity.player.Player player) {
                player.sendOverlayMessage(Component.translatable("text.betterdogs.adoption_cancelled_damage", this.getName()));
            }
        }

        if (WolfCombatHooks.onActuallyHurt((Wolf) (Object) this, source, amount)) {
            ci.cancel();
        }
    }

    @Inject(method = "wantsToAttack", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onWantsToAttack(LivingEntity target, LivingEntity owner,
            CallbackInfoReturnable<Boolean> cir) {
        Boolean result = WolfCombatHooks.wantsToAttack((Wolf) (Object) this, target, owner);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }
}
