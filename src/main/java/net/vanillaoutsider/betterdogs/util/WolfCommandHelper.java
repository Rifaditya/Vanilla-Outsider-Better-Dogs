package net.vanillaoutsider.betterdogs.util;
// Verified against: Wolf.java (26.1.2 Release)
import org.jspecify.annotations.Nullable;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.scheduler.events.HowlDogEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;
import java.util.List;
import net.minecraft.world.phys.AABB;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.EntityType;
import net.dasik.social.api.group.GroupMember;
import net.minecraft.world.entity.EntitySpawnReason;

import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Command execution logic for Better Dogs debugging.
 * Strictly adheres to Modularity Mandate and Zenith Execution Guardrails.
 * Target validation and execution environment checks enforced.
 */
public class WolfCommandHelper {

    public static int setPersonality(CommandSourceStack source, Collection<? extends net.minecraft.world.entity.Entity> targets, String personalityStr) {
        int count = 0;
        WolfPersonality selectedPersonality;
        
        switch (personalityStr.toLowerCase()) {
            case "aggressive" -> selectedPersonality = WolfPersonality.AGGRESSIVE;
            case "pacifist" -> selectedPersonality = WolfPersonality.PACIFIST;
            case "normal" -> selectedPersonality = WolfPersonality.NORMAL;
            default -> {
                source.sendFailure(Component.literal("Invalid personality. Choose: aggressive, pacifist, normal."));
                return 0;
            }
        }

        for (net.minecraft.world.entity.Entity entity : targets) {
            if (entity instanceof Wolf wolf) {
                // Modularity mandate: Use extensions
                ((WolfExtensions) wolf).betterdogs$setPersonality(selectedPersonality);
                WolfStatManager.applyPersonalityStats(wolf, selectedPersonality);
                count++;
            }
        }

        if (count > 0) {
            final int finalCount = count;
            source.sendSuccess(() -> Component.literal("Successfully updated personality to " + personalityStr + " for " + finalCount + " wolves."), true);
        } else {
            source.sendFailure(Component.literal("No valid wolves selected."));
        }
        return count;
    }

