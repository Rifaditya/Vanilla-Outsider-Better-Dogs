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
import net.vanillaoutsider.betterdogs.scheduler.events.*; // Import events for polling
import net.dasik.social.api.SocialEntity;
import net.dasik.social.core.EntitySocialScheduler;
import net.dasik.social.core.GlobalSocialSystem;
import net.dasik.social.api.group.GroupMember;
import net.dasik.social.api.group.FlockType;
import net.vanillaoutsider.betterdogs.ai.group.WildWolfFollowLeaderGoal;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import java.util.Optional;
import java.util.Optional;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.network.chat.Component;
import net.vanillaoutsider.betterdogs.util.WolfDebugLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin for Wolf entity to add personality system and other enhancements.
 * Uses Fabric Data Attachment API for persistence.
 * Verified against: Wolf.java (26.1.2 Release)
 */
@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal implements WolfExtensions, SocialEntity, GroupMember {

    @Unique
    private int betterdogs$healTimer = 0;

    @Unique
    private boolean betterdogs$statsApplied = false;

    @Unique
    private boolean betterdogs$initialized = false;

    // Dummy constructor required for extending TamableAnimal
    protected WolfMixin() {
        super(null, null);
    }

    // ========== WolfExtensions Implementation (delegates to persistence)
    // ==========

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
         WolfPersistentData current = WolfPersistentData.getWolfData((Wolf) (Object) this);
        WolfPersistentData.setScale(current.personalityId(), current.lastDamageTime(), current.submissive(),
                current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), (Wolf) (Object) this, scale, current.affinityMap(), current.leaderUuid());
    }

    // === SOCIAL BONDING & VISUALS (V3.1.37) ===

    @Override
    public int betterdogs$getAffinity(String targetUuid) {
        return WolfPersistentData.getPersistedAffinity((Wolf) (Object) this, targetUuid);
    }

    @Override
    public void betterdogs$adjustAffinity(String targetUuid, int delta) {
        WolfPersistentData.adjustPersistedAffinity((Wolf) (Object) this, targetUuid, delta);
    }

    @Override
    public UUID betterdogs$getLeaderUuid() {
        return WolfPersistentData.getPersistedLeaderUuid((Wolf) (Object) this).orElse(null);
    }

    @Override
    public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {
        WolfPersistentData.setPersistedLeaderUuid((Wolf) (Object) this, uuid);
    }

    // betterdogs$getSpeciesId removed as it is not part of WolfExtensions

    // betterdogs$asEntity removed as it is not part of WolfExtensions

    // ========== Dasik SocialEntity Implementation (Bridging) ==========

    @Override
    public long dasik$getDNA() {
        return betterdogs$getDNA();
    }

    @Override
    public String dasik$getSpeciesId() {
        return "wolf";
    }

    @Override
    public LivingEntity dasik$asEntity() {
        return (LivingEntity) (Object) this;
    }

    @Override
    public float dasik$getSocialScale() {
        return betterdogs$getSocialScale();
    }

    @Override
    public net.dasik.social.api.SocialScheduler dasik$getScheduler() {
        return betterdogs$getScheduler();
    }

    // ========== Dasik GroupMember Implementation (Leader-Follower) ==========

    @Unique
    private LivingEntity betterdogs$leader = null;

    @Unique
    private net.dasik.social.core.group.FlockState betterdogs$flockState = null;

    @Override
    public LivingEntity getLeader() {
        if (this.betterdogs$leader == null || !this.betterdogs$leader.isAlive()) {
            UUID uuid = betterdogs$getLeaderUuid();
            if (uuid != null && this.level() instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(uuid);
                if (entity instanceof LivingEntity living) {
                    this.betterdogs$leader = living;
                }
            }
        }
        return betterdogs$leader;
    }

    @Override
    public boolean hasLeader() {
        return betterdogs$getLeaderUuid() != null;
    }

    @Override
    public void setLeader(@Nullable LivingEntity leader) {
        this.betterdogs$leader = leader;
        betterdogs$setLeaderUuid(leader != null ? leader.getUUID() : null);
    }

    @Override
    public int getGroupSize() {
        // Sovereign Refactor (Build 7): Delegate to DasikLibrary FlockState.
        // The GroupManager computes flock size for the leader on a staggered schedule.
        // This avoids an O(N) bounding-box scan on every wolf tick.
        net.dasik.social.core.group.FlockState state = this.betterdogs$flockState;
        if (state != null) {
            // FlockState is computed on the leader; if we ARE the leader, use it.
            return state.getMemberCount();
        }
        // Follower: ask our leader for their FlockState.
        if (this.betterdogs$leader instanceof net.dasik.social.api.group.GroupMember leaderGM) {
            net.dasik.social.core.group.FlockState leaderState = leaderGM.getFlockState();
            if (leaderState != null) {
                return leaderState.getMemberCount();
            }
        }
        // No flock data available yet; return 1 (self).
        return 1;
    }

    @Override
    public FlockType getFlockType() {
        return FlockType.TERRESTRIAL;
    }

    @Override
    public net.dasik.social.core.group.FlockState getFlockState() {
        return betterdogs$flockState;
    }

    @Override
    public void setFlockState(net.dasik.social.core.group.FlockState state) {
        this.betterdogs$flockState = state;
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

    @Unique
    private LivingEntity betterdogs$socialTarget = null;
    @Unique
    private WolfExtensions.SocialAction betterdogs$socialAction = WolfExtensions.SocialAction.NONE;
    @Unique
    private int betterdogs$socialModeTimer = 0;

    @Override
    public void betterdogs$setSocialState(@Nullable LivingEntity target, WolfExtensions.SocialAction action,
            int maxDurationTicks) {
        if (target != null && action != WolfExtensions.SocialAction.NONE) {
            this.betterdogs$socialTarget = target;
            this.betterdogs$socialAction = action;
            this.betterdogs$socialModeTimer = maxDurationTicks;
        } else {
            this.betterdogs$socialTarget = null;
            this.betterdogs$socialAction = WolfExtensions.SocialAction.NONE;
            this.betterdogs$socialModeTimer = 0;
            ((Wolf) (Object) this).setTarget(null);
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
        return this.betterdogs$socialTarget != null && this.betterdogs$socialTarget.isAlive()
                && this.betterdogs$socialModeTimer > 0;
    }

    @Override
    public void betterdogs$tickSocialMode() {
        if (this.betterdogs$socialModeTimer > 0) {
            this.betterdogs$socialModeTimer--;
            if (this.betterdogs$socialModeTimer <= 0) {
                betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
            }
        } else if (this.betterdogs$socialTarget != null) {
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

            // Poll ambient triggers if idle
            betterdogs$pollAmbientEvents();

            if (betterdogs$socialScheduler.isIdle()) {
                betterdogs$socialScheduler = null;
            }
        }
    }

    @Unique
    private long betterdogs$lastBeggingTime = 0;

    @Unique
    private void betterdogs$pollAmbientEvents() {
        // Only pool if scheduler exists and no high priority blocking?
        // Dasik scheduler handles priority, so we can just offer events.
        // But we shouldn't spam offer every tick.
        if (this.tickCount % 20 != 0)
            return; // Poll every second

        // Random chance for some events or just condition checks
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.isOrderedToSit())
            return;

        // Begging
        if (wolf.isTame() && wolf.onGround() && !betterdogs$socialScheduler.isEventActive(BeggingDogEvent.ID)) {
            long currentTime = wolf.level().getGameTime();
            if (currentTime - betterdogs$lastBeggingTime >= 1200) { // 60 seconds cooldown
                betterdogs$lastBeggingTime = currentTime;
                betterdogs$socialScheduler.schedule(new BeggingDogEvent());
            }
        }

        // NEW: Morning Zoomies (v3.1.37)
        // Minecraft morning is roughly 0 - 3000 ticks (6:00 - 9:00 AM)
        long timeOfDay = wolf.level().getDefaultClockTime() % 24000;
        if (wolf.isTame() && wolf.onGround() && timeOfDay < 3000) {
            if (!betterdogs$socialScheduler.isEventActive(ZoomiesDogEvent.ID)) {
                // 1% chance every second during morning
                if (wolf.getRandom().nextFloat() < 0.01f) {
                    betterdogs$socialScheduler.schedule(new ZoomiesDogEvent());
                    WolfDebugLogger.log(wolf, "Ambient", "Morning zoomies triggered!");
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void betterdogs$onTick(CallbackInfo ci) {
        betterdogs$tickSocialMode();
    }

    @Unique
    private void betterdogs$applyPersonalityStats(WolfPersonality personality) {
        WolfStatManager.applyPersonalityStats((Wolf) (Object) this, personality);
    }

    @Inject(method = "finalizeSpawn", at = @At("RETURN"))
    private void betterdogs$onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData,
            CallbackInfoReturnable<SpawnGroupData> cir) {
        if (!level.getLevel().isClientSide()) {
            WolfPersonality personality = WolfPersonality.random(level.getLevel());
            betterdogs$setPersonality(personality);
            WolfDebugLogger.log((Wolf)(Object)this, "Spawn", "Personality assigned: " + personality.name());

            long dna = this.getRandom().nextLong();
            betterdogs$setDNA(dna);
            float scale = 0.9f + (this.getRandom().nextFloat() * 0.2f); // 0.9x to 1.1x
            betterdogs$setSocialScale(scale);

            net.dasik.social.core.SocialRegistry.register(this);
            WolfDebugLogger.log((Wolf)(Object)this, "Spawn", "Initialized with personality: " + personality.name());

            // Density Boost Logic (v3.3.0)
            if (spawnReason == EntitySpawnReason.NATURAL && groupData == null) {
                int boostChance = BetterDogsGameRules.getInt(level.getLevel(), BetterDogsGameRules.BD_WOLF_SPAWN_DENSITY_BOOST);
                int roll = this.getRandom().nextInt(100);
                WolfDebugLogger.log((Wolf)(Object)this, "Spawn", "Density Boost Evaluation - Roll: " + roll + " / Target: " + boostChance);
                if (roll < boostChance) {
                    this.betterdogs$triggerReinforcementSpawn(level);
                }
            }
        }
    }

    @Unique
    private void betterdogs$triggerReinforcementSpawn(ServerLevelAccessor level) {
        Wolf wolf = (Wolf) (Object) this;
        ServerLevel serverLevel = level.getLevel();
        BlockPos pos = wolf.blockPosition();

        // Find a spot nearby
        BlockPos spawnPos = pos.offset(
            this.getRandom().nextInt(16) - 8,
            0,
            this.getRandom().nextInt(16) - 8
        );

        // Spawn a reinforcement cluster
        int clusterSize = BetterDogsGameRules.getInt(serverLevel, BetterDogsGameRules.BD_WOLF_PACK_CLUSTER_SIZE);
        int count = clusterSize / 2 + 1;
        for (int i = 0; i < count; i++) {
             Wolf reinforcement = net.minecraft.world.entity.EntityType.WOLF.create(serverLevel, EntitySpawnReason.REINFORCEMENT);
             if (reinforcement != null) {
                 reinforcement.snapTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, this.getRandom().nextFloat() * 360.0F, 0.0F);
                 reinforcement.finalizeSpawn(level, serverLevel.getCurrentDifficultyAt(spawnPos), EntitySpawnReason.REINFORCEMENT, null);
                 serverLevel.addFreshEntityWithPassengers(reinforcement);
             }
        }
        WolfDebugLogger.log(wolf, "Spawn", "Density Boost: Reinforcement pack (" + count + ") spawned at " + spawnPos);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return BetterDogsGameRules.getInt(this.level(), BetterDogsGameRules.BD_WOLF_PACK_CLUSTER_SIZE);
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide()) {
            net.dasik.social.core.SocialRegistry.unregister(this);
        }
        super.remove(reason);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void betterdogs$registerCustomGoals(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;

        this.goalSelector.addGoal(8, new WolfGiftGoal(wolf));
        this.goalSelector.addGoal(1, new FleeCreeperGoal(wolf));
        this.goalSelector.addGoal(1, new AvoidHazardsGoal(wolf));
        this.goalSelector.addGoal(3, new EatGroundFoodGoal(wolf));
        this.goalSelector.addGoal(4, new WildWolfTerritorialGoal(wolf));
        this.goalSelector.addGoal(4, new WildWolfPackWarGoal(wolf));

        this.targetSelector.addGoal(2, new AggressiveTargetGoal(wolf));
        this.targetSelector.addGoal(2, new PacifistRevengeGoal(wolf));

        this.targetSelector.addGoal(1, new BloodFeudGoal(wolf));
        this.goalSelector.addGoal(0, new BabyBiteBackGoal(wolf));
        this.goalSelector.addGoal(4, new AdultCorrectionGoal(wolf));
        this.goalSelector.addGoal(4, new SmallFightGoal(wolf));
        this.goalSelector.addGoal(5, new BabyMischiefGoal(wolf));
        this.goalSelector.addGoal(6, new ZoomiesGoal(wolf));
        this.goalSelector.addGoal(7, new BeggingGoal(wolf));
        this.goalSelector.addGoal(7, new WolfFetchGoal(wolf));

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

        this.goalSelector.addGoal(7, new GroupHowlGoal(wolf));
        this.goalSelector.addGoal(7, new BabyCuriosityGoal(wolf, 0.8));
        this.goalSelector.addGoal(8, new WanderlustGoal(wolf, 1.0));
        
        // DasikLibrary Wild Pack Group Logic (Disabled automatically if Tamed)
        this.goalSelector.addGoal(5, new WildWolfFollowLeaderGoal(wolf));
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        if (player.getItemInHand(hand).getItem() instanceof DebugStickItem) {
            if (BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_DEBUGGING)) {
                if (!wolf.level().isClientSide()) {
                    if (player.isSecondaryUseActive()) {
                        // Shift + Click: Cycle Scale
                        float currentScale = betterdogs$getSocialScale();
                        float nextScale = currentScale + 0.1f;
                        if (nextScale > 1.5f) nextScale = 0.5f;
                        betterdogs$setSocialScale(nextScale);
                        player.sendOverlayMessage(Component.literal("§b[Debug] §fScale: " + String.format("%.1f", nextScale)));
                        WolfDebugLogger.log(wolf, "DebugStick", "Scale changed to " + nextScale);
                    } else {
                        // Normal Click: Cycle Personality
                        WolfPersonality current = betterdogs$getPersonality();
                        WolfPersonality next = current.next();
                        betterdogs$setPersonality(next);
                        // Force re-apply stats if tamed
                        if (wolf.isTame()) {
                            betterdogs$applyPersonalityStats(next);
                        }
                        player.sendOverlayMessage(Component.literal("§b[Debug] §fPersonality: " + next.name()));
                        WolfDebugLogger.log(wolf, "DebugStick", "Personality changed to " + next.name());
                    }
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "applyTamingSideEffects", at = @At("HEAD"))
    private void betterdogs$onApplyTamingSideEffects(CallbackInfo ci) {
        if (!this.isTame() || this.level().isClientSide())
            return;

        if (!betterdogs$hasPersonality()) {
            WolfPersonality personality = WolfPersonality.random(this.level());
            betterdogs$setPersonality(personality);
            WolfDebugLogger.log((Wolf)(Object)this, "Tame", "Assigned initial personality: " + personality.name());
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

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$tickHandler(CallbackInfo ci) {
        if (!this.level().isClientSide()) {
            if (!betterdogs$initialized || this.tickCount % 100 == 0) {
                if (!net.dasik.social.core.SocialRegistry.contains((net.dasik.social.api.SocialEntity) (Object) this)) {
                    net.dasik.social.core.SocialRegistry.register(this);
                }

                if (betterdogs$getDNA() == 0L) {
                    betterdogs$setDNA(this.getUUID().getMostSignificantBits());
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

        this.betterdogs$tickScheduler();

        Wolf wolf = (Wolf) (Object) this;

        if (BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_DEBUGGING) && this.tickCount % 20 == 0) {
            WolfPersonality personality = betterdogs$getPersonality();
            if (personality == WolfPersonality.AGGRESSIVE) {
                wolf.level().addParticle(ParticleTypes.FLAME, wolf.getRandomX(0.5), wolf.getRandomY() + 0.5, wolf.getRandomZ(0.5), 0, 0.05, 0);
            } else if (personality == WolfPersonality.PACIFIST) {
                wolf.level().addParticle(ParticleTypes.NOTE, wolf.getRandomX(0.5), wolf.getRandomY() + 0.5, wolf.getRandomZ(0.5), 0, 0.05, 0);
            } else {
                wolf.level().addParticle(ParticleTypes.HAPPY_VILLAGER, wolf.getRandomX(0.5), wolf.getRandomY() + 0.5, wolf.getRandomZ(0.5), 0, 0.05, 0);
            }
        }

        if (!this.level().isClientSide()) {
            betterdogs$checkTargetCliffSafety();
            betterdogs$checkMovementCliffSafety();
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

    @Unique
    private void betterdogs$checkMovementCliffSafety() {
        Wolf wolf = (Wolf) (Object) this;
        if (!BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_CLIFF_SAFETY))
            return;

        if (wolf.getDeltaMovement().horizontalDistanceSqr() < 0.0001)
            return;

        Vec3 velocity = wolf.getDeltaMovement();
        Vec3 lookaheadPos = wolf.position().add(velocity.scale(5.0));
        BlockPos hazardPos = BlockPos.containing(lookaheadPos);

        net.minecraft.world.level.Level level = wolf.level();

        boolean solidGround = false;
        for (int i = 0; i <= 3; i++) {
            if (!level.isEmptyBlock(hazardPos.below(i))) {
                solidGround = true;
                break;
            }
        }

        if (!solidGround) {
            wolf.getNavigation().stop();
            wolf.setDeltaMovement(Vec3.ZERO);
            wolf.setShiftKeyDown(true);
        }
    }

    @Unique
    private void betterdogs$checkTargetCliffSafety() {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.getTarget() == null)
            return;

        if (!BetterDogsGameRules.getBoolean(wolf.level(), BetterDogsGameRules.BD_CLIFF_SAFETY))
            return;

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
