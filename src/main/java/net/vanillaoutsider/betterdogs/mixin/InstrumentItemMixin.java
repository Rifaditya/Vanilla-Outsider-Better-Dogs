// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: InstrumentItem.java (26.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Instruments;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.EntityRaycastHelper;
import net.vanillaoutsider.betterdogs.util.WolfTeleportHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(InstrumentItem.class)
public class InstrumentItemMixin {

    @Inject(method = "use", at = @At("RETURN"))
    private void betterdogs$onInstrumentUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.CONSUME || !(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        ItemStack stack = player.getItemInHand(hand);
        InstrumentComponent component = stack.get(DataComponents.INSTRUMENT);
        if (component == null) {
            return;
        }

        Holder<Instrument> holder = component.instrument();
        boolean isPonder = holder.is(Instruments.PONDER_GOAT_HORN);
        boolean isFeel = holder.is(Instruments.FEEL_GOAT_HORN);
        boolean isSing = holder.is(Instruments.SING_GOAT_HORN);
        boolean isYearn = holder.is(Instruments.YEARN_GOAT_HORN);
        boolean isSeek = holder.is(Instruments.SEEK_GOAT_HORN);

        if (!isPonder && !isFeel && !isSing && !isYearn && !isSeek) {
            return;
        }

        int range = DynamicGameRuleManager.getInt(serverLevel, BetterDogsGameRules.BD_HORN_COMMAND_RANGE);
        double rangeD = (double) range;

        AABB area = player.getBoundingBox().inflate(rangeD, rangeD, rangeD);
        List<Wolf> nearbyWolves = serverLevel.getEntitiesOfClass(Wolf.class, area);

        BlockPos callPos = player.blockPosition();

        if (isYearn) {
            // Stage 4: Yearn Goat Horn (Resume Follow Command / Stand Up)
            for (Wolf wolf : nearbyWolves) {
                if (WolfTeleportHelper.isEligibleSittingWolf(wolf, serverPlayer)) {
                    if (wolf instanceof WolfExtensions ext) {
                        wolf.setOrderedToSit(false);
                        ext.betterdogs$setSittingManually(false);
                        ext.betterdogs$setSoundLocationTarget(null); // Preemption: clear assemble target
                        ext.betterdogs$setPassiveOverrideTicks(0); // Preemption: clear pacifist override

                        serverLevel.sendParticles(
                                ParticleTypes.NOTE,
                                wolf.getX(),
                                wolf.getY() + 0.8,
                                wolf.getZ(),
                                5,
                                0.2,
                                0.2,
                                0.2,
                                0.0
                        );
                        serverLevel.sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                wolf.getX(),
                                wolf.getY() + 0.8,
                                wolf.getZ(),
                                4,
                                0.2,
                                0.2,
                                0.2,
                                0.0
                        );
                    }
                }
            }
            return;
        }

