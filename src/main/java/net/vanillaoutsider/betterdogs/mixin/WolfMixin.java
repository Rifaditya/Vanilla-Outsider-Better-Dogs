// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.ai.AggressiveTargetGoal;
import net.vanillaoutsider.betterdogs.ai.AvoidHazardsGoal;
import net.vanillaoutsider.betterdogs.ai.EatGroundFoodGoal;
import net.vanillaoutsider.betterdogs.ai.FleeCreeperGoal;
import net.vanillaoutsider.betterdogs.ai.PacifistRevengeGoal;
import net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal;
import net.vanillaoutsider.betterdogs.ai.TamedWanderNearOwnerGoal;
import net.vanillaoutsider.betterdogs.ai.WolfBegGoal;
import net.vanillaoutsider.betterdogs.ai.WolfFleeLowHealthGoal;
import net.vanillaoutsider.betterdogs.ai.WolfFlankAttackGoal;
import net.vanillaoutsider.betterdogs.ai.WolfStormAnxietyGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public abstract class WolfMixin extends net.minecraft.world.entity.TamableAnimal implements WolfExtensions {

    protected WolfMixin(net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.TamableAnimal> entityType, net.minecraft.world.level.Level level) {
        super(entityType, level);
    }

    @Unique
    private WolfPersonality betterdogs$personality = null;

    @Unique
    private float betterdogs$socialScale = 1.0f;

    @Unique
    private long betterdogs$dnaSeed = 0L;

    @Unique
    private String betterdogs$favoriteTreat = "";

    @Unique
    private long betterdogs$soothedTime = 0L;

    @Unique
    private net.minecraft.core.BlockPos betterdogs$soundLocationTarget = null;

    @Unique
    private int betterdogs$passiveOverrideTicks = 0;

    @Unique
    private String betterdogs$nemesisEntityType = "";

    @Unique
    private long betterdogs$nemesisExpiryTime = 0L;

    @Unique
    private java.util.UUID betterdogs$parentUUID1 = null;

    @Unique
    private java.util.UUID betterdogs$parentUUID2 = null;

    @Unique
    private boolean betterdogs$isInbred = false;

    @Unique
    private boolean betterdogs$isGuarding = false;

    @Unique
    private net.minecraft.core.BlockPos betterdogs$guardPos = null;

    @Unique
    private boolean betterdogs$isUpForAdoption = false;

    @Unique
    private long betterdogs$lastGiftDay = -1L;

    @Unique
    private int betterdogs$zoomiesTicks = 0;

    @Unique
    private int betterdogs$calmTicks = 0;

    @Unique
    private int betterdogs$howlingTicks = 0;

    @Unique
    private boolean betterdogs$hasFetchedStick = false;

    @Unique
    private int betterdogs$feedCount = 0;

    @Unique
    private net.minecraft.world.entity.LivingEntity betterdogs$retaliationTarget = null;

    @Unique
    private int betterdogs$retaliationTicks = 0;

    @Override
    public net.minecraft.world.entity.LivingEntity betterdogs$getRetaliationTarget() {
        return this.betterdogs$retaliationTarget;
    }

    @Override
    public void betterdogs$setRetaliationTarget(net.minecraft.world.entity.LivingEntity target, int ticks) {
        this.betterdogs$retaliationTarget = target;
        this.betterdogs$retaliationTicks = Math.max(0, ticks);
    }

    @Override
    public int betterdogs$getRetaliationTicks() {
        return this.betterdogs$retaliationTicks;
    }

    @Unique
    private String betterdogs$bloodFeudTarget = "";

    @Override
    public String betterdogs$getBloodFeudTarget() {
        return this.betterdogs$bloodFeudTarget;
    }

    @Override
    public void betterdogs$setBloodFeudTarget(String targetUuid) {
        this.betterdogs$bloodFeudTarget = targetUuid != null ? targetUuid : "";
    }

    @Override
    public boolean betterdogs$hasBloodFeud() {
        return !this.betterdogs$bloodFeudTarget.isEmpty();
    }

    @Unique
    private int betterdogs$playFightCooldown = 0;

    @Override
    public int betterdogs$getPlayFightCooldown() {
        return this.betterdogs$playFightCooldown;
    }

    @Override
    public void betterdogs$setPlayFightCooldown(int ticks) {
        this.betterdogs$playFightCooldown = Math.max(0, ticks);
    }

    @Unique
    private java.util.UUID betterdogs$leaderUuid = null;
    @Unique
    private boolean betterdogs$isPackLeader = false;

    @Override
    public java.util.UUID betterdogs$getLeaderUUID() {
        return this.betterdogs$leaderUuid;
    }

    @Override
    public void betterdogs$setLeaderUUID(java.util.UUID uuid) {
        this.betterdogs$leaderUuid = uuid;
    }

    @Override
    public boolean betterdogs$isPackLeader() {
        return this.betterdogs$isPackLeader;
    }

    @Override
    public void betterdogs$setPackLeader(boolean isLeader) {
        this.betterdogs$isPackLeader = isLeader;
    }

    @Unique
    private int betterdogs$wanderlustTicks = 0;

    @Override
    public int betterdogs$getWanderlustTicks() {
        return this.betterdogs$wanderlustTicks;
    }

    @Override
    public void betterdogs$setWanderlustTicks(int ticks) {
        this.betterdogs$wanderlustTicks = Math.max(0, ticks);
    }

    @Override
    public int betterdogs$getFeedCount() {
        return this.betterdogs$feedCount;
    }

    @Override
    public void betterdogs$setFeedCount(int count) {
        this.betterdogs$feedCount = Math.max(0, count);
    }

    @Override
    public int betterdogs$getHowlingTicks() {
        return this.betterdogs$howlingTicks;
    }

    @Override
    public void betterdogs$setHowlingTicks(int ticks) {
        this.betterdogs$howlingTicks = ticks;
    }

    @Override
    public int betterdogs$getCalmTicks() {
        return this.betterdogs$calmTicks;
    }

    @Override
    public void betterdogs$setCalmTicks(int ticks) {
        this.betterdogs$calmTicks = ticks;
    }

    @Override
    public int betterdogs$getZoomiesTicks() {
        return this.betterdogs$zoomiesTicks;
    }

    @Override
    public void betterdogs$setZoomiesTicks(int ticks) {
        this.betterdogs$zoomiesTicks = ticks;
    }

    @Override
    public boolean betterdogs$hasFetchedStick() {
        return this.betterdogs$hasFetchedStick;
    }

    @Override
    public void betterdogs$setHasFetchedStick(boolean fetched) {
        this.betterdogs$hasFetchedStick = fetched;
    }

    @Override
    public long betterdogs$getLastGiftDay() {
        return this.betterdogs$lastGiftDay;
    }

    @Override
    public void betterdogs$setLastGiftDay(long day) {
        this.betterdogs$lastGiftDay = day;
    }

    @Override
    public boolean betterdogs$isUpForAdoption() {
        return this.betterdogs$isUpForAdoption;
    }

    @Override
    public void betterdogs$setUpForAdoption(boolean adoption) {
        this.betterdogs$isUpForAdoption = adoption;
    }

    @Override
    public boolean betterdogs$isGuarding() {
        return this.betterdogs$isGuarding;
    }

    @Override
    public void betterdogs$setGuarding(boolean guarding) {
        this.betterdogs$isGuarding = guarding;
    }

    @Override
    public net.minecraft.core.BlockPos betterdogs$getGuardPos() {
        return this.betterdogs$guardPos;
    }

    @Override
    public void betterdogs$setGuardPos(net.minecraft.core.BlockPos pos) {
        this.betterdogs$guardPos = pos;
    }

    @Override
    public java.util.UUID betterdogs$getParentUUID1() {
        return this.betterdogs$parentUUID1;
    }

    @Override
    public void betterdogs$setParentUUID1(java.util.UUID uuid) {
        this.betterdogs$parentUUID1 = uuid;
    }

    @Override
    public java.util.UUID betterdogs$getParentUUID2() {
        return this.betterdogs$parentUUID2;
    }

    @Override
    public void betterdogs$setParentUUID2(java.util.UUID uuid) {
        this.betterdogs$parentUUID2 = uuid;
    }

    @Override
    public boolean betterdogs$isInbred() {
        return this.betterdogs$isInbred;
    }

    @Override
    public void betterdogs$setInbred(boolean inbred) {
        this.betterdogs$isInbred = inbred;
    }

    @Override
    public WolfPersonality betterdogs$getPersonality() {
        if (this.betterdogs$personality == null) {
            Wolf wolf = (Wolf) (Object) this;
            this.betterdogs$personality = WolfPersonality.random(wolf.getRandom());
        }
        return this.betterdogs$personality;
    }

    @Override
    public void betterdogs$setPersonality(WolfPersonality personality) {
        this.betterdogs$personality = personality != null ? personality : WolfPersonality.NORMAL;
        Wolf wolf = (Wolf) (Object) this;
        net.vanillaoutsider.betterdogs.util.WolfPersonalityStatHelper.applyPersonalityStats(wolf, this.betterdogs$personality);
    }

    @Override
    public boolean betterdogs$hasPersonality() {
        return this.betterdogs$personality != null;
    }

    @Override
    public float betterdogs$getSocialScale() {
        if (this.betterdogs$socialScale <= 0.0f) {
            Wolf wolf = (Wolf) (Object) this;
            this.betterdogs$socialScale = net.vanillaoutsider.betterdogs.util.WolfScaleGeneticsHelper.generateWildWolfScale(wolf.level(), wolf.getRandom());
        }
        return this.betterdogs$socialScale;
    }

    @Override
    public void betterdogs$setSocialScale(float scale) {
        this.betterdogs$socialScale = scale;
    }

    @Override
    public long betterdogs$getDnaSeed() {
        if (this.betterdogs$dnaSeed == 0L) {
            Wolf wolf = (Wolf) (Object) this;
            this.betterdogs$dnaSeed = WolfPersistentData.generateDnaSeed(wolf.getUUID());
        }
        return this.betterdogs$dnaSeed;
    }

    @Override
    public String betterdogs$getFavoriteTreat() {
        return this.betterdogs$favoriteTreat != null ? this.betterdogs$favoriteTreat : "";
    }

    @Override
    public void betterdogs$setFavoriteTreat(String treat) {
        this.betterdogs$favoriteTreat = treat != null ? treat : "";
    }

    @Override
    public long betterdogs$getSoothedTime() {
        return this.betterdogs$soothedTime;
    }

    @Override
    public void betterdogs$setSoothedTime(long time) {
        this.betterdogs$soothedTime = time;
    }

    @Override
    public net.minecraft.core.BlockPos betterdogs$getSoundLocationTarget() {
        return this.betterdogs$soundLocationTarget;
    }

    @Override
    public void betterdogs$setSoundLocationTarget(net.minecraft.core.BlockPos pos) {
        this.betterdogs$soundLocationTarget = pos;
    }

    @Override
    public int betterdogs$getPassiveOverrideTicks() {
        return this.betterdogs$passiveOverrideTicks;
    }

    @Override
    public void betterdogs$setPassiveOverrideTicks(int ticks) {
        this.betterdogs$passiveOverrideTicks = ticks;
    }

    @Override
    public String betterdogs$getNemesisEntityType() {
        return this.betterdogs$nemesisEntityType;
    }

    @Override
    public void betterdogs$setNemesisEntityType(String type) {
        this.betterdogs$nemesisEntityType = type != null ? type : "";
    }

    @Override
    public long betterdogs$getNemesisExpiryTime() {
        return this.betterdogs$nemesisExpiryTime;
    }

    @Override
    public void betterdogs$setNemesisExpiryTime(long time) {
        this.betterdogs$nemesisExpiryTime = time;
    }

    @Invoker("setCollarColor")
    public abstract void betterdogs$invokeSetCollarColor(DyeColor color);

    @Override
    public void betterdogs$setCollarColor(DyeColor color) {
        betterdogs$invokeSetCollarColor(color);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        Wolf wolf = (Wolf) (Object) this;
        if (net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper.shouldCancelDamage(wolf, source)) {
            cir.setReturnValue(false);
            return;
        }

        if (source.getEntity() != null && source.getEntity() == wolf.getOwner()) {
            if (net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.getBoolean(wolf.level(), net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_DEMERIT_ACCIDENTAL_ATTACKS, true)) {
                this.betterdogs$feedCount = 0;
            }
        }

        if (wolf.isBaby() && this.betterdogs$personality == WolfPersonality.AGGRESSIVE && source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker && attacker != wolf) {
            int retaliateChance = net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.getInt(wolf.level(), net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_BABY_RETALIATE_PERCENT, 50);
            if (wolf.getRandom().nextInt(100) < retaliateChance) {
                this.betterdogs$retaliationTarget = attacker;
                this.betterdogs$retaliationTicks = 100;
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void betterdogs$onDie(DamageSource source, CallbackInfo ci) {
        net.vanillaoutsider.betterdogs.util.WolfNemesisHelper.recordNemesis((Wolf) (Object) this, source);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$onTick(CallbackInfo ci) {
        if (!this.level().isClientSide()) {
            if (this.betterdogs$passiveOverrideTicks > 0) {
                this.betterdogs$passiveOverrideTicks--;
            }
            if (this.betterdogs$zoomiesTicks > 0) {
                this.betterdogs$zoomiesTicks--;
            }
            if (this.betterdogs$calmTicks > 0) {
                this.betterdogs$calmTicks--;
            }
            if (this.betterdogs$howlingTicks > 0) {
                this.betterdogs$howlingTicks--;
            }
            if (this.betterdogs$playFightCooldown > 0) {
                this.betterdogs$playFightCooldown--;
            }
            if (this.betterdogs$retaliationTicks > 0) {
                this.betterdogs$retaliationTicks--;
                if (this.betterdogs$retaliationTicks == 0) {
                    this.betterdogs$retaliationTarget = null;
                }
            }
            if (this.betterdogs$wanderlustTicks > 0) {
                this.betterdogs$wanderlustTicks--;
            } else {
                Wolf wolf = (Wolf) (Object) this;
                if (wolf.isTame() && !wolf.isOrderedToSit() && !wolf.isLeashed() && wolf.getTarget() == null && !this.betterdogs$isGuarding) {
                    if (wolf.getRandom().nextInt(400) == 0) {
                        this.betterdogs$wanderlustTicks = 200;
                    }
                }
            }
        }
        net.vanillaoutsider.betterdogs.util.WolfInbreedingHelper.tickRuntAmbientParticles((Wolf) (Object) this);
        net.vanillaoutsider.betterdogs.util.WolfAdoptionHelper.tickAdoptionAmbientParticles((Wolf) (Object) this);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void betterdogs$registerCustomGoals(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        this.goalSelector.getAvailableGoals().removeIf(wrapped -> {
            var goal = wrapped.getGoal();
            return goal instanceof FollowOwnerGoal || (goal instanceof WaterAvoidingRandomStrollGoal && !(goal instanceof TamedWanderNearOwnerGoal));
        });

        this.goalSelector.addGoal(1, new net.vanillaoutsider.betterdogs.ai.MoveToVehicleGoal(wolf));
        this.goalSelector.addGoal(1, new FleeCreeperGoal(wolf));
        this.goalSelector.addGoal(2, new AvoidHazardsGoal(wolf));
        this.goalSelector.addGoal(2, new WolfFleeLowHealthGoal(wolf, 1.4));
        this.goalSelector.addGoal(2, new WolfStormAnxietyGoal(wolf));
        this.goalSelector.addGoal(2, new net.vanillaoutsider.betterdogs.ai.WolfHornGoal(wolf));
        this.goalSelector.addGoal(2, new net.vanillaoutsider.betterdogs.ai.ZoomiesGoal(wolf));
        this.goalSelector.addGoal(3, new WolfFlankAttackGoal(wolf, 1.25));
        this.goalSelector.addGoal(3, new net.vanillaoutsider.betterdogs.ai.WolfFetchGoal(wolf));
        this.goalSelector.addGoal(3, new net.vanillaoutsider.betterdogs.ai.AdultCorrectionGoal(wolf));
        this.goalSelector.addGoal(4, new EatGroundFoodGoal(wolf, 1.25));
        this.goalSelector.addGoal(4, new net.vanillaoutsider.betterdogs.ai.WolfGiftGoal(wolf));
        this.goalSelector.addGoal(4, new net.vanillaoutsider.betterdogs.ai.BabyMischiefGoal(wolf));
        this.goalSelector.addGoal(4, new net.vanillaoutsider.betterdogs.ai.BabyCuriosityGoal(wolf, 0.9));
        this.goalSelector.addGoal(4, new net.vanillaoutsider.betterdogs.ai.BabyBiteBackGoal(wolf, 1.25));
        this.goalSelector.addGoal(4, new net.vanillaoutsider.betterdogs.ai.SmallFightGoal(wolf));
        this.goalSelector.addGoal(4, new net.vanillaoutsider.betterdogs.ai.WildWolfPackWarGoal(wolf));
        this.goalSelector.addGoal(5, new net.vanillaoutsider.betterdogs.ai.WolfGuardGoal(wolf));
        this.goalSelector.addGoal(5, new net.vanillaoutsider.betterdogs.ai.WildWolfTerritorialGoal(wolf));
        this.goalSelector.addGoal(5, new net.vanillaoutsider.betterdogs.ai.WildWolfFollowLeaderGoal(wolf));
        this.goalSelector.addGoal(6, new PersonalityFollowOwnerGoal(wolf, 1.25, 2.0f, 50.0f));
        this.goalSelector.addGoal(6, new net.vanillaoutsider.betterdogs.ai.GroupHowlGoal(wolf));
        this.goalSelector.addGoal(7, new WolfBegGoal(wolf, 5.0F));
        this.goalSelector.addGoal(8, new TamedWanderNearOwnerGoal(wolf, 1.0));
        this.goalSelector.addGoal(8, new net.vanillaoutsider.betterdogs.ai.WanderlustGoal(wolf, 1.0));
        this.targetSelector.addGoal(1, new net.vanillaoutsider.betterdogs.ai.BloodFeudGoal(wolf));
        this.targetSelector.addGoal(2, new net.vanillaoutsider.betterdogs.ai.WolfNemesisTargetGoal(wolf));
        this.targetSelector.addGoal(3, new PacifistRevengeGoal(wolf));
        this.targetSelector.addGoal(4, new AggressiveTargetGoal(wolf));
        this.targetSelector.addGoal(5, new net.vanillaoutsider.betterdogs.ai.HuntWhenHurtGoal(wolf));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"), require = 0)
    private void betterdogs$writeNbt(CompoundTag tag, CallbackInfo ci) {
        WolfPersistentData.writeToNbt(tag, betterdogs$getPersonality(), betterdogs$getSocialScale(), betterdogs$getDnaSeed(), betterdogs$favoriteTreat, betterdogs$soothedTime, betterdogs$nemesisEntityType, betterdogs$nemesisExpiryTime, betterdogs$parentUUID1, betterdogs$parentUUID2, betterdogs$isInbred, betterdogs$isGuarding, betterdogs$guardPos, betterdogs$isUpForAdoption, betterdogs$lastGiftDay, betterdogs$feedCount, betterdogs$getBloodFeudTarget());
        WolfPersistentData.writeLeaderDataToNbt(tag, this.betterdogs$leaderUuid, this.betterdogs$isPackLeader);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"), require = 0)
    private void betterdogs$readNbt(CompoundTag tag, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        this.betterdogs$personality = WolfPersistentData.readPersonalityFromNbt(tag);
        this.betterdogs$socialScale = WolfPersistentData.readSocialScaleFromNbt(tag);
        this.betterdogs$dnaSeed = WolfPersistentData.readDnaSeedFromNbt(tag, wolf.getUUID());
        this.betterdogs$favoriteTreat = WolfPersistentData.readFavoriteTreatFromNbt(tag);
        this.betterdogs$soothedTime = WolfPersistentData.readSoothedTimeFromNbt(tag);
        this.betterdogs$nemesisEntityType = WolfPersistentData.readNemesisTypeFromNbt(tag);
        this.betterdogs$nemesisExpiryTime = WolfPersistentData.readNemesisExpiryFromNbt(tag);
        this.betterdogs$parentUUID1 = WolfPersistentData.readParentUUID1FromNbt(tag);
        this.betterdogs$parentUUID2 = WolfPersistentData.readParentUUID2FromNbt(tag);
        this.betterdogs$isInbred = WolfPersistentData.readIsInbredFromNbt(tag);
        this.betterdogs$isGuarding = WolfPersistentData.readIsGuardingFromNbt(tag);
        this.betterdogs$guardPos = WolfPersistentData.readGuardPosFromNbt(tag);
        this.betterdogs$isUpForAdoption = WolfPersistentData.readIsUpForAdoptionFromNbt(tag);
        this.betterdogs$lastGiftDay = WolfPersistentData.readLastGiftDayFromNbt(tag);
        this.betterdogs$feedCount = WolfPersistentData.readFeedCountFromNbt(tag);
        this.betterdogs$bloodFeudTarget = WolfPersistentData.readBloodFeudTargetFromNbt(tag);
        this.betterdogs$leaderUuid = WolfPersistentData.readLeaderUUIDFromNbt(tag);
        this.betterdogs$isPackLeader = WolfPersistentData.readIsPackLeaderFromNbt(tag);
        net.vanillaoutsider.betterdogs.util.WolfPersonalityStatHelper.applyPersonalityStats(wolf, this.betterdogs$personality);
    }

    @Inject(method = "getBreedOffspring", at = @At("RETURN"))
    private void betterdogs$onBreedOffspring(ServerLevel level, AgeableMob otherParent, CallbackInfoReturnable<Wolf> cir) {
        Wolf child = cir.getReturnValue();
        if (child != null && child instanceof WolfExtensions childExt) {
            Wolf parentA = (Wolf) (Object) this;
            WolfPersonality personalityA = parentA instanceof WolfExtensions extA ? extA.betterdogs$getPersonality() : WolfPersonality.NORMAL;
            WolfPersonality personalityB = otherParent instanceof WolfExtensions extB ? extB.betterdogs$getPersonality() : WolfPersonality.NORMAL;
            WolfPersonality inherited = net.vanillaoutsider.betterdogs.util.WolfGeneticsHelper.calculateOffspringPersonality(level, personalityA, personalityB, child.getRandom());
            childExt.betterdogs$setPersonality(inherited);

            float scaleA = parentA instanceof WolfExtensions extA ? extA.betterdogs$getSocialScale() : 1.0f;
            float scaleB = otherParent instanceof WolfExtensions extB ? extB.betterdogs$getSocialScale() : 1.0f;
            float inheritedScale = net.vanillaoutsider.betterdogs.util.WolfScaleGeneticsHelper.calculateOffspringScale(level, scaleA, scaleB, child.getRandom());
            childExt.betterdogs$setSocialScale(inheritedScale);

            if (inheritedScale >= 1.25f && parentA.getOwner() instanceof net.minecraft.world.entity.player.Player player) {
                net.vanillaoutsider.betterdogs.util.WolfAdvancementHelper.grantAdvancement(player, "giant_lineage");
            }

            net.vanillaoutsider.betterdogs.util.WolfInbreedingHelper.applyInbreeding(child, parentA, otherParent);
            if (otherParent instanceof Wolf wolfParentB) {
                net.vanillaoutsider.betterdogs.util.WolfCoatVariantHelper.assignPuppyVariant(child, parentA, wolfParentB);
            }
            net.vanillaoutsider.betterdogs.util.WolfLitterHelper.spawnExtraPuppies(level, parentA, otherParent, child);
        }
    }
}
