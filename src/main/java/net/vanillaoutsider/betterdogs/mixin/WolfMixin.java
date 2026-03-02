package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersistenceKt;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.ai.AggressiveTargetGoal;
import net.vanillaoutsider.betterdogs.ai.WildWolfHuntGoal;
import net.vanillaoutsider.betterdogs.ai.WolfGiftGoal;
import net.vanillaoutsider.betterdogs.ai.WolfStormAnxietyGoal;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.ai.*;
import net.vanillaoutsider.betterdogs.scheduler.WolfScheduler;
import net.vanillaoutsider.betterdogs.WolfExtensions.SocialAction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal;
import java.util.Set;
import java.util.HashSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal; // Add import
import net.vanillaoutsider.betterdogs.ai.PersonalityWanderGoal; // Add import
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for Wolf entity to add personality system and other enhancements.
 * Uses Fabric Data Attachment API for persistence.
 */
@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal implements WolfExtensions {

    @Unique
    private int betterdogs$healTimer = 0;

    @Unique
    private boolean betterdogs$statsApplied = false;

    @Unique
    private WolfScheduler betterdogs$scheduler;

    @Unique
    private LivingEntity betterdogs$socialTarget;

    @Unique
    private SocialAction betterdogs$socialAction = SocialAction.NONE;

    @Unique
    private int betterdogs$socialTimer = 0;

    // Dummy constructor required for extending TamableAnimal
    protected WolfMixin() {
        super(null, null);
    }

    // ========== Stat Modifier Constants ==========

    @Unique
    private static final String AGGRESSIVE_SPEED_ID = "betterdogs:aggressive_speed";
    @Unique
    private static final String AGGRESSIVE_DAMAGE_ID = "betterdogs:aggressive_damage";
    @Unique
    private static final String PACIFIST_SPEED_ID = "betterdogs:pacifist_speed";
    @Unique
    private static final String PACIFIST_DAMAGE_ID = "betterdogs:pacifist_damage";
    @Unique
    private static final String PACIFIST_KNOCKBACK_ID = "betterdogs:pacifist_knockback";
    @Unique
    private static final String BASE_SPEED_ID = "betterdogs:base_speed_boost";
    @Unique
    private static final String AGGRESSIVE_HEALTH_ID = "betterdogs:aggressive_health";
    @Unique
    private static final String NORMAL_SPEED_ID = "betterdogs:normal_speed";
    @Unique
    private static final String NORMAL_DAMAGE_ID = "betterdogs:normal_damage";
    @Unique
    private static final String NORMAL_HEALTH_ID = "betterdogs:normal_health";

    // ========== WolfExtensions Implementation (delegates to persistence)
    // ==========

    @Override
    public WolfPersonality betterdogs$getPersonality() {
        return WolfPersistenceKt.getPersistedPersonality((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setPersonality(WolfPersonality personality) {
        WolfPersistenceKt.setPersistedPersonality((Wolf) (Object) this, personality);
    }

    @Override
    public boolean betterdogs$hasPersonality() {
        return WolfPersistenceKt.hasPersistedPersonality((Wolf) (Object) this);
    }

    @Override
    public int betterdogs$getLastDamageTime() {
        return WolfPersistenceKt.getPersistedLastDamageTime((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setLastDamageTime(int time) {
        WolfPersistenceKt.setPersistedLastDamageTime((Wolf) (Object) this, time);
    }

    // ========== 1.21.11 Parity Implementations ==========

    @Override
    public boolean betterdogs$isSubmissive() {
        return WolfPersistenceKt.getPersistedSubmissive((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setSubmissive(boolean submissive) {
        WolfPersistenceKt.setPersistedSubmissive((Wolf) (Object) this, submissive);
    }

    @Override
    public String betterdogs$getBloodFeudTarget() {
        return WolfPersistenceKt.getPersistedBloodFeudTarget((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setBloodFeudTarget(String targetUuid) {
        WolfPersistenceKt.setPersistedBloodFeudTarget((Wolf) (Object) this, targetUuid);
    }

    @Override
    public boolean betterdogs$hasBloodFeud() {
        return WolfPersistenceKt.hasPersistedBloodFeud((Wolf) (Object) this);
    }

    @Override
    public long betterdogs$getLastMischiefDay() {
        return WolfPersistenceKt.getPersistedLastMischiefDay((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setLastMischiefDay(long day) {
        WolfPersistenceKt.setPersistedLastMischiefDay((Wolf) (Object) this, day);
    }

    @Override
    public boolean betterdogs$isBeingDisciplined() {
        return WolfPersistenceKt.getPersistedBeingDisciplined((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setBeingDisciplined(boolean isBeingDisciplined) {
        WolfPersistenceKt.setPersistedBeingDisciplined((Wolf) (Object) this, isBeingDisciplined);
    }

    // === SCHEDULER SYSTEM ===

    @Override
    public WolfScheduler betterdogs$getScheduler() {
        if (betterdogs$scheduler == null) {
            betterdogs$scheduler = new WolfScheduler((Wolf) (Object) this);
        }
        return betterdogs$scheduler;
    }

    @Override
    public void betterdogs$tickScheduler() {
        betterdogs$getScheduler().tickActiveEvent();
        betterdogs$getScheduler().tryStartEvent();
    }

    // === SOCIAL CHANNEL SYSTEM ===

    @Override
    public void betterdogs$setSocialState(@Nullable LivingEntity target, SocialAction action, int maxDurationTicks) {
        this.betterdogs$socialTarget = target;
        this.betterdogs$socialAction = action;
        this.betterdogs$socialTimer = maxDurationTicks;
    }

    @Override
    public @Nullable LivingEntity betterdogs$getSocialTarget() {
        return this.betterdogs$socialTarget;
    }

    @Override
    public SocialAction betterdogs$getSocialAction() {
        return this.betterdogs$socialAction;
    }

    @Override
    public boolean betterdogs$isSocialModeActive() {
        return this.betterdogs$socialAction != SocialAction.NONE;
    }

    @Override
    public void betterdogs$tickSocialMode() {
        if (this.betterdogs$socialTimer > 0) {
            this.betterdogs$socialTimer--;
            if (this.betterdogs$socialTimer <= 0) {
                this.betterdogs$setSocialState(null, SocialAction.NONE, 0);
            }
        }
    }

    // ========== Stat Modifiers ==========

    @Unique
    private void betterdogs$applyPersonalityStats(WolfPersonality personality) {
        var speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        var damageAttr = this.getAttribute(Attributes.ATTACK_DAMAGE);
        var knockbackAttr = this.getAttribute(Attributes.ATTACK_KNOCKBACK);

        if (speedAttr == null || damageAttr == null)
            return;

        Identifier aggressiveSpeedId = Identifier.parse(AGGRESSIVE_SPEED_ID);
        Identifier aggressiveDamageId = Identifier.parse(AGGRESSIVE_DAMAGE_ID);
        Identifier pacifistSpeedId = Identifier.parse(PACIFIST_SPEED_ID);
        Identifier pacifistDamageId = Identifier.parse(PACIFIST_DAMAGE_ID);
        Identifier pacifistKnockbackId = Identifier.parse(PACIFIST_KNOCKBACK_ID);
        Identifier baseSpeedId = Identifier.parse(BASE_SPEED_ID);

        // Remove existing modifiers
        speedAttr.removeModifier(aggressiveSpeedId);
        speedAttr.removeModifier(pacifistSpeedId);
        speedAttr.removeModifier(baseSpeedId);
        damageAttr.removeModifier(aggressiveDamageId);
        damageAttr.removeModifier(pacifistDamageId);

        // Apply Base Speed Boost (Configurable) to all Better Dogs
        double speedBuff = BetterDogsConfig.Companion.get().getGlobalSpeedBuff();
        speedAttr.addPermanentModifier(new AttributeModifier(baseSpeedId, speedBuff,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        if (knockbackAttr != null) {
            knockbackAttr.removeModifier(pacifistKnockbackId);
        }

        switch (personality) {
            case AGGRESSIVE -> {
                // Configurable Modifiers
                double speedMod = BetterDogsConfig.Companion.get().getAggressiveSpeedModifier();
                double damageMod = BetterDogsConfig.Companion.get().getAggressiveDamageModifier();

                speedAttr.addPermanentModifier(new AttributeModifier(aggressiveSpeedId, speedMod,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                damageAttr.addPermanentModifier(new AttributeModifier(aggressiveDamageId, damageMod,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

                var healthAttr = this.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null) {
                    Identifier aggressiveHealthId = Identifier.parse(AGGRESSIVE_HEALTH_ID);
                    healthAttr.removeModifier(aggressiveHealthId);

                    double hpBonus = BetterDogsConfig.Companion.get().getAggressiveHealthBonus();
                    if (hpBonus > 0) {
                        healthAttr.addPermanentModifier(new AttributeModifier(aggressiveHealthId, hpBonus,
                                AttributeModifier.Operation.ADD_VALUE));
                        // Heal to new max if needed
                        if (this.getHealth() < this.getMaxHealth()) {
                            this.heal((float) hpBonus);
                        }
                    }
                }
            }
            case PACIFIST -> {
                // Configurable Modifiers
                double speedMod = BetterDogsConfig.Companion.get().getPacifistSpeedModifier();
                double damageMod = BetterDogsConfig.Companion.get().getPacifistDamageModifier();
                double kbMod = BetterDogsConfig.Companion.get().getPacifistKnockbackModifier();

                speedAttr.addPermanentModifier(new AttributeModifier(pacifistSpeedId, speedMod,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                damageAttr.addPermanentModifier(new AttributeModifier(pacifistDamageId, damageMod,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                if (knockbackAttr != null) {
                    knockbackAttr.addPermanentModifier(new AttributeModifier(pacifistKnockbackId, kbMod,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
            }
            case NORMAL -> {
                // Configurable Modifiers
                double speedMod = BetterDogsConfig.Companion.get().getNormalSpeedModifier();
                double damageMod = BetterDogsConfig.Companion.get().getNormalDamageModifier();
                double healthMod = BetterDogsConfig.Companion.get().getNormalHealthBonus();

                Identifier normalSpeedId = Identifier.parse(NORMAL_SPEED_ID);
                Identifier normalDamageId = Identifier.parse(NORMAL_DAMAGE_ID);
                Identifier normalHealthId = Identifier.parse(NORMAL_HEALTH_ID);

                if (speedMod != 0.0) {
                    speedAttr.addPermanentModifier(new AttributeModifier(normalSpeedId, speedMod,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
                if (damageMod != 0.0) {
                    damageAttr.addPermanentModifier(new AttributeModifier(normalDamageId, damageMod,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }

                var healthAttr = this.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null) {
                    if (healthMod > 0) {
                        healthAttr.addPermanentModifier(new AttributeModifier(normalHealthId, healthMod,
                                AttributeModifier.Operation.ADD_VALUE));
                        if (this.getHealth() < this.getMaxHealth()) {
                            this.heal((float) healthMod);
                        }
                    }
                }
            }
        }
    }

    // ========== AI Goal Injection ==========

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void betterdogs$registerCustomGoals(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        // 1. Add Gift Goal (Priority 8 - low priority)
        this.goalSelector.addGoal(8, new WolfGiftGoal(wolf));

        // 2. Add Aggressive Target Goal (Priority 2 - high priority)
        this.targetSelector.addGoal(2, new AggressiveTargetGoal(wolf));

        // 3. Replace Vanilla Wild Hunting
        // We want to remove the vanilla NonTameRandomTargetGoal because it doesn't
        // check for health
        Set<WrappedGoal> goalsToRemove = new HashSet<>();
        for (WrappedGoal goal : this.targetSelector.getAvailableGoals()) {
            // Check string representation as fallback if class is not accessible
            if (goal.getGoal().toString().contains("NonTameRandomTargetGoal")) {
                goalsToRemove.add(goal);
            }
        }

        for (WrappedGoal goal : goalsToRemove) {
            this.targetSelector.removeGoal(goal.getGoal());
        }

        // 4. Replace Vanilla Follow Owner Goal (to apply config ranges)
        // Vanilla usually adds this at priority 6. We'll remove it and add our own.
        // Similar search logic in case we can't target the class directly easily
        // (though we can w/ import)
        Set<WrappedGoal> followGoalsToRemove = new HashSet<>();
        for (WrappedGoal goal : this.goalSelector.getAvailableGoals()) {
            if (goal.getGoal() instanceof FollowOwnerGoal) {
                followGoalsToRemove.add(goal);
            }
        }
        for (WrappedGoal goal : followGoalsToRemove) {
            this.goalSelector.removeGoal(goal.getGoal());
        }

        // Params: wolf, speed, leavesAllowed (dist logic inside goal)
        this.goalSelector.addGoal(6, new PersonalityFollowOwnerGoal(wolf, 1.0, false));

        // Add our custom hunting goal
        TargetingConditions.Selector preySelector = (entity, level) -> entity instanceof Sheep
                || entity instanceof Rabbit || entity instanceof Chicken;

        this.targetSelector.addGoal(4, new WildWolfHuntGoal<>(
                wolf,
                Animal.class,
                false,
                preySelector));

        // 4. Add Storm Anxiety Goal (High Priority - 6)
        if (BetterDogsConfig.Companion.get().getEnableStormAnxiety()) {
            this.goalSelector.addGoal(6, new WolfStormAnxietyGoal(wolf));
        }

        // 5. Parity Update: Add new goals
        this.goalSelector.addGoal(5, new BabyMischiefGoal(wolf));
        this.goalSelector.addGoal(1, new BloodFeudGoal(wolf)); // Very high priority
        this.goalSelector.addGoal(3, new AdultCorrectionGoal(wolf));
        this.goalSelector.addGoal(3, new BabyBiteBackGoal(wolf));
        this.goalSelector.addGoal(5, new ZoomiesGoal(wolf));
        this.goalSelector.addGoal(7, new HowlGoal(wolf));

        // 6. Replace Vanilla Wander Goal (WaterAvoidingRandomStrollGoal)
        // We want to reduce the frequency of wandering for Pacifist dogs.
        Set<WrappedGoal> wanderGoalsToRemove = new HashSet<>();
        for (WrappedGoal goal : this.goalSelector.getAvailableGoals()) {
            if (goal.getGoal() instanceof WaterAvoidingRandomStrollGoal) {
                wanderGoalsToRemove.add(goal);
            }
        }
        for (WrappedGoal goal : wanderGoalsToRemove) {
            this.goalSelector.removeGoal(goal.getGoal());
        }

        // Add our custom wander goal (Priority 8, same as vanilla usually)
        this.goalSelector.addGoal(8, new PersonalityWanderGoal(wolf, 1.0));
    }

    // ========== On Tame - Assign Personality ==========

    @Override
    public void setTame(boolean tamed, boolean applyBoost) {
        super.setTame(tamed, applyBoost);

        if (!tamed)
            return;

        if (!betterdogs$hasPersonality()) {
            WolfPersonality personality = WolfPersonality.Companion.random();
            betterdogs$setPersonality(personality);
            betterdogs$applyPersonalityStats(personality);
            betterdogs$statsApplied = true;

            if (this.level() instanceof ServerLevel serverLevel) {
                switch (personality) {
                    case AGGRESSIVE -> {
                        for (int i = 0; i < 7; i++) {
                            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                                    this.getX() + this.getRandom().nextGaussian() * 0.5,
                                    this.getY() + 0.5 + this.getRandom().nextDouble() * 0.5,
                                    this.getZ() + this.getRandom().nextGaussian() * 0.5, 1, 0, 0, 0, 0);
                        }
                    }
                    case PACIFIST -> {
                        for (int i = 0; i < 7; i++) {
                            serverLevel.sendParticles(ParticleTypes.HEART,
                                    this.getX() + this.getRandom().nextGaussian() * 0.5,
                                    this.getY() + 0.5 + this.getRandom().nextDouble() * 0.5,
                                    this.getZ() + this.getRandom().nextGaussian() * 0.5, 1, 0, 0, 0, 0);
                        }
                    }
                    case NORMAL -> {
                        for (int i = 0; i < 7; i++) {
                            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                    this.getX() + this.getRandom().nextGaussian() * 0.5,
                                    this.getY() + 0.5 + this.getRandom().nextDouble() * 0.5,
                                    this.getZ() + this.getRandom().nextGaussian() * 0.5, 1, 0, 0, 0, 0);
                        }
                    }
                }
            }
        }
    }

    // ========== Passive Healing + Stats Reapply ==========

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$tickHandler(CallbackInfo ci) {
        // Reapply stats on first tick after world load
        if (!betterdogs$statsApplied && this.isTame() && betterdogs$hasPersonality()) {
            betterdogs$applyPersonalityStats(betterdogs$getPersonality());
            betterdogs$statsApplied = true;
        }

        // Tick Scheduler & Social Mode
        if (this.isTame()) {
            betterdogs$tickScheduler();
            betterdogs$tickSocialMode();
        }

        if (!this.isTame())
            return;

        // Cliff Safety Protocol: Stop chasing if target is significantly below us
        // (likely fell off)
        Wolf wolf = (Wolf) (Object) this;
        // Cliff Safety Game Rule (Server Side Only to avoid ClassCastException and
        // desync)
        if (!this.level().isClientSide()) {
            if (wolf.getTarget() != null && BetterDogsConfig.Companion.get().getEnableCliffSafety()) {
                betterdogs$checkTargetCliffSafety();
            }
            betterdogs$checkMovementCliffSafety();
        }

        int currentTick = this.tickCount;
        int lastDamageTime = betterdogs$getLastDamageTime();

        if (currentTick - lastDamageTime > BetterDogsConfig.Companion.get().getCombatHealDelayTicks()
                && this.getHealth() < this.getMaxHealth()) {
            betterdogs$healTimer++;
            if (betterdogs$healTimer >= BetterDogsConfig.Companion.get().getPassiveHealIntervalTicks()) {
                this.heal((float) BetterDogsConfig.Companion.get().getPassiveHealAmount());
                betterdogs$healTimer = 0;
            }
        } else {
            betterdogs$healTimer = 0;
        }
    }

    // ========== Track Damage Time + Friendly Fire Protection ==========

    // ========== Track Damage Time + Friendly Fire Protection ==========

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onActuallyHurt(ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        betterdogs$setLastDamageTime(this.tickCount);

        if (this.isTame() && source.getEntity() instanceof Player player) {
            if (BetterDogsConfig.Companion.get().getEnableFriendlyFireProtection() && this.isOwnedBy(player)
                    && !player.isShiftKeyDown()) {
                ci.cancel(); // Cancel damage logic entirely
            }
        }
    }

    /**
     * PASSIVE CLIFF SAFETY (Smart Brakes)
     * Prevents falling due to Zoomies or Pushing check ahead based on velocity.
     */
    @Unique
    private void betterdogs$checkMovementCliffSafety() {
        Wolf wolf = (Wolf) (Object) this;
        // 1. Config Check
        if (!BetterDogsConfig.Companion.get().getEnableCliffSafety())
            return;

        // 2. Optimization: Only check if moving horizontally
        if (wolf.getDeltaMovement().horizontalDistanceSqr() < 0.0001)
            return;

        // REMOVED: isJumping() and onGround() checks.
        // We now check safety regardless of state to catch mid-air pushes or jumps.

        // 4. Velocity Lookahead Logic
        Vec3 velocity = wolf.getDeltaMovement();

        // Look 5 ticks ahead (stronger buffer than 3)
        // This covers jumping arcs better.
        Vec3 lookaheadPos = wolf.position().add(velocity.scale(5.0));
        BlockPos hazardPos = BlockPos.containing(lookaheadPos);

        // Level is sufficient, no need to cast to ServerLevel (checked in caller)
        net.minecraft.world.level.Level level = wolf.level();

        // 5. Cliff Definition: AIR at feet, and AIR deep below.
        boolean solidGround = false;
        // Check 3 blocks down for ground. Safe drop is <= 3.
        for (int i = 0; i <= 3; i++) {
            if (!level.isEmptyBlock(hazardPos.below(i))) {
                solidGround = true;
                break;
            }
        }

        if (!solidGround) {
            // DANGER: No ground found in 3 blocks drop at lookahead position.
            // ACTION: Smart Brake
            wolf.getNavigation().stop();
            // Kill ONLY horizontal momentum, preserve vertical (gravity) so we don't float
            // Actually, killing all momentum is safer to prevent arc completion.
            wolf.setDeltaMovement(Vec3.ZERO);
            wolf.setShiftKeyDown(true); // Visual "Oh snap!" crouch + physics braking

            // Debug Log (To help diagnose "Why did I stop?" or confirm it's working)
            // BetterDogs.LOGGER.info("CliffSafety PROTECTION: Stopping Wolf {} at {}",
            // wolf.getId(), hazardPos);
        }
    }

    /**
     * TARGET CLIFF SAFETY (Existing Logic)
     * Refactored to helper method.
     */
    @Unique
    private void betterdogs$checkTargetCliffSafety() {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.getTarget() == null)
            return;

        // Logic from previous implementation
        double yDiff = wolf.getY() - wolf.getTarget().getY();
        if (yDiff > 3.0 && wolf.onGround()) {
            wolf.getNavigation().stop();
            wolf.setTarget(null); // Clear target to stop aggro
            return;
        }

        if (!wolf.getTarget().onGround()) {
            boolean groundFound = false;
            BlockPos targetPos = wolf.getTarget().blockPosition();
            for (int i = 1; i <= 4; i++) {
                if (!wolf.level().isEmptyBlock(targetPos.below(i))) {
                    groundFound = true;
                    break;
                }
            }

            if (!groundFound) {
                wolf.getNavigation().stop();
                wolf.getLookControl().setLookAt(wolf.getTarget(), 30.0f, 30.0f);
            }
        }
    }
}
