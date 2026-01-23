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
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.ai.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.minecraft.resources.ResourceLocation;
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

    // ========== Stat Modifiers ==========

    @Unique
    private void betterdogs$applyPersonalityStats(WolfPersonality personality) {
        var speedAttr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        var damageAttr = this.getAttribute(Attributes.ATTACK_DAMAGE);
        var knockbackAttr = this.getAttribute(Attributes.ATTACK_KNOCKBACK);

        if (speedAttr == null || damageAttr == null)
            return;

        ResourceLocation aggressiveSpeedId = ResourceLocation.parse(AGGRESSIVE_SPEED_ID);
        ResourceLocation aggressiveDamageId = ResourceLocation.parse(AGGRESSIVE_DAMAGE_ID);
        ResourceLocation pacifistSpeedId = ResourceLocation.parse(PACIFIST_SPEED_ID);
        ResourceLocation pacifistDamageId = ResourceLocation.parse(PACIFIST_DAMAGE_ID);
        ResourceLocation pacifistKnockbackId = ResourceLocation.parse(PACIFIST_KNOCKBACK_ID);
        ResourceLocation baseSpeedId = ResourceLocation.parse(BASE_SPEED_ID);

        // Remove existing modifiers
        speedAttr.removeModifier(aggressiveSpeedId);
        speedAttr.removeModifier(pacifistSpeedId);
        speedAttr.removeModifier(baseSpeedId);
        damageAttr.removeModifier(aggressiveDamageId);
        damageAttr.removeModifier(pacifistDamageId);

        // Apply Base Speed Boost (Configurable) to all Better Dogs
        double speedBuff = BetterDogsConfig.get().getGlobalSpeedBuff();
        speedAttr.addPermanentModifier(new AttributeModifier(baseSpeedId, speedBuff,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        if (knockbackAttr != null) {
            knockbackAttr.removeModifier(pacifistKnockbackId);
        }

        switch (personality) {
            case AGGRESSIVE -> {
                // Configurable Modifiers
                double speedMod = BetterDogsConfig.get().getAggressiveSpeedModifier();
                double damageMod = BetterDogsConfig.get().getAggressiveDamageModifier();

                speedAttr.addPermanentModifier(new AttributeModifier(aggressiveSpeedId, speedMod,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                damageAttr.addPermanentModifier(new AttributeModifier(aggressiveDamageId, damageMod,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

                var healthAttr = this.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null) {
                    ResourceLocation aggressiveHealthId = ResourceLocation.parse(AGGRESSIVE_HEALTH_ID);
                    healthAttr.removeModifier(aggressiveHealthId);

                    double hpBonus = BetterDogsConfig.get().getAggressiveHealthBonus();
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
                double speedMod = BetterDogsConfig.get().getPacifistSpeedModifier();
                double damageMod = BetterDogsConfig.get().getPacifistDamageModifier();
                double kbMod = BetterDogsConfig.get().getPacifistKnockbackModifier();

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
                double speedMod = BetterDogsConfig.get().getNormalSpeedModifier();
                double damageMod = BetterDogsConfig.get().getNormalDamageModifier();
                double healthMod = BetterDogsConfig.get().getNormalHealthBonus();

                ResourceLocation normalSpeedId = ResourceLocation.parse(NORMAL_SPEED_ID);
                ResourceLocation normalDamageId = ResourceLocation.parse(NORMAL_DAMAGE_ID);
                ResourceLocation normalHealthId = ResourceLocation.parse(NORMAL_HEALTH_ID);

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

        // Safety: Flee from creepers (tamed only) - priority 1
        this.goalSelector.addGoal(1, new FleeCreeperGoal(wolf));

        // Safety: Avoid hazards (all wolves) - priority 1
        this.goalSelector.addGoal(1, new AvoidHazardsGoal(wolf));

        // Wild wolf: Eat food from ground - priority 3
        this.goalSelector.addGoal(3, new EatGroundFoodGoal(wolf));

        // 2. Add Aggressive Target Goal (Priority 2 - high priority)
        this.targetSelector.addGoal(2, new AggressiveTargetGoal(wolf));

        // Pacifist personality: Revenge for owner - priority 2
        this.targetSelector.addGoal(2, new PacifistRevengeGoal(wolf));

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
        if (BetterDogsConfig.get().getEnableStormAnxiety()) {
            this.goalSelector.addGoal(6, new WolfStormAnxietyGoal(wolf));
        }
    }

    // ========== On Tame - Assign Personality ==========

    @Override
    public void setTame(boolean tamed, boolean applyBoost) {
        super.setTame(tamed, applyBoost);

        if (!tamed)
            return;

        if (!betterdogs$hasPersonality()) {
            WolfPersonality personality = WolfPersonality.random();
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

        if (!this.isTame())
            return;

        // Cliff Safety Protocol: Stop chasing if target is significantly below us
        // (likely fell off)
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
                // Active Retreat: Move away from the cliff/danger
                Vec3 targetPos = wolf.getTarget().position();
                // Clear target so we stop chasing
                wolf.setTarget(null);

                // Find a spot away from the danger zone (range 4, vertical 1)
                Vec3 retreatPos = DefaultRandomPos.getPosAway(wolf, 4, 1, targetPos);
                if (retreatPos != null) {
                    wolf.getNavigation().moveTo(retreatPos.x, retreatPos.y, retreatPos.z, 1.0);
                }
                return;
            }
        }

        int currentTick = this.tickCount;
        int lastDamageTime = betterdogs$getLastDamageTime();

        if (currentTick - lastDamageTime > BetterDogsConfig.get().getCombatHealDelayTicks()
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

        if (this.isTame() && source.getEntity() instanceof Player player) {
            if (BetterDogsConfig.get().getEnableFriendlyFireProtection() && this.isOwnedBy(player)
                    && !player.isShiftKeyDown()) {
                ci.cancel(); // Cancel damage logic entirely
            }
        }
    }
}
