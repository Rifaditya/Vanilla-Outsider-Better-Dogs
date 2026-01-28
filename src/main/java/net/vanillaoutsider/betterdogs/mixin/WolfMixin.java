package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import org.jspecify.annotations.Nullable;
import net.minecraft.resources.Identifier;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.ai.WanderlustGoal;
import net.vanillaoutsider.betterdogs.ai.*;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfStatManager;
import net.vanillaoutsider.betterdogs.util.WolfCombatHooks;
import net.vanillaoutsider.betterdogs.util.WolfParticleHandler;
import net.vanillaoutsider.social.core.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for Wolf entity to add personality system and other enhancements.
 * Uses Fabric Data Attachment API for persistence.
 */
@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal implements WolfExtensions, SocialEntity {

    @Unique
    private int betterdogs$healTimer = 0;

    @Unique
    private boolean betterdogs$statsApplied = false;


    // Dummy constructor required for extending TamableAnimal
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

    // ========== Submissive (baby cannot attack pack after correction) ==========

    @Override
    public boolean betterdogs$isSubmissive() {
        return WolfPersistentData.isPersistedSubmissive((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setSubmissive(boolean submissive) {
        WolfPersistentData.setPersistedSubmissive((Wolf) (Object) this, submissive);
    }

    // ========== Blood Feud (permanent vendetta) ==========

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

    // ========== Last Mischief Day (daily mischief tracking) ==========

    @Override
    public long betterdogs$getLastMischiefDay() {
        return WolfPersistentData.getPersistedLastMischiefDay((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setLastMischiefDay(long day) {
        WolfPersistentData.setPersistedLastMischiefDay((Wolf) (Object) this, day);
    }

    // ========== SocialEntity Implementation & DNA ==========

    @Override
    public long betterdogs$getDNA() {
        return WolfPersistentData.getDNA((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setDNA(long dna) {
        WolfPersistentData.setDNA((Wolf) (Object) this, dna);
    }

    @Override
    public float betterdogs$getSocialScale() {
        return WolfPersistentData.getScale((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setSocialScale(float scale) {
        WolfPersistentData current = WolfPersistentData.getWolfData((Wolf)(Object)this);
        WolfPersistentData.setScale(current.personalityId(), current.lastDamageTime(), current.submissive(), current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), (Wolf)(Object)this, scale);
    }

    @Override
    public String betterdogs$getSpeciesId() {
        return "wolf";
    }

    @Override
    public LivingEntity betterdogs$asEntity() {
        return (LivingEntity) (Object) this;
    }


    // ========== Dunce Cap (Transient Disciplinary State) ==========

    @Unique
    private boolean isBeingDisciplined = false;

    @Override
    public boolean betterdogs$isBeingDisciplined() {
        return this.isBeingDisciplined;
    }

    @Override
    public void betterdogs$setBeingDisciplined(boolean isBeingDisciplined) {
        this.isBeingDisciplined = isBeingDisciplined;
    }
    
    // === SOCIAL CHANNEL SYSTEM (V2) ===
    
    @Unique private LivingEntity betterdogs$socialTarget = null;
    @Unique private WolfExtensions.SocialAction betterdogs$socialAction = WolfExtensions.SocialAction.NONE;
    @Unique private int betterdogs$socialModeTimer = 0;

    @Override
    public void betterdogs$setSocialState(@Nullable LivingEntity target, WolfExtensions.SocialAction action, int maxDurationTicks) {
        if (target != null && action != WolfExtensions.SocialAction.NONE) {
            this.betterdogs$socialTarget = target;
            this.betterdogs$socialAction = action;
            this.betterdogs$socialModeTimer = maxDurationTicks;
            // Immediate Logic: If we set a social target, we immediately engage.
            // Gatekeeper will now allow this specific target + action pair.
        } else {
            // Clearing target = End Social Mode
            this.betterdogs$socialTarget = null;
            this.betterdogs$socialAction = WolfExtensions.SocialAction.NONE;
            this.betterdogs$socialModeTimer = 0;
            // WAKE UP MASTER BRAIN
            ((Wolf)(Object)this).setTarget(null); 
        }
    }

    @Override
    public @Nullable LivingEntity betterdogs$getSocialTarget() {
        return this.betterdogs$socialTarget;
    }

    @Override
    public WolfExtensions.SocialAction betterdogs$getSocialAction() {
        return this.betterdogs$socialAction;
    }

    @Override
    public boolean betterdogs$isSocialModeActive() {
        return this.betterdogs$socialTarget != null && this.betterdogs$socialTarget.isAlive() && this.betterdogs$socialModeTimer > 0;
    }

    @Override
    public void betterdogs$tickSocialMode() {
        if (this.betterdogs$socialModeTimer > 0) {
            this.betterdogs$socialModeTimer--;
            if (this.betterdogs$socialModeTimer <= 0) {
                // FAILSAFE TRIGGERED: Force Unlock
                betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
            }
        } else if (this.betterdogs$socialTarget != null) {
             // Consistency check: Timer died but target exists? Kill it.
             betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
    }
    
    // === SCHEDULER SYSTEM (V3.1 - Ghost Brain Mode) ===
    
    @Unique
    private transient EntitySocialScheduler betterdogs$socialScheduler;

    @Override
    public @Nullable EntitySocialScheduler betterdogs$getScheduler() {
        return betterdogs$socialScheduler;
    }

    /**
     * Lazy initialization: Only creates a brain when social AI needs to "think".
     */
    @Override
    public EntitySocialScheduler betterdogs$getOrInitializeScheduler() {
        if (betterdogs$socialScheduler == null) {
            betterdogs$socialScheduler = new EntitySocialScheduler(this);
        }
        return betterdogs$socialScheduler;
    }

    @Override
    public void betterdogs$tickScheduler() {
        if (!this.level().isClientSide() && betterdogs$socialScheduler != null) {
            betterdogs$socialScheduler.tick();
            
            // SELF-PURGE: If the brain is idle, erase it from RAM.
            if (betterdogs$socialScheduler.isIdle()) {
                betterdogs$socialScheduler = null;
            }
        }
    }

    
    @Inject(method = "tick", at = @At("HEAD"))
    private void betterdogs$onTick(CallbackInfo ci) {
        betterdogs$tickSocialMode();
    }

    @Unique
    private void betterdogs$applyPersonalityStats(WolfPersonality personality) {
        WolfStatManager.applyPersonalityStats((Wolf)(Object)this, personality);
    }

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void betterdogs$onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (!level.getLevel().isClientSide()) {
            WolfPersonality personality = WolfPersonality.random(level.getLevel());
            betterdogs$setPersonality(personality);
            BetterDogs.LOGGER.info("Wolf spawned with personality: {}", personality.name());
            
            // Generate DNA and Scale
            long dna = this.getRandom().nextLong();
            betterdogs$setDNA(dna);
            float scale = 0.9f + (this.getRandom().nextFloat() * 0.2f); // 0.9x to 1.1x
            betterdogs$setSocialScale(scale);
            
            // Register to Global System Registry
            SocialRegistry.registerEntity(this);
        }
    }
    
    // Inject at remove to Unregister
    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide()) {
             SocialRegistry.unregisterEntity(this.betterdogs$asEntity());
        }
        super.remove(reason);
    }

    // ========== AI Goal Injection ==========

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void betterdogs$registerCustomGoals(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        this.goalSelector.addGoal(8, new WolfGiftGoal(wolf));
        this.goalSelector.addGoal(1, new FleeCreeperGoal(wolf));
        this.goalSelector.addGoal(1, new AvoidHazardsGoal(wolf));
        this.goalSelector.addGoal(3, new EatGroundFoodGoal(wolf));

        this.targetSelector.addGoal(2, new AggressiveTargetGoal(wolf));
        this.targetSelector.addGoal(2, new PacifistRevengeGoal(wolf));
        
        // Baby Training System (v1.10.000)
        this.targetSelector.addGoal(1, new BloodFeudGoal(wolf));        // Highest priority
        this.goalSelector.addGoal(0, new BabyBiteBackGoal(wolf));       // Retaliation (PRIORITY 0: Absolute Top)
        this.goalSelector.addGoal(4, new AdultCorrectionGoal(wolf));    // Correction
        this.goalSelector.addGoal(4, new SmallFightGoal(wolf));         // Small Fight (Simulated Aggression)
        this.goalSelector.addGoal(5, new BabyMischiefGoal(wolf));       // Daily mischief
        this.goalSelector.addGoal(6, new ZoomiesGoal(wolf));            // Zoomies (Hyperactive running)
        this.goalSelector.addGoal(7, new BeggingGoal(wolf));            // Begging
        this.goalSelector.addGoal(7, new WolfFetchGoal(wolf));          // Fetch

        Set<WrappedGoal> goalsToRemove = new HashSet<>();
        for (WrappedGoal goal : this.targetSelector.getAvailableGoals()) {
            if (goal.getGoal().toString().contains("NonTameRandomTargetGoal")) {
                goalsToRemove.add(goal);
            }
        }
        for (WrappedGoal goal : goalsToRemove) {
            this.targetSelector.removeGoal(goal.getGoal());
        }

        Set<WrappedGoal> followGoalsToRemove = new HashSet<>();
        for (WrappedGoal goal : this.goalSelector.getAvailableGoals()) {
            if (goal.getGoal() instanceof FollowOwnerGoal) {
                followGoalsToRemove.add(goal);
            }
        }
        for (WrappedGoal goal : followGoalsToRemove) {
            this.goalSelector.removeGoal(goal.getGoal());
        }

        this.goalSelector.addGoal(6, new PersonalityFollowOwnerGoal(wolf, 1.0, false));

        TargetingConditions.Selector preySelector = (entity, level) -> entity instanceof Sheep
                || entity instanceof Rabbit || entity instanceof Chicken;

        this.targetSelector.addGoal(4, new WildWolfHuntGoal<>(
                wolf,
                Animal.class,
                false,
                preySelector));

        if (BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_STORM_ANXIETY)) {
            this.goalSelector.addGoal(6, new WolfStormAnxietyGoal(wolf));
        }

        this.goalSelector.addGoal(7, new BabyCuriosityGoal(wolf, 0.8));
        
        // Scheduler Goals (V3.0)
        this.goalSelector.addGoal(8, new WanderlustGoal(wolf, 1.0));
    }
    
    // ========== On Tame - Assign Personality ==========
    @Inject(method = "applyTamingSideEffects", at = @At("HEAD"))
    private void betterdogs$onApplyTamingSideEffects(CallbackInfo ci) {
        if (!this.isTame() || this.level().isClientSide())
            return;

        if (!betterdogs$hasPersonality()) {
            WolfPersonality personality = WolfPersonality.random(this.level());
            betterdogs$setPersonality(personality);
            BetterDogs.LOGGER.info("Wolf [{}] tamed - assigning initial personality: {}", this.getUUID(), personality.name());
        }

        WolfPersonality personality = betterdogs$getPersonality();
        betterdogs$applyPersonalityStats(personality);
        betterdogs$statsApplied = true;

        betterdogs$playTameParticles(personality);
    }

    @Override
    public void spawnTamingParticles(boolean success) {
        if (success) {
            for (int i = 0; i < 3; i++) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, e, f);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.SMOKE, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, e, f);
            }
        }
    }

    @Unique
    private void betterdogs$playTameParticles(WolfPersonality personality) {
        WolfParticleHandler.playTameParticles((Wolf)(Object)this, personality);
    }

    
    // ========== Passive Healing + Stats Reapply ==========

    @Unique
    private boolean betterdogs$initialized = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$tickHandler(CallbackInfo ci) {
        if (!this.level().isClientSide()) {
            // Self-Healing: Register to Hive Mind if not tracked (e.g., world load/Nether transition)
            if (!betterdogs$initialized || this.tickCount % 100 == 0) {
                if (!SocialRegistry.containsEntity((LivingEntity)(Object)this)) {
                    SocialRegistry.registerEntity(this);
                }
                
                // Legacy DNA Migration: If 0, generate from UUID
                if (betterdogs$getDNA() == 0L) {
                    betterdogs$setDNA(this.getUUID().getMostSignificantBits());
                    // Re-calculate scale as well
                    float scale = 0.9f + (this.getRandom().nextFloat() * 0.2f);
                    betterdogs$setSocialScale(scale);
                }
                
                betterdogs$initialized = true;
            }
        }

        if (!betterdogs$statsApplied && this.isTame() && betterdogs$hasPersonality()) {
            betterdogs$applyPersonalityStats(betterdogs$getPersonality());
            betterdogs$statsApplied = true;
        }

        if (!this.isTame())
            return;
            
        // Tick Scheduler (V3.0)
        this.betterdogs$tickScheduler();
            
        Wolf wolf = (Wolf) (Object) this;
        // Cliff Safety Game Rule (Server Side Only to avoid ClassCastException and desync)
        if (!this.level().isClientSide()) {
            betterdogs$checkTargetCliffSafety();
            betterdogs$checkMovementCliffSafety();
        }

        // Passive healing
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

    // ========== Track Damage Time + Friendly Fire Protection ==========

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onActuallyHurt(ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        betterdogs$setLastDamageTime(this.tickCount);

        if (WolfCombatHooks.onActuallyHurt((Wolf)(Object)this, source, amount)) {
            ci.cancel();
        }
    }

    /**
     * Target control for baby training system.
     * - Submissive wolves cannot attack pack members
     * - Blood feud wolves CAN attack their nemesis (bypass protection)
     */
    @Inject(method = "wantsToAttack", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onWantsToAttack(LivingEntity target, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
        Boolean result = WolfCombatHooks.wantsToAttack((Wolf)(Object)this, target, owner);
        if (result != null) {
            cir.setReturnValue(result);
        }
    }

    /**
     * PASSIVE CLIFF SAFETY (Smart Brakes)
     * Prevents falling due to Zoomies or Pushing check ahead based on velocity.
     */
    // ========== Cliff Safety (Hardened) ==========

    @Unique
    private void betterdogs$checkMovementCliffSafety() {
        Wolf wolf = (Wolf) (Object) this;
        // 1. Config Check
        if (!BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_CLIFF_SAFETY)) return;

        // 2. Optimization: Only check if moving horizontally
        if (wolf.getDeltaMovement().horizontalDistanceSqr() < 0.0001) return;

        // REMOVED: isJumping() and onGround() checks to catch "pushed" dogs.

        // 4. Velocity Lookahead Logic
        Vec3 velocity = wolf.getDeltaMovement();
        // Look 5 blocks ahead (hardened buffer)
        Vec3 lookaheadPos = wolf.position().add(velocity.scale(5.0));
        BlockPos hazardPos = BlockPos.containing(lookaheadPos);
        
        // Use generic Level to avoid casting issues, though we are guarded by server check now.
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
            // DANGER: No ground found.
            // ACTION: Smart Brake - Kill horizontal momentum.
            wolf.getNavigation().stop();
            wolf.setDeltaMovement(Vec3.ZERO);
            wolf.setShiftKeyDown(true); // Visual "Oh snap!"
            
            // BetterDogs.LOGGER.info("CliffSafety: Stopped Wolf {} at {}", wolf.getId(), hazardPos);
        }
    }

    /**
     * TARGET CLIFF SAFETY (Existing Logic)
     * Prevents jumping down after a target that is too low.
     */
    @Unique
    private void betterdogs$checkTargetCliffSafety() {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.getTarget() == null) return;
        
        if (!BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_CLIFF_SAFETY)) return;

        double yDiff = wolf.getY() - wolf.getTarget().getY();
        boolean dangerDetected = false;
        
        if (yDiff > 3.0 && wolf.onGround()) {
            dangerDetected = true;
        } else if (!wolf.getTarget().onGround()) {
            boolean groundFound = false;
            BlockPos targetPos = wolf.getTarget().blockPosition();
            for (int i = 1; i <= 4; i++) {
                if (!wolf.level().isEmptyBlock(targetPos.below(i))) {
                    groundFound = true;
                    break;
                }
            }
            if (!groundFound) {
                dangerDetected = true;
            }
        }

        if (dangerDetected) {
            wolf.getNavigation().stop();
            Vec3 targetPos = wolf.getTarget().position();
            wolf.setTarget(null);
            Vec3 retreatPos = DefaultRandomPos.getPosAway(wolf, 4, 1, targetPos);
            if (retreatPos != null) {
                wolf.getNavigation().moveTo(retreatPos.x, retreatPos.y, retreatPos.z, 1.0);
            }
        }
    }
}