    public static int executeAction(CommandSourceStack source, Collection<? extends Entity> targets, String actionStr, @Nullable Entity secondaryTarget) {
        int count = 0;
        BetterDogsConfig config = BetterDogsConfig.get();

        for (net.minecraft.world.entity.Entity entity : targets) {
            if (entity instanceof Wolf wolf && !wolf.level().isClientSide()) {
                WolfExtensions ext = (WolfExtensions) wolf;
                boolean actionApplied = true;
                LivingEntity socialTarget = (secondaryTarget instanceof LivingEntity le) ? le : null;

                switch (actionStr.toLowerCase()) {
                    case "howl" -> {
                        EntitySocialScheduler scheduler = ext.betterdogs$getOrInitializeScheduler();
                        if (scheduler != null) scheduler.schedule(new HowlDogEvent());
                    }
                    case "zoomies" -> {
                        EntitySocialScheduler scheduler = ext.betterdogs$getOrInitializeScheduler();
                        if (scheduler != null) scheduler.schedule(new ZoomiesDogEvent());
                    }
                    case "mischief" -> {
                        // Triggers a random attack target search for baby wolves
                        if (wolf.isBaby()) {
                            AABB searchBox = wolf.getBoundingBox().inflate(10.0);
                            List<LivingEntity> nearby = wolf.level().getEntitiesOfClass(
                                    LivingEntity.class,
                                    searchBox,
                                    e -> e != wolf && e.isAlive() && wolf.hasLineOfSight(e)
                            );
                            if (!nearby.isEmpty()) {
                                LivingEntity target = nearby.get(wolf.getRandom().nextInt(nearby.size()));
                                wolf.setTarget(target);
                                source.sendSuccess(() -> Component.literal("Triggered mischief: " + wolf.getName().getString() + " is attacking " + target.getName().getString()), true);
                            } else {
                                source.sendFailure(Component.literal("No mischief targets found for " + wolf.getName().getString()));
                                actionApplied = false;
                            }
                        } else {
                            source.sendFailure(Component.literal("Only baby wolves can perform mischief."));
                            actionApplied = false;
                        }
                    }
                    case "disciplined" -> ext.betterdogs$setBeingDisciplined(true);
                    case "play_fight" -> {
                        if (socialTarget == null) {
                            if (wolf.level() instanceof ServerLevel serverLevel) {
                                socialTarget = serverLevel.getNearestEntity(Wolf.class, net.minecraft.world.entity.ai.targeting.TargetingConditions.forNonCombat().range(8.0), wolf, wolf.getX(), wolf.getY(), wolf.getZ(), wolf.getBoundingBox().inflate(8.0));
                            }
                        }
                        if (socialTarget != null) {
                            ext.betterdogs$setSocialState(socialTarget, WolfExtensions.SocialAction.PLAY_FIGHT, 200);
                        } else {
                            source.sendFailure(Component.literal("No suitable play-fight target found for " + wolf.getName().getString()));
                            actionApplied = false;
                        }
                    }
                    case "retaliation" -> {
                        if (socialTarget == null) socialTarget = wolf.getOwner();
                        if (socialTarget != null) {
                            ext.betterdogs$setSocialState(socialTarget, WolfExtensions.SocialAction.RETALIATION, 100);
                        } else {
                            source.sendFailure(Component.literal("No retaliation target found (requires owner or secondary target)."));
                            actionApplied = false;
                        }
                    }
                    case "discipline" -> {
                        if (socialTarget == null) {
                            if (wolf.level() instanceof ServerLevel serverLevel) {
                                socialTarget = serverLevel.getNearestEntity(Wolf.class, net.minecraft.world.entity.ai.targeting.TargetingConditions.forNonCombat().range(8.0), wolf, wolf.getX(), wolf.getY(), wolf.getZ(), wolf.getBoundingBox().inflate(8.0));
                            }
                        }
                        if (socialTarget != null && socialTarget instanceof Wolf targetWolf && targetWolf.isBaby()) {
                            ext.betterdogs$setSocialState(socialTarget, WolfExtensions.SocialAction.DISCIPLINE, config.getCorrectionDuration());
                        } else {
                            source.sendFailure(Component.literal("Discipline requires a baby wolf target."));
                            actionApplied = false;
                        }
                    }
                    case "territorial_dispute" -> {
                        if (socialTarget == null) {
                            if (wolf.level() instanceof ServerLevel serverLevel) {
                                socialTarget = serverLevel.getNearestEntity(Wolf.class, net.minecraft.world.entity.ai.targeting.TargetingConditions.forNonCombat().range(16.0), wolf, wolf.getX(), wolf.getY(), wolf.getZ(), wolf.getBoundingBox().inflate(16.0));
                            }
                        }
                        if (socialTarget != null) {
                            ext.betterdogs$setSocialState(socialTarget, WolfExtensions.SocialAction.TERRITORIAL_DISPUTE, 200);
                        } else {
                            actionApplied = false;
                        }
                    }
                    case "territorial_war" -> {
                        if (socialTarget == null) {
                            if (wolf.level() instanceof ServerLevel serverLevel) {
                                socialTarget = serverLevel.getNearestEntity(Wolf.class, net.minecraft.world.entity.ai.targeting.TargetingConditions.forCombat().range(16.0), wolf, wolf.getX(), wolf.getY(), wolf.getZ(), wolf.getBoundingBox().inflate(16.0));
                            }
                        }
                        if (socialTarget != null) {
                            ext.betterdogs$setSocialState(socialTarget, WolfExtensions.SocialAction.TERRITORIAL_WAR, 600);
                        } else {
                            actionApplied = false;
                        }
                    }
                    default -> {
                        source.sendFailure(Component.literal("Invalid action. Supported: howl, zoomies, mischief, disciplined, play_fight, retaliation, discipline, territorial_dispute, territorial_war"));
                        return 0;
                    }
                }

                if (actionApplied) {
                    count++;
                }
            }
        }

        if (count > 0) {
            final int finalCount = count;
            source.sendSuccess(() -> Component.literal("Successfully triggered action '" + actionStr + "' on " + finalCount + " wolves."), true);
        }
        return count;
    }

    public static int spawnTerritoryScenario(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        Vec3 pos = source.getPosition();

        // Pack A (East)
        spawnPack(level, pos.add(8, 0, 0), "Aggressive Pack");
        // Pack B (West)
        spawnPack(level, pos.add(-8, 0, 0), "Normal Pack");

        source.sendSuccess(() -> Component.literal("Successfully spawned 2 rival packs (10 wolves total) for territorial testing."), true);
        return 10;
    }

    private static void spawnPack(ServerLevel level, Vec3 pos, String name) {
        // Leader
        Wolf leader = EntityType.WOLF.create(level, EntitySpawnReason.COMMAND);
        if (leader == null) return;
        
        // Find surface level
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int)pos.x, (int)pos.z);
        leader.snapTo(pos.x, surfaceY, pos.z, 0.0f, 0.0f);
        ((WolfExtensions) leader).betterdogs$setPersonality(name.contains("Aggressive") ? WolfPersonality.AGGRESSIVE : WolfPersonality.NORMAL);
        
        // Ensure they are truly wild
        leader.setTame(false, false);
        level.addFreshEntity(leader);

        // Followers
        for (int i = 0; i < 4; i++) {
            Wolf follower = EntityType.WOLF.create(level, EntitySpawnReason.COMMAND);
            if (follower == null) continue;
            double offsetX = (level.getRandom().nextDouble() - 0.5) * 4;
            double offsetZ = (level.getRandom().nextDouble() - 0.5) * 4;
            
            double targetX = pos.x + offsetX;
            double targetZ = pos.z + offsetZ;
            int followerY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int)targetX, (int)targetZ);
            
            follower.snapTo(targetX, followerY, targetZ, 0.0f, 0.0f);
            
            // Link to leader
            if (follower instanceof GroupMember member) {
                member.setLeader(leader);
            }
            
            ((WolfExtensions) follower).betterdogs$setPersonality(WolfPersonality.NORMAL);
            follower.setTame(false, false);
            level.addFreshEntity(follower);
        }
    }
}
