// Verified against: Wolf.java (26.2+)
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
import net.vanillaoutsider.betterdogs.util.WolfCombatHooks;
import net.vanillaoutsider.betterdogs.util.WolfDebugLogger;
import net.vanillaoutsider.betterdogs.util.WolfStatManager;
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

    @Unique
    private int betterdogs$lastDamageTime = 0;

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
        return this.betterdogs$lastDamageTime;
    }

    @Override
    public void betterdogs$setLastDamageTime(int time) {
        this.betterdogs$lastDamageTime = time;
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
        if (!this.betterdogs$statsApplied && this.betterdogs$hasPersonality()) {
            this.betterdogs$applyPersonalityStats(this.betterdogs$getPersonality());
            this.betterdogs$statsApplied = true;
        }

        if (!this.isTame()) {
            return;
        }

        Wolf wolf = (Wolf) (Object) this;

        if (!wolf.level().isClientSide()) {
            if (this.tickCount % 20 == 0 && this.betterdogs$isGuardMode()) {
                if (wolf.level() instanceof ServerLevel serverLevel) {
                    net.vanillaoutsider.betterdogs.util.WolfTickHelper.tickGuardMode(wolf, this, serverLevel);
                }
            }

            if (this.tickCount % 40 == 0 && this.betterdogs$isAdoptable()) {
                if (wolf.level() instanceof ServerLevel serverLevel) {
                    net.vanillaoutsider.betterdogs.util.WolfTickHelper.tickAdoptableParticles(wolf, serverLevel);
                }
            }

            if (this.tickCount % 40 == 0 && net.vanillaoutsider.betterdogs.WolfPersistentData.isPersistedInbred(wolf)) {
                if (wolf.level() instanceof ServerLevel serverLevel) {
                    net.vanillaoutsider.betterdogs.util.WolfTickHelper.tickRuntParticles(wolf, serverLevel);
                }
            }
        }

        this.betterdogs$healTimer = net.vanillaoutsider.betterdogs.util.WolfTickHelper.tickPassiveHealing(wolf, this, this.betterdogs$healTimer);
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

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void betterdogs$onAddAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        WolfPersistentData current = WolfPersistentData.getWolfData(wolf);
        int elapsed = wolf.tickCount - this.betterdogs$lastDamageTime;
        int cooldownRemaining = Math.max(0, BetterDogsConfig.get().getCombatHealDelayTicks() - elapsed);

        WolfPersistentData updated = new WolfPersistentData(
            current.personalityId(),
            cooldownRemaining,
            current.submissive(),
            current.bloodFeudTarget(),
            current.lastMischiefDay(),
            current.dna(),
            current.scale(),
            current.affinityMap(),
            current.leaderUuid(),
            current.guardMode(),
            current.guardPos(),
            current.adoptable(),
            current.healthBonus(),
            current.damageMod(),
            current.speedMod(),
            current.statsRolled(),
            current.parent1Uuid(),
            current.parent2Uuid(),
            current.inbred()
        );
        WolfPersistentData.setWolfData(wolf, updated);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void betterdogs$onReadAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        int cooldownRemaining = WolfPersistentData.getPersistedLastDamageTime(wolf);
        this.betterdogs$lastDamageTime = wolf.tickCount - (BetterDogsConfig.get().getCombatHealDelayTicks() - cooldownRemaining);
    }

    // Verified against: Entity.java (26.2+)
    @Inject(method = "killedEntity", at = @At("HEAD"))
    private void betterdogs$onKilledEntity(ServerLevel level, LivingEntity entity, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (this.isTame() && this.betterdogs$isGuardMode() && this.betterdogs$getPersonality() == WolfPersonality.AGGRESSIVE) {
            LivingEntity owner = this.getOwner();
            if (owner instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                net.vanillaoutsider.betterdogs.BetterDogs.ON_PATROL.trigger(serverPlayer);
            }
        }
    }
}
