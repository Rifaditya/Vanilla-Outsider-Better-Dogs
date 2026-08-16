// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
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

    @Invoker("getVariant")
    public abstract net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfVariant> betterdogs$invokeGetVariant();

    @Invoker("setVariant")
    public abstract void betterdogs$invokeSetVariant(net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfVariant> variant);

    @Override
    public net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfVariant> betterdogs$getVariant() {
        return betterdogs$invokeGetVariant();
    }

    @Override
    public void betterdogs$setVariant(net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfVariant> variant) {
        betterdogs$invokeSetVariant(variant);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onHurtServer(net.minecraft.server.level.ServerLevel serverLevel, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (net.vanillaoutsider.betterdogs.util.WolfFriendlyFireHelper.shouldCancelDamage((Wolf) (Object) this, source)) {
            cir.setReturnValue(false);
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
        this.goalSelector.addGoal(5, new net.vanillaoutsider.betterdogs.ai.WolfGuardGoal(wolf));
        this.goalSelector.addGoal(5, new net.vanillaoutsider.betterdogs.ai.WildWolfTerritorialGoal(wolf));
        this.goalSelector.addGoal(6, new PersonalityFollowOwnerGoal(wolf, 1.25, 2.0f, 50.0f));
        this.goalSelector.addGoal(6, new net.vanillaoutsider.betterdogs.ai.GroupHowlGoal(wolf));
        this.goalSelector.addGoal(7, new WolfBegGoal(wolf, 5.0F));
        this.goalSelector.addGoal(8, new TamedWanderNearOwnerGoal(wolf, 1.0));
        this.targetSelector.addGoal(1, new net.vanillaoutsider.betterdogs.ai.WolfNemesisTargetGoal(wolf));
        this.targetSelector.addGoal(2, new PacifistRevengeGoal(wolf));
        this.targetSelector.addGoal(3, new AggressiveTargetGoal(wolf));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void betterdogs$writeSaveData(ValueOutput output, CallbackInfo ci) {
        WolfPersistentData.writeToSaveData(output, betterdogs$getPersonality(), betterdogs$getSocialScale(), betterdogs$getDnaSeed(), betterdogs$favoriteTreat, betterdogs$soothedTime, betterdogs$nemesisEntityType, betterdogs$nemesisExpiryTime, betterdogs$parentUUID1, betterdogs$parentUUID2, betterdogs$isInbred, betterdogs$isGuarding, betterdogs$guardPos, betterdogs$isUpForAdoption, betterdogs$lastGiftDay);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void betterdogs$readSaveData(ValueInput input, CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        this.betterdogs$personality = WolfPersistentData.readPersonalityFromSaveData(input);
        this.betterdogs$socialScale = WolfPersistentData.readSocialScaleFromSaveData(input);
        this.betterdogs$dnaSeed = WolfPersistentData.readDnaSeedFromSaveData(input, wolf.getUUID());
        this.betterdogs$favoriteTreat = WolfPersistentData.readFavoriteTreatFromSaveData(input);
        this.betterdogs$soothedTime = WolfPersistentData.readSoothedTimeFromSaveData(input);
        this.betterdogs$nemesisEntityType = WolfPersistentData.readNemesisTypeFromSaveData(input);
        this.betterdogs$nemesisExpiryTime = WolfPersistentData.readNemesisExpiryFromSaveData(input);
        this.betterdogs$parentUUID1 = WolfPersistentData.readParentUUID1FromSaveData(input);
        this.betterdogs$parentUUID2 = WolfPersistentData.readParentUUID2FromSaveData(input);
        this.betterdogs$isInbred = WolfPersistentData.readIsInbredFromSaveData(input);
        this.betterdogs$isGuarding = WolfPersistentData.readIsGuardingFromSaveData(input);
        this.betterdogs$guardPos = WolfPersistentData.readGuardPosFromSaveData(input);
        this.betterdogs$isUpForAdoption = WolfPersistentData.readIsUpForAdoptionFromSaveData(input);
        this.betterdogs$lastGiftDay = WolfPersistentData.readLastGiftDayFromSaveData(input);
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
