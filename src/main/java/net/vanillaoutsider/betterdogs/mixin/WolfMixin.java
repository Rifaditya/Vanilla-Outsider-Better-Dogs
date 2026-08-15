// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.damagesource.DamageSource;
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
    }

    @Override
    public boolean betterdogs$hasPersonality() {
        return this.betterdogs$personality != null;
    }

    @Override
    public float betterdogs$getSocialScale() {
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
        if (!this.level().isClientSide() && this.betterdogs$passiveOverrideTicks > 0) {
            this.betterdogs$passiveOverrideTicks--;
        }
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void betterdogs$registerCustomGoals(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        this.goalSelector.getAvailableGoals().removeIf(wrapped -> {
            var goal = wrapped.getGoal();
            return goal instanceof FollowOwnerGoal || (goal instanceof WaterAvoidingRandomStrollGoal && !(goal instanceof TamedWanderNearOwnerGoal));
        });

        this.goalSelector.addGoal(1, new FleeCreeperGoal(wolf));
        this.goalSelector.addGoal(2, new AvoidHazardsGoal(wolf));
        this.goalSelector.addGoal(2, new WolfFleeLowHealthGoal(wolf, 1.4));
        this.goalSelector.addGoal(2, new WolfStormAnxietyGoal(wolf));
        this.goalSelector.addGoal(2, new net.vanillaoutsider.betterdogs.ai.WolfHornGoal(wolf));
        this.goalSelector.addGoal(3, new WolfFlankAttackGoal(wolf, 1.25));
        this.goalSelector.addGoal(4, new EatGroundFoodGoal(wolf, 1.25));
        this.goalSelector.addGoal(6, new PersonalityFollowOwnerGoal(wolf, 1.25, 2.0f, 50.0f));
        this.goalSelector.addGoal(7, new WolfBegGoal(wolf, 5.0F));
        this.goalSelector.addGoal(8, new TamedWanderNearOwnerGoal(wolf, 1.0));
        this.targetSelector.addGoal(1, new net.vanillaoutsider.betterdogs.ai.WolfNemesisTargetGoal(wolf));
        this.targetSelector.addGoal(2, new PacifistRevengeGoal(wolf));
        this.targetSelector.addGoal(3, new AggressiveTargetGoal(wolf));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void betterdogs$writeSaveData(ValueOutput output, CallbackInfo ci) {
        WolfPersistentData.writeToSaveData(output, betterdogs$getPersonality(), betterdogs$getSocialScale(), betterdogs$getDnaSeed(), betterdogs$getFavoriteTreat(), betterdogs$getSoothedTime(), betterdogs$getNemesisEntityType(), betterdogs$getNemesisExpiryTime());
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
    }
}