        if (isSeek) {
            // Stage 5: Seek Goat Horn (Search / Track Target Command)
            LivingEntity raycastTarget = EntityRaycastHelper.findCrosshairTarget(serverPlayer, 32.0);
            LivingEntity finalTarget = raycastTarget;

            if (finalTarget == null) {
                // Fallback Area Search: find nearest hostile mob targeting owner or pack
                List<LivingEntity> hostiles = serverLevel.getEntitiesOfClass(
                        LivingEntity.class,
                        area,
                        e -> e.isAlive() && (e instanceof Enemy || e instanceof Monster)
                );
                double minSqDist = Double.MAX_VALUE;
                for (LivingEntity hostile : hostiles) {
                    double dist = serverPlayer.distanceToSqr(hostile);
                    if (dist < minSqDist) {
                        minSqDist = dist;
                        finalTarget = hostile;
                    }
                }
            }

            for (Wolf wolf : nearbyWolves) {
                if (WolfTeleportHelper.isEligibleFollowingWolf(wolf, serverPlayer)) {
                    if (wolf instanceof WolfExtensions ext) {
                        ext.betterdogs$setSoundLocationTarget(null); // Preemption: clear assemble target
                        ext.betterdogs$setPassiveOverrideTicks(0); // Preemption: clear pacifist override

                        if (finalTarget != null) {
                            wolf.setTarget(finalTarget);
                            wolf.setLastHurtByMob(finalTarget);
                            wolf.getNavigation().moveTo(finalTarget, 1.3);

                            // Angry villager particle above wolf and target
                            serverLevel.sendParticles(
                                    ParticleTypes.ANGRY_VILLAGER,
                                    wolf.getX(),
                                    wolf.getY() + 0.8,
                                    wolf.getZ(),
                                    4,
                                    0.2,
                                    0.2,
                                    0.2,
                                    0.0
                            );
                        } else {
                            // No target found in area: emit smoke particles
                            serverLevel.sendParticles(
                                    ParticleTypes.SMOKE,
                                    wolf.getX(),
                                    wolf.getY() + 0.8,
                                    wolf.getZ(),
                                    5,
                                    0.2,
                                    0.2,
                                    0.2,
                                    0.0
                            );
                        }
                    }
                }
            }

            if (finalTarget != null) {
                serverLevel.sendParticles(
                        ParticleTypes.ANGRY_VILLAGER,
                        finalTarget.getX(),
                        finalTarget.getY() + finalTarget.getBbHeight() + 0.3,
                        finalTarget.getZ(),
                        8,
                        0.3,
                        0.3,
                        0.3,
                        0.0
                );
            }
            return;
        }

        for (Wolf wolf : nearbyWolves) {
            // Strictly check if wolf is in active follow state (ignoring sitting, leashed, or guarding dogs)
            if (WolfTeleportHelper.isEligibleFollowingWolf(wolf, serverPlayer)) {
                if (wolf instanceof WolfExtensions ext) {
                    if (isPonder) {
                        // Stage 1: Ponder Goat Horn (Assemble Call)
                        ext.betterdogs$setSoundLocationTarget(callPos);
                        ext.betterdogs$setPassiveOverrideTicks(0); // Preemption: clear pacifist override
                        serverLevel.sendParticles(
                                ParticleTypes.NOTE,
                                wolf.getX(),
                                wolf.getY() + 0.8,
                                wolf.getZ(),
                                5,
                                0.2,
                                0.2,
                                0.2,
                                0.0
                        );
                    } else if (isFeel) {
                        // Stage 2: Feel Goat Horn (Tactical Pacifist - 30s)
                        int duration = DynamicGameRuleManager.getInt(serverLevel, BetterDogsGameRules.BD_HORN_OVERRIDE_DURATION);
                        ext.betterdogs$setPassiveOverrideTicks(duration);
                        ext.betterdogs$setSoundLocationTarget(null); // Preemption: clear assemble target

                        // Hard combat clear
                        wolf.setTarget(null);
                        wolf.setLastHurtByMob(null);
                        ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
                        wolf.getNavigation().stop();

                        // Single burst green happy villager particles
                        serverLevel.sendParticles(
                                ParticleTypes.HAPPY_VILLAGER,
                                wolf.getX(),
                                wolf.getY() + 0.8,
                                wolf.getZ(),
                                6,
                                0.3,
                                0.3,
                                0.3,
                                0.0
                        );
                    } else if (isSing) {
                        // Stage 3: Sing Goat Horn (Hold Command - Order Dogs to Sit)
                        wolf.setOrderedToSit(true);
                        ext.betterdogs$setSittingManually(true);
                        ext.betterdogs$setSoundLocationTarget(null); // Preemption: clear assemble target
                        ext.betterdogs$setPassiveOverrideTicks(0); // Preemption: clear pacifist override

                        // Hard combat clear
                        wolf.setTarget(null);
                        wolf.setLastHurtByMob(null);
                        ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
                        wolf.getNavigation().stop();

                        // Single burst note particles
                        serverLevel.sendParticles(
                                ParticleTypes.NOTE,
                                wolf.getX(),
                                wolf.getY() + 0.8,
                                wolf.getZ(),
                                5,
                                0.2,
                                0.2,
                                0.2,
                                0.0
                        );
                    }
                }
            }
        }
    }
}
