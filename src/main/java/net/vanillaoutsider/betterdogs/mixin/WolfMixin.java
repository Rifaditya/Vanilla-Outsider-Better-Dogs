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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import net.minecraft.resources.Identifier;
import net.vanillaoutsider.betterdogs.BetterDogs;
import net.vanillaoutsider.betterdogs.scheduler.WolfScheduler;
import net.vanillaoutsider.betterdogs.ai.WanderlustGoal;
import net.vanillaoutsider.betterdogs.ai.*;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.vanillaoutsider.betterdogs.util.WolfStatManager;
import net.vanillaoutsider.betterdogs.util.WolfCombatHooks;
import net.vanillaoutsider.betterdogs.util.WolfParticleHandler;
import java.util.Set;
import java.util.HashSet;
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
public abstract class WolfMixin extends TamableAnimal implements WolfExtensions {

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
    
    // === SCHEDULER SYSTEM (V3.0 - Modular) ===
    
    @Unique
    private WolfScheduler betterdogs$scheduler;

    @Override
    public WolfScheduler betterdogs$getScheduler() {
        if (betterdogs$scheduler == null) {
            betterdogs$scheduler = new WolfScheduler((Wolf)(Object)this);
        }
        return betterdogs$scheduler;
    }

    @Override
    public void betterdogs$tickScheduler() {
        if (!this.level().isClientSide()) { // Server only
            betterdogs$getScheduler().tickActiveEvent(); // Only tick active behaviors
        }
    }

    // === THE GATEKEEPER (setTarget Override) ===
    // This REPLACES the standard setTarget logic to enforce "One Brain at a Time"
    
    @Override
    public void setTarget(@Nullable LivingEntity newTarget) {
        // If this wolf is in SOCIAL MODE
        if (betterdogs$isSocialModeActive()) {
            
            // RULE: Only the Social Target is allowed to pass.
            if (newTarget == betterdogs$getSocialTarget()) {
                // ALLOW (Transient Switch for attack mechanics)
                // Proceed to super
            } else {
                // DENY (Master Brain is Dormant. Ignore "Enemies", "Attackers", "Owner Hitting something")
                return; 
            }
        }
        // If NOT in Social Mode -> Standard Vanilla Logic applies (Master Brain is Awake)
        super.setTarget(newTarget);
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
            WolfPersonality personality = WolfPersonality.random();
            betterdogs$setPersonality(personality);
            BetterDogs.LOGGER.info("Wolf spawned with personality: {}", personality.name());
            
            // Register to Global System Scheduler
            net.vanillaoutsider.betterdogs.scheduler.WolfSystemScheduler.get().add((Wolf)(Object)this);
        }
    }
    
    // Inject at remove to Unregister
    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide()) {
             net.vanillaoutsider.betterdogs.scheduler.WolfSystemScheduler.get().remove((Wolf)(Object)this);
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
        this.goalSelector.addGoal(6, new HowlGoal(wolf));               // Group Howl (Sits and Howls)

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

        if (BetterDogsConfig.get().getEnableStormAnxiety()) {
            this.goalSelector.addGoal(6, new WolfStormAnxietyGoal(wolf));
        }

        this.goalSelector.addGoal(7, new BabyCuriosityGoal(wolf, 0.8));
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
            WolfPersonality personality = WolfPersonality.random();
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

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void betterdogs$addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        betterdogs$getScheduler().save(output);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void betterdogs$readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        betterdogs$getScheduler().load(input);
        
        // Register to Global Scheduler on Load
        if (!this.level().isClientSide()) {
            net.vanillaoutsider.betterdogs.scheduler.WolfSystemScheduler.get().add((Wolf)(Object)this);
        }
    }
    
    // ========== Passive Healing + Stats Reapply ==========

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$tickHandler(CallbackInfo ci) {
        if (!betterdogs$statsApplied && this.isTame() && betterdogs$hasPersonality()) {
            betterdogs$applyPersonalityStats(betterdogs$getPersonality());
            betterdogs$statsApplied = true;
        }

        if (!this.isTame())
            return;
            
        // Tick Scheduler (V3.0)
        this.betterdogs$tickScheduler();
            
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.getTarget() != null && BetterDogsConfig.get().getEnableCliffSafety()) {
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
                return;
            }
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

}
