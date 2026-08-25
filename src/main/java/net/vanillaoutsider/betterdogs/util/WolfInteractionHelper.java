// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfDebugLogger;
import net.vanillaoutsider.betterdogs.util.WolfPersonalityStatHelper;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;

import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent;

public class WolfInteractionHelper {

    public static Item getFavoriteTreat(Wolf wolf) {
        return DogTreatHelper.getFavoriteTreat(wolf);
    }

    public static InteractionResult handleMobInteract(Wolf wolf, Player player, InteractionHand hand, ItemStack itemStack) {
        if (wolf.isTame() && wolf.isOwnedBy(player) && itemStack.is(Items.GOLDEN_APPLE) && hand == InteractionHand.MAIN_HAND) {
            if (wolf instanceof WolfExtensions ext && net.vanillaoutsider.betterdogs.WolfPersistentData.isPersistedInbred(wolf)) {
                if (DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_ENABLE_INBRED_CURING)) {
                    if (!wolf.level().isClientSide()) {
                        itemStack.consume(1, player);
                        
                        float finalHp = net.vanillaoutsider.betterdogs.WolfPersistentData.getPersistedHealthBonus(wolf);
                        float finalDmg = net.vanillaoutsider.betterdogs.WolfPersistentData.getPersistedDamageMod(wolf);
                        float finalSpeed = net.vanillaoutsider.betterdogs.WolfPersistentData.getPersistedSpeedMod(wolf);
                        
                        // Inverse formulas:
                        // finalHp = avgHp * 0.6f - 6.0f;  =>  avgHp = (finalHp + 6.0f) / 0.6f;
                        // finalDmg = avgDmg * 0.6f - 0.20f; => avgDmg = (finalDmg + 0.20f) / 0.6f;
                        // finalSpeed = avgSpeed * 0.6f - 0.15f; => avgSpeed = (finalSpeed + 0.15f) / 0.6f;
                        float avgHp = (finalHp + 6.0f) / 0.6f;
                        float avgDmg = (finalDmg + 0.20f) / 0.6f;
                        float avgSpeed = (finalSpeed + 0.15f) / 0.6f;
                        
                        avgHp = Math.max(avgHp, -30.0f);
                        avgDmg = Math.max(avgDmg, -0.8f);
                        avgSpeed = Math.max(avgSpeed, -0.6f);
                        
                        net.vanillaoutsider.betterdogs.WolfPersistentData.setPersistedHealthBonus(wolf, avgHp);
                        net.vanillaoutsider.betterdogs.WolfPersistentData.setPersistedDamageMod(wolf, avgDmg);
                        net.vanillaoutsider.betterdogs.WolfPersistentData.setPersistedSpeedMod(wolf, avgSpeed);
                        
                        java.util.Optional<UUID> p1 = net.vanillaoutsider.betterdogs.WolfPersistentData.getPersistedParent1Uuid(wolf);
                        java.util.Optional<UUID> p2 = net.vanillaoutsider.betterdogs.WolfPersistentData.getPersistedParent2Uuid(wolf);
                        net.vanillaoutsider.betterdogs.WolfPersistentData.setPersistedParentsAndInbred(wolf, p1.orElse(null), p2.orElse(null), false);
                        
                        WolfPersonality personality = ext.betterdogs$getPersonality();
                        WolfPersonalityStatHelper.applyPersonalityStats(wolf, personality);
                        
                        wolf.setHealth(wolf.getMaxHealth());
                        
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, wolf.getSoundSource(), 1.0f, 1.0f);
                        
                        if (wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getRandomX(1.0), wolf.getRandomY() + 0.5, wolf.getRandomZ(1.0), 10, 0.2, 0.2, 0.2, 0.05);
                        }
                        
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.cured_inbred", wolf.getName()));
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (wolf.isTame() && wolf instanceof WolfExtensions ext) {
            // Comforting/Petting to soothe Storm Anxiety (Secondary use interaction with empty hand)
            if (wolf.isOwnedBy(player) && player.isSecondaryUseActive() && itemStack.isEmpty() && hand == InteractionHand.MAIN_HAND && wolf.level().isThundering()) {
                long currentTime = wolf.level().getGameTime();
                long soothedTime = ext.betterdogs$getSoothedTime();
                if (currentTime - soothedTime >= 12000L) { // Not recently soothed
                    if (!wolf.level().isClientSide()) {
                        ext.betterdogs$setSoothedTime(currentTime);
                        
                        // Play whimpering/happy sound mix and spawn comforting particles
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().whineSound().value(), wolf.getSoundSource(), 1.0f, 0.8f);
                        
                        if (wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.HEART, wolf.getRandomX(1.0), wolf.getRandomY() + 0.5, wolf.getRandomZ(1.0), 3, 0.2, 0.1, 0.2, 0.02);
                            serverLevel.sendParticles(ParticleTypes.NOTE, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }

			// 0. Stick / Command Item command: select or dismount
			if (wolf.isOwnedBy(player) && player.isSecondaryUseActive() && DogCommandManager.isCommandItem(itemStack) && hand == InteractionHand.MAIN_HAND) {
				if (!wolf.level().isClientSide()) {
					if (wolf.isPassenger()) {
						// Dismount and Stand
						Entity vehicle = wolf.getVehicle();
						wolf.stopRiding();
						if (vehicle != null && vehicle.entityTags().contains("betterdogs:seat")) {
							vehicle.discard();
						}
						
						DogCommandManager.clearVehicleTarget(wolf.getUUID());
						DogCommandManager.clearSelection(player.getUUID());
						
						net.minecraft.sounds.SoundEvent sound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().ambientSound().value();
						wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), sound, wolf.getSoundSource(), 1.0f, 1.0f);
						player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_dismounted", wolf.getName()));
						if (wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
							serverLevel.sendParticles(ParticleTypes.CLOUD, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 5, 0.2, 0.2, 0.2, 0.02);
						}
					} else {
						// Select dog
						DogCommandManager.selectDog(player.getUUID(), wolf.getUUID());
						net.minecraft.sounds.SoundEvent sound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().ambientSound().value();
						wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), sound, wolf.getSoundSource(), 1.0f, 1.2f);
						player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_selected", wolf.getName()));
						if (wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
							serverLevel.sendParticles(ParticleTypes.NOTE, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
						}
					}
				}
				return InteractionResult.SUCCESS;
			}

			// 1. Triggering / Toggling Pending Adoption
			if (wolf.isOwnedBy(player) && player.isSecondaryUseActive() && itemStack.is(Items.PAPER) && hand == InteractionHand.MAIN_HAND) {
                if (!wolf.level().isClientSide()) {
                    boolean currentAdoptable = ext.betterdogs$isAdoptable();
                    boolean newAdoptable = !currentAdoptable;
                    ext.betterdogs$setAdoptable(newAdoptable);

                    if (newAdoptable) {
                        wolf.setOrderedToSit(true);
                        wolf.getNavigation().stop();
                        wolf.setTarget(null);
                        wolf.stopBeingAngry();
                        if (ext.betterdogs$isGuardMode()) {
                            ext.betterdogs$setSittingManually(true);
                        }
                        itemStack.consume(1, player);
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.BOOK_PAGE_TURN, wolf.getSoundSource(), 1.0f, 1.0f);
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.adoption_pending", wolf.getName()));

                        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                            net.vanillaoutsider.betterdogs.BetterDogs.PUT_UP_FOR_ADOPTION.trigger(serverPlayer);
                        }
                    } else {
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.BOOK_PAGE_TURN, wolf.getSoundSource(), 1.0f, 1.0f);
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.adoption_cancelled", wolf.getName()));
                    }
                }
                return InteractionResult.SUCCESS;
            }

            // 2. Claiming / Adopting by another player
            if (!wolf.isOwnedBy(player) && ext.betterdogs$isAdoptable() && itemStack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
                if (!wolf.level().isClientSide()) {
                    EntityReference<LivingEntity> oldOwnerRef = wolf.getOwnerReference();
                    UUID oldOwnerUuid = oldOwnerRef != null ? oldOwnerRef.getUUID() : null;

                    // Tame/Adopt the wolf
                    wolf.tame(player);
                    wolf.setOrderedToSit(false);
                    ext.betterdogs$setAdoptable(false);
                    
                    // Reset old social and guarding states
                    ext.betterdogs$setLeaderUuid(null);
                    ext.betterdogs$setBloodFeudTarget(null);
                    ext.betterdogs$setGuardMode(false);
                    ext.betterdogs$setGuardPos(null);

                    // Ensure dog has a personality assigned before applying stats
                    if (!ext.betterdogs$hasPersonality()) {
                        ext.betterdogs$setPersonality(WolfPersonality.random(wolf.level()));
                    }

                    // Re-apply personality stats for the new owner relationship
                    WolfPersonality personality = ext.betterdogs$getPersonality();
                    if (personality == null) {
                        personality = WolfPersonality.NORMAL;
                        ext.betterdogs$setPersonality(personality);
                    }
                    WolfPersonalityStatHelper.applyPersonalityStats(wolf, personality);

                    // Play effects
                    wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().ambientSound().value(), wolf.getSoundSource(), 1.0f, 1.0f);
                    if (wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.HEART, wolf.getRandomX(1.0), wolf.getRandomY() + 0.5, wolf.getRandomZ(1.0), 5, 0.2, 0.2, 0.2, 0.02);
                    }

                    // Notify players
                    player.sendOverlayMessage(Component.translatable("text.betterdogs.adopted_new_owner", wolf.getName()));
                    if (oldOwnerUuid != null) {
                        Player oldOwner = wolf.level().getPlayerByUUID(oldOwnerUuid);
                        if (oldOwner != null) {
                            oldOwner.sendOverlayMessage(Component.translatable("text.betterdogs.adopted_old_owner", wolf.getName(), player.getName()));
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }

            // 3. Cancelling adoption by normal owner interaction (not shifting with paper)
            if (wolf.isOwnedBy(player) && ext.betterdogs$isAdoptable()) {
                if (!player.isSecondaryUseActive() || !itemStack.is(Items.PAPER)) {
                    if (!wolf.level().isClientSide()) {
                        ext.betterdogs$setAdoptable(false);
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.BOOK_PAGE_TURN, wolf.getSoundSource(), 1.0f, 1.0f);
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.adoption_cancelled", wolf.getName()));
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        // Shift + Right click empty hand: calm down interaction
        if (wolf.isTame() && wolf.isOwnedBy(player) && player.isSecondaryUseActive() && itemStack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
            if (!wolf.level().isClientSide()) {
                if (wolf.isPassenger()) {
                    Entity vehicle = wolf.getVehicle();
                    wolf.stopRiding();
                    if (vehicle != null && vehicle.entityTags().contains("betterdogs:seat")) {
                        vehicle.discard();
                    }
                    DogCommandManager.clearVehicleTarget(wolf.getUUID());

                    // Safely offset position outside vehicle/minecart collision envelope
                    net.minecraft.world.phys.Vec3 offset = player.getLookAngle().scale(-0.8).multiply(1, 0, 1);
                    wolf.setPos(wolf.getX() + offset.x, wolf.getY(), wolf.getZ() + offset.z);
                }

                wolf.setOrderedToSit(true);
                wolf.getNavigation().stop();
                wolf.setTarget(null);
                
                // Clear persistent anger (calm down)
                wolf.stopBeingAngry();
                
                if (wolf instanceof WolfExtensions ext) {
                    if (ext.betterdogs$isGuardMode()) {
                        ext.betterdogs$setSittingManually(true);
                    }
                }
                
                net.minecraft.sounds.SoundEvent sound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().whineSound().value();
                wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), sound, wolf.getSoundSource(), 1.0f, 1.2f);
                
                player.sendOverlayMessage(Component.translatable("text.betterdogs.calmed_down", wolf.getName()));
                
                if (wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 5, 0.2, 0.2, 0.2, 0.02);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (wolf.isTame() && wolf.isOwnedBy(player) && hand == InteractionHand.MAIN_HAND) {
            if (wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
                if (!wolf.isFood(itemStack) && !itemStack.is(net.minecraft.tags.ItemTags.WOLF_COLLAR_DYES) 
                    && !wolf.isEquippableInSlot(itemStack, net.minecraft.world.entity.EquipmentSlot.BODY)
                    && !(wolf.isInSittingPose() && wolf.isWearingBodyArmor() && wolf.getBodyArmorItem().isDamaged() && wolf.getBodyArmorItem().isValidRepairItem(itemStack))) {
                    
                    if (!wolf.level().isClientSide()) {
                        boolean currentSitting = ext.betterdogs$isSittingManually();
                        boolean newSitting = !currentSitting;
                        ext.betterdogs$setSittingManually(newSitting);
                        wolf.setOrderedToSit(newSitting);
                        wolf.getNavigation().stop();
                        wolf.setTarget(null);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        if (player.getItemInHand(hand).getItem() instanceof DebugStickItem) {
            if (DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_DEBUGGING)
                && player.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)) {
                if (!wolf.level().isClientSide()) {
                    if (wolf instanceof WolfExtensions ext) {
                        if (player.isSecondaryUseActive()) {
                            // Shift + Click: Cycle Scale
                            float currentScale = ext.betterdogs$getSocialScale();
                            float nextScale = currentScale + 0.1f;
                            if (nextScale > 1.5f) nextScale = 0.5f;
                            ext.betterdogs$setSocialScale(nextScale);
                            player.sendOverlayMessage(Component.literal("§b[Debug] §fScale: " + String.format("%.1f", nextScale)));
                            WolfDebugLogger.log(wolf, "DebugStick", "Scale changed to " + nextScale);
                        } else {
                            // Normal Click: Cycle Personality
                            WolfPersonality current = ext.betterdogs$getPersonality();
                            WolfPersonality next = current.next();
                            ext.betterdogs$setPersonality(next);
                            // Force re-apply stats
                            WolfPersonalityStatHelper.applyPersonalityStats(wolf, next);
                            player.sendOverlayMessage(Component.literal("§b[Debug] §fPersonality: " + next.name()));
                            WolfDebugLogger.log(wolf, "DebugStick", "Personality changed to " + next.name());
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        return null;
    }
}
