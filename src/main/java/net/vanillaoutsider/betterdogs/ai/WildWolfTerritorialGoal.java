// Verified against: WildWolfTerritorialGoal.java (26.1.2+)
package net.vanillaoutsider.betterdogs.ai;

import java.util.EnumSet;
import net.dasik.social.api.group.GroupMember;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

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
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$getSocialAction() != WolfExtensions.SocialAction.NONE) {
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
        if (this.rival instanceof WolfExtensions rivalExt) {
            rivalExt.betterdogs$setSocialState(this.wolf, WolfExtensions.SocialAction.TERRITORIAL_DISPUTE, 1200);
        }
    }

    @Override
    public void stop() {
        if (this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
        if (this.rival instanceof WolfExtensions rivalExt && rivalExt.betterdogs$getSocialTarget() == this.wolf) {
            rivalExt.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
        this.rival = null;
        int maxCooldown = BetterDogsGameRules.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_DEBUGGING) ? 20 : 400;
        this.cooldown = maxCooldown;
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
                if (!this.isFatal) {
                    if (this.rival.getHealth() < this.rival.getMaxHealth() * 0.4) {
                        this.winConflict();
                    } else if (this.wolf.getHealth() < this.wolf.getMaxHealth() * 0.4) {
                        this.loseConflict();
                    }
                } else {
                    if (this.rival.isRemoved() || !this.rival.isAlive()) {
                        this.winConflict();
                    } else if (this.wolf.isRemoved() || !this.wolf.isAlive()) {
                        this.loseConflict();
                    }
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
        Wolf rivalWolf = (Wolf) this.rival;
        WolfExtensions rivalExt = (WolfExtensions) rivalWolf;

        // Use deterministic seed for both wolves to ensure synchronized decisions
        long seed = (long) Math.min(this.wolf.getId(), this.rival.getId()) << 32 | (long) Math.max(this.wolf.getId(), this.rival.getId());
        java.util.Random seededRandom = new java.util.Random(seed);

        WolfPersonality myPers = ext.betterdogs$getPersonality();
        WolfPersonality rivalPers = rivalExt.betterdogs$getPersonality();

        int warChance = 0;
        int mergeChance = 0;

        // Determine which GameRules to use based on the personality pair
        if (myPers == WolfPersonality.AGGRESSIVE && rivalPers == WolfPersonality.AGGRESSIVE) {
            warChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_AA_WAR);
            mergeChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_AA_MERGE);
        } else if ((myPers == WolfPersonality.AGGRESSIVE && rivalPers == WolfPersonality.NORMAL) || 
                   (myPers == WolfPersonality.NORMAL && rivalPers == WolfPersonality.AGGRESSIVE)) {
            warChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_AN_WAR);
            mergeChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_AN_MERGE);
        } else if ((myPers == WolfPersonality.AGGRESSIVE && rivalPers == WolfPersonality.PACIFIST) || 
                   (myPers == WolfPersonality.PACIFIST && rivalPers == WolfPersonality.AGGRESSIVE)) {
            warChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_AP_WAR);
            mergeChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_AP_MERGE);
        } else if (myPers == WolfPersonality.NORMAL && rivalPers == WolfPersonality.NORMAL) {
            warChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_NN_WAR);
            mergeChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_NN_MERGE);
        } else if ((myPers == WolfPersonality.NORMAL && rivalPers == WolfPersonality.PACIFIST) || 
                   (myPers == WolfPersonality.PACIFIST && rivalPers == WolfPersonality.NORMAL)) {
            warChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_NP_WAR);
            mergeChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_NP_MERGE);
        } else if (myPers == WolfPersonality.PACIFIST && rivalPers == WolfPersonality.PACIFIST) {
            warChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_PP_WAR);
            mergeChance = BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERR_PP_MERGE);
        }

        int roll = seededRandom.nextInt(100);
        this.debugLog("Matrix Roll: " + roll + " (War: <" + warChance + ", Merge: <" + (warChance + mergeChance) + ") | " + 
                      this.wolf.getName().getString() + " (" + myPers + ") vs " + rivalWolf.getName().getString() + " (" + rivalPers + ")");

        if (roll < warChance) {
            // WAR
            this.startWar(seededRandom);
        } else if (roll < warChance + mergeChance) {
            // MERGE (Dominance based)
            // Hierarchy: Aggressive > Normal > Pacifist
            boolean iAmDominant = isMoreDominant(myPers, rivalPers);
            // If same personality, higher ID is dominant for stability
            if (myPers == rivalPers) {
                iAmDominant = this.wolf.getId() > rivalWolf.getId();
            }

            if (iAmDominant) {
                this.debugLog("Merge Victory: " + this.wolf.getName().getString() + " absorbs " + rivalWolf.getName().getString());
                this.winConflict();
            } else {
                this.debugLog("Merge Defeat: " + this.wolf.getName().getString() + " absorbed by " + rivalWolf.getName().getString());
                this.loseConflict();
            }
        } else {
            // RETREAT
            this.debugLog("Matrix Result: Mutual Retreat");
            this.behavior = Behavior.RETREAT;
        }
    }

    private boolean isMoreDominant(WolfPersonality a, WolfPersonality b) {
        if (a == WolfPersonality.AGGRESSIVE && b != WolfPersonality.AGGRESSIVE) return true;
        if (a == WolfPersonality.NORMAL && b == WolfPersonality.PACIFIST) return true;
        return false;
    }

    private void debugLog(String message) {
        if (BetterDogsGameRules.getBoolean(this.wolf.level(), BetterDogsGameRules.BD_DEBUGGING)) {
            net.vanillaoutsider.betterdogs.BetterDogs.LOGGER.info("[Territory Debug] " + message);
        }
    }

    private void startWar(java.util.Random seededRandom) {
        WolfExtensions ext = (WolfExtensions) this.wolf;
        this.behavior = Behavior.FIGHT;
        // Deterministic fatality decision
        this.isFatal = seededRandom.nextInt(100) < BetterDogsGameRules.getInt(this.wolf.level(), BetterDogsGameRules.BD_TERRITORIAL_FATAL_CHANCE);
        ext.betterdogs$setSocialState(this.rival, WolfExtensions.SocialAction.TERRITORIAL_WAR, 1200);
        if (this.rival instanceof WolfExtensions rivalExt) {
            rivalExt.betterdogs$setSocialState(this.wolf, WolfExtensions.SocialAction.TERRITORIAL_WAR, 1200);
        }
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

    private void loseConflict() {
        this.debugLog("Pack Conflict Resolved: " + this.wolf.getName().getString() + " (" + this.wolf.getId() + ") loses against " + this.rival.getName().getString() + " (" + this.rival.getId() + "). Packs merged.");
        // I join rival's pack
        if (this.rival instanceof Wolf rivalWolf) {
            // Merge packs: All of my followers should now follow rival
            if (this.wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                 for (Wolf other : serverLevel.getEntitiesOfClass(Wolf.class, this.wolf.getBoundingBox().inflate(this.searchRadius))) {
                     if (((GroupMember)other).getLeader() == this.wolf) {
                         ((GroupMember)other).setLeader(rivalWolf);
                     }
                 }
            }
            if (this.wolf instanceof GroupMember gm) {
                gm.setLeader(rivalWolf);
            }
            // Visual success feedback for winner
            if (rivalWolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, 
                    rivalWolf.getX(), rivalWolf.getY() + 1.0, rivalWolf.getZ(), 5, 0.5, 0.5, 0.5, 0.0);
            }
        }
        this.behaviorTicks = 2000; // Trigger completion
    }
}
