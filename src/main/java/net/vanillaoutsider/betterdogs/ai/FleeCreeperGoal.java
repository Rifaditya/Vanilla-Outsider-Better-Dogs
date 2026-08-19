// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.ai;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfParticleHelper;

/**
 * AI Goal for tamed wolves to flee from swelling/ignited creepers at 1.5x sprint speed,
 * emitting alarm whines and sprint smoke trails scaled by particle density settings.
 */
public class FleeCreeperGoal extends AvoidEntityGoal<Creeper> {

    public static final float DEFAULT_FLEE_DISTANCE = 10.0F;
    public static final double DEFAULT_SPRINT_SPEED = 1.5D;

    private final Wolf wolf;

    public FleeCreeperGoal(Wolf wolf) {
        super(wolf, Creeper.class, DEFAULT_FLEE_DISTANCE, DEFAULT_SPRINT_SPEED, DEFAULT_SPRINT_SPEED,
                FleeCreeperGoal::isThreateningCreeper);
        this.wolf = wolf;
    }

    /**
     * Testable, null-safe predicate checking whether a living entity is an active, threatening Creeper.
     */
    public static boolean isThreateningCreeper(LivingEntity livingEntity) {
        if (!(livingEntity instanceof Creeper creeper) || !creeper.isAlive()) {
            return false;
        }
        return creeper.isIgnited() || creeper.getSwellDir() > 0;
    }

    @Override
    public boolean canUse() {
        if (this.wolf == null || !this.wolf.isAlive() || !this.wolf.isTame()) {
            return false;
        }

        if (this.wolf.isOrderedToSit() || this.wolf.isInSittingPose() || this.wolf.isLeashed()) {
            return false;
        }

        if (this.wolf instanceof WolfExtensions ext && (ext.betterdogs$isGuardMode() || ext.betterdogs$isSittingManually())) {
            return false;
        }

        if (this.wolf.level() != null) {
            try {
                boolean awareness = DynamicGameRuleManager.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_CREEPER_AWARENESS);
                boolean evasion = DynamicGameRuleManager.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_CREEPER_EVASION_ENABLED);
                if (!awareness && !evasion) {
                    return false;
                }
            } catch (Throwable ignored) {
                // Test environment fallback
            }
        }

        return super.canUse();
    }

    @Override
    public void start() {
        super.start();
        playAlarmFeedback();
    }

    private void playAlarmFeedback() {
        if (this.wolf == null || this.wolf.level() == null || this.wolf.level().isClientSide()) {
            return;
        }
        if (this.wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
                    null,
                    this.wolf.getX(),
                    this.wolf.getY(),
                    this.wolf.getZ(),
                    SoundEvents.WOLF_WHINE_BABY.value(),
                    SoundSource.NEUTRAL,
                    0.9F,
                    (this.wolf.isBaby() ? 1.0F : 0.8F) + this.wolf.getRandom().nextFloat() * 0.2F
            );
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.toAvoid != null && this.wolf != null) {
            WolfParticleHelper.spawnParticles(
                    this.wolf,
                    ParticleTypes.SMOKE,
                    0.1,
                    0.1,
                    0.05,
                    0.1,
                    0.02
            );
        }
    }
}
