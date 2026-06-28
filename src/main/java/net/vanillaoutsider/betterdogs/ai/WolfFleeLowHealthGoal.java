// Verified against: WolfFleeLowHealthGoal.java (26.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * AI goal for all wolves (tamed or wild) to flee from combat when low health.
 * Triggers when health falls below 30% of max health.
 */
public class WolfFleeLowHealthGoal extends Goal {

    private final Wolf wolf;
    private final double speedModifier;
    private double posX;
    private double posY;
    private double posZ;

    public WolfFleeLowHealthGoal(Wolf wolf, double speedModifier) {
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Toggleable via gamerule
        if (!DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_FLEE_LOW_HEALTH)) {
            return false;
        }

        // Must be below 30% health
        float lowHealthThreshold = wolf.getMaxHealth() * 0.30f;
        if (wolf.getHealth() >= lowHealthThreshold) {
            return false;
        }

        // Check personality probability
        WolfExtensions ext = (WolfExtensions) wolf;
        WolfPersonality personality = ext.betterdogs$hasPersonality() 
                ? ext.betterdogs$getPersonality() 
                : WolfPersonality.NORMAL;

        int chance = switch (personality) {
            case PACIFIST -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_PACI_FLEE_CHANCE);
            case NORMAL -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_NORMAL_FLEE_CHANCE);
            case AGGRESSIVE -> DynamicGameRuleManager.getInt(wolf.level(), BetterDogsGameRules.BD_AGGRO_FLEE_CHANCE);
        };

        if (wolf.getRandom().nextInt(100) >= chance) {
            return false;
        }

        LivingEntity attacker = wolf.getLastHurtByMob();
        LivingEntity target = wolf.getTarget();
        LivingEntity avoidTarget = attacker != null ? attacker : target;

        Vec3 escapePos = null;
        if (avoidTarget != null) {
            escapePos = DefaultRandomPos.getPosAway(wolf, 10, 5, avoidTarget.position());
        }
        if (escapePos == null) {
            escapePos = DefaultRandomPos.getPos(wolf, 10, 5);
        }

        if (escapePos == null) {
            return false;
        }

        this.posX = escapePos.x;
        this.posY = escapePos.y;
        this.posZ = escapePos.z;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        float lowHealthThreshold = wolf.getMaxHealth() * 0.30f;
        if (wolf.getHealth() >= lowHealthThreshold) {
            return false;
        }
        return !wolf.getNavigation().isDone();
    }

    @Override
    public void start() {
        wolf.getNavigation().moveTo(this.posX, this.posY, this.posZ, this.speedModifier);
        // Clear active target to stop attacking while fleeing
        wolf.setTarget(null);
    }

    @Override
    public void stop() {
        wolf.getNavigation().stop();
    }
}
