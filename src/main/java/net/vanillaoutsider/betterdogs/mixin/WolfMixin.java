package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
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

        // Apply Base Speed Boost (20%) to all Better Dogs
        speedAttr.addPermanentModifier(new AttributeModifier(baseSpeedId, 0.20,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        if (knockbackAttr != null) {
            knockbackAttr.removeModifier(pacifistKnockbackId);
        }

        switch (personality) {
            case AGGRESSIVE -> {
                // +15% speed, -15% damage, +20 Max Health (Total 40)
                speedAttr.addPermanentModifier(new AttributeModifier(aggressiveSpeedId, 0.15,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                damageAttr.addPermanentModifier(new AttributeModifier(aggressiveDamageId, -0.15,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

                var healthAttr = this.getAttribute(Attributes.MAX_HEALTH);
                if (healthAttr != null) {
                    Identifier aggressiveHealthId = Identifier.parse(AGGRESSIVE_HEALTH_ID);
                    healthAttr.removeModifier(aggressiveHealthId);
                    healthAttr.addPermanentModifier(new AttributeModifier(aggressiveHealthId, 20.0,
                            AttributeModifier.Operation.ADD_VALUE));
                    // Heal to new max if needed
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.heal(20.0f);
                    }
                }
            }
            case PACIFIST -> {
                // -10% speed, +15% damage, +50% knockback
                speedAttr.addPermanentModifier(new AttributeModifier(pacifistSpeedId, -0.10,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                damageAttr.addPermanentModifier(new AttributeModifier(pacifistDamageId, 0.15,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                if (knockbackAttr != null) {
                    knockbackAttr.addPermanentModifier(new AttributeModifier(pacifistKnockbackId, 0.5,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
            }
            case NORMAL -> {
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

        // Add our custom hunting goal
        TargetingConditions.Selector preySelector = (entity, level) -> entity instanceof Sheep
                || entity instanceof Rabbit || entity instanceof Chicken;

        this.targetSelector.addGoal(4, new WildWolfHuntGoal<>(
                wolf,
                Animal.class,
                false,
                preySelector));
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

        if (!this.isTame())
            return;

        // Cliff Safety Protocol: Stop chasing if target is significantly below us
        // (likely fell off)
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.getTarget() != null) {
            double yDiff = wolf.getY() - wolf.getTarget().getY();
            if (yDiff > 3.0 && wolf.onGround()) {
                // Target is more than 3 blocks below and we are on solid ground
                // Stop navigation to prevent jumping off
                wolf.getNavigation().stop();
                wolf.setTarget(null);
            }
        }

        int currentTick = this.tickCount;
        int lastDamageTime = betterdogs$getLastDamageTime();

        if (currentTick - lastDamageTime > 60 && this.getHealth() < this.getMaxHealth()) {
            betterdogs$healTimer++;
            if (betterdogs$healTimer >= 1200) {
                this.heal(1.0f);
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
            if (this.isOwnedBy(player) && !player.isShiftKeyDown()) {
                ci.cancel(); // Cancel damage logic entirely
            }
        }
    }
}
