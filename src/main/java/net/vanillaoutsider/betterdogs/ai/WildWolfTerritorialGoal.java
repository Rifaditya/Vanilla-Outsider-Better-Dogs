package net.vanillaoutsider.betterdogs.ai;

import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.dasik.social.api.group.GroupMember;

import java.util.EnumSet;

/**
 * Wild Wolf Territorial Rivalry.
 * If two wild pack leaders meet within 96 blocks, they must either fight or retreat.
 * Outcome depends on personalities:
 * - Aggressive leaders will fight for dominance until one yields (merging packs).
 * - Pacifist/Normal leaders will retreat in opposite directions to maintain territory.
 */
public class WildWolfTerritorialGoal extends Goal {
    private final Wolf wolf;
    private LivingEntity rival;
    private double searchRadius = 96.0;
    private int cooldown = 0;
    private Behavior behavior = Behavior.STARE;
    private int behaviorTicks = 0;
    private boolean isFatal = false;

    private enum Behavior {
        STARE,
        FIGHT,
        RETREAT
    }

    private final TargetingConditions targeting;

    public WildWolfTerritorialGoal(Wolf wolf) {
        this.wolf = wolf;
        this.targeting = TargetingConditions.forNonCombat().range(searchRadius).selector((living, level) -> {
            if (!(living instanceof Wolf other)) return false;
            if (other.isTame() || ((GroupMember)other).getLeader() != null || other == this.wolf) return false;
            
            // Exclusive Disputes Check (v3.3.1)
            if (BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_TERRITORIAL_EXCLUSIVE_DISPUTES)) {
                WolfExtensions otherExt = (WolfExtensions) other;
                // Rival is busy with someone else
                if (otherExt.betterdogs$getSocialAction() != WolfExtensions.SocialAction.NONE && otherExt.betterdogs$getSocialTarget() != this.wolf) {
                    if (BetterDogsGameRules.getBoolean(level, BetterDogsGameRules.BD_DEBUGGING)) {
                        net.vanillaoutsider.betterdogs.BetterDogs.LOGGER.info("[Territory Debug] " + this.wolf.getName().getString() + " skipped rival " + other.getName().getString() + " (REASON: Busy with " + (otherExt.betterdogs$getSocialTarget() != null ? otherExt.betterdogs$getSocialTarget().getName().getString() : "Unknown") + ")");
                    }
                    return false;
                }
            }
            return true;
        });
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!BetterDogsGameRules.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_TERRITORIAL_RIVALRY)) {
            return false;
        }
        if (this.wolf.isTame() || ((GroupMember)this.wolf).getLeader() != null) {
            return false;
        }
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        if (this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            this.searchRadius = BetterDogsGameRules.getInt(serverLevel, BetterDogsGameRules.BD_TERRITORIAL_SEARCH_RADIUS);
            this.rival = serverLevel.getNearestEntity(Wolf.class, this.targeting.range(this.searchRadius), this.wolf, this.wolf.getX(), this.wolf.getY(), this.wolf.getZ(), this.wolf.getBoundingBox().inflate(this.searchRadius));
            if (this.rival != null) {
                this.debugLog("Targeting rival leader: " + this.rival.getName().getString() + " at " + this.rival.position());
            }
        }
        return this.rival != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.rival == null || !this.rival.isAlive() || this.wolf.isTame() || ((GroupMember)this.wolf).getLeader() != null) {
            return false;
        }
        // Goal finishes if they are far enough apart after a retreat
        if (this.behavior == Behavior.RETREAT && this.wolf.distanceToSqr(this.rival) > searchRadius * searchRadius) {
            return false;
        }
        return this.behaviorTicks < 1200; // Max 1 minute conflict
    }

    @Override
    public void start() {
        this.behavior = Behavior.STARE;
        this.behaviorTicks = 0;
        this.wolf.getNavigation().stop();
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(this.rival, WolfExtensions.SocialAction.TERRITORIAL_DISPUTE, 1200);
        }
    }

    @Override
    public void stop() {
        this.rival = null;
        this.cooldown = 400; // Long cooldown between territory disputes
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
    }

    @Override
    public void tick() {
        this.behaviorTicks++;
        if (this.rival == null) return;

        this.wolf.getLookControl().setLookAt(this.rival, 30.0f, 30.0f);

        switch (this.behavior) {
            case STARE -> {
                if (this.behaviorTicks > 40) { // 2 seconds stare-down
                    this.decideOutcome();
                }
            }
            case FIGHT -> {
                this.wolf.getNavigation().moveTo(this.rival, 1.3);
                if (this.wolf.distanceToSqr(this.rival) < 4.0) {
                    if (this.behaviorTicks % 20 == 0 && this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        this.wolf.doHurtTarget(serverLevel, this.rival);
                    }
                }
                // Check if someone yields (or dies if fatal)
                if (!this.isFatal && this.rival.getHealth() < this.rival.getMaxHealth() * 0.4) {
                    this.winConflict();
                } else if (this.isFatal && (this.rival.isRemoved() || !this.rival.isAlive())) {
                    this.winConflict();
                }
            }
            case RETREAT -> {
                if (this.wolf.getNavigation().isDone() || this.behaviorTicks % 40 == 0) {
                    Vec3 awayPos = DefaultRandomPos.getPosAway(this.wolf, 24, 7, this.rival.position());
                    if (awayPos != null) {
                        this.wolf.getNavigation().moveTo(awayPos.x, awayPos.y, awayPos.z, 1.2);
                    }
                }
            }
        }
    }

    private void decideOutcome() {
        WolfExtensions ext = (WolfExtensions) this.wolf;
        WolfExtensions rivalExt = (WolfExtensions) this.rival;

        // Use deterministic seed for both wolves to ensure synchronized decisions
        long seed = (long) Math.min(this.wolf.getId(), this.rival.getId()) << 32 | (long) Math.max(this.wolf.getId(), this.rival.getId());
        java.util.Random seededRandom = new java.util.Random(seed);

        // Deterministic order: roll for the lower ID wolf first, then higher ID
        Wolf lowerIdWolf = this.wolf.getId() < this.rival.getId() ? this.wolf : (Wolf) this.rival;
        Wolf higherIdWolf = this.wolf.getId() < this.rival.getId() ? (Wolf) this.rival : this.wolf;

        boolean lowerWantsWar = wantsWar(lowerIdWolf, (WolfExtensions) lowerIdWolf, seededRandom);
        boolean higherWantsWar = wantsWar(higherIdWolf, (WolfExtensions) higherIdWolf, seededRandom);

        this.debugLog("Decision Phase: " + lowerIdWolf.getName().getString() + " (Wants War: " + lowerWantsWar + ") vs " + higherIdWolf.getName().getString() + " (Wants War: " + higherWantsWar + ")");

        boolean iAmLower = this.wolf == lowerIdWolf;
        boolean iWantWar = iAmLower ? lowerWantsWar : higherWantsWar;
        boolean rivalWantsWar = iAmLower ? higherWantsWar : lowerWantsWar;

        if (lowerWantsWar && higherWantsWar) {
            // Both want war -> Cinematic Duel
            this.debugLog("Pack War Started between " + this.wolf.getName().getString() + " and " + this.rival.getName().getString());
            this.startWar(seededRandom);
        } else if (lowerWantsWar || higherWantsWar) {
            // One wants war, the other doesn't -> Handshake logic (Yield or Retreat)
            int yieldChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERRITORIAL_YIELD_ON_ONE_SIDED_CHANCE);
            if (seededRandom.nextInt(100) < yieldChance) {
                // Yield & Merge: The one who wants war wins immediately
                if (iWantWar) {
                    this.debugLog("Pack Merge (Handshake): " + this.rival.getName().getString() + " surrendered to " + this.wolf.getName().getString());
                    this.winConflict();
                } else {
                    // I yield to the aggressor
                    this.behaviorTicks = 2000; 
                }
            } else {
                // Negotiated Retreat: The aggressive leader couldn't force a surrender
                this.debugLog("Negotiated Retreat: " + this.wolf.getName().getString() + " and " + this.rival.getName().getString());
                this.behavior = Behavior.RETREAT;
            }
        } else {
            // Neither wants war -> Peaceful Retreat
            this.debugLog("Peaceful Retreat: " + this.wolf.getName().getString() + " and " + this.rival.getName().getString());
            this.behavior = Behavior.RETREAT;
        }
    }

    private void debugLog(String message) {
        if (BetterDogsGameRules.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_DEBUGGING)) {
            net.vanillaoutsider.betterdogs.BetterDogs.LOGGER.info("[Territory Debug] " + message);
        }
    }

    private boolean wantsWar(Wolf wolf, WolfExtensions ext, java.util.Random random) {
        int chance;
        switch (ext.betterdogs$getPersonality()) {
            case AGGRESSIVE -> chance = BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_TERRITORIAL_WAR_CHANCE_AGGRO);
            case NORMAL -> chance = BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_TERRITORIAL_WAR_CHANCE_NORMAL);
            case PACIFIST -> chance = BetterDogsGameRules.getInt(wolf.level(), BetterDogsGameRules.BD_TERRITORIAL_WAR_CHANCE_PACI);
            default -> chance = 0;
        }
        int roll = random.nextInt(100);
        boolean result = roll < chance;
        this.debugLog(wolf.getName().getString() + " (" + ext.betterdogs$getPersonality() + ") rolled " + roll + " / target " + chance + " -> Wants War: " + result);
        return result;
    }

    private void startWar(java.util.Random seededRandom) {
        WolfExtensions ext = (WolfExtensions) this.wolf;
        this.behavior = Behavior.FIGHT;
        // Deterministic fatality decision
        this.isFatal = seededRandom.nextInt(100) < BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERRITORIAL_FATAL_CHANCE);
        ext.betterdogs$setSocialState(this.rival, WolfExtensions.SocialAction.TERRITORIAL_WAR, 1200);
    }

    private void winConflict() {
        this.debugLog("Pack Conflict Resolved: " + this.wolf.getName().getString() + " (" + this.wolf.getId() + ") wins against " + this.rival.getName().getString() + " (" + this.rival.getId() + "). Packs merged.");
        // Loser joins winner's pack
        if (this.rival instanceof GroupMember gm) {
            // Merge packs: All of rival's followers should now follow me
            if (this.rival.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                 // Find all wolves following the rival and point them to me
                 for (Wolf other : serverLevel.getEntitiesOfClass(Wolf.class, this.rival.getBoundingBox().inflate(this.searchRadius))) {
                     if (((GroupMember)other).getLeader() == this.rival) {
                         ((GroupMember)other).setLeader(this.wolf);
                     }
                 }
            }
            gm.setLeader(this.wolf);
            // Visual success feedback
            if (this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, 
                    this.wolf.getX(), this.wolf.getY() + 1.0, this.wolf.getZ(), 5, 0.5, 0.5, 0.5, 0.0);
            }
        }
        this.behaviorTicks = 2000; // Trigger completion
    }
}
