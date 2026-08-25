// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.listener;

import java.util.UUID;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.DogCommandManager;
import net.vanillaoutsider.betterdogs.util.DogSeatHelper;

/**
 * Dedicated single-purpose listener for player interactions with entities,
 * handling vehicle boarding, commands, and dismounting for dogs.
 */
public class DogEntityCommandListener {

    public static void register() {
        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {
            if (hand != InteractionHand.MAIN_HAND || level.isClientSide()) {
                return InteractionResult.PASS;
            }
            if (!player.isSecondaryUseActive()) {
                return InteractionResult.PASS;
            }
            ItemStack held = player.getItemInHand(hand);
            if (!DogSeatHelper.isCommandItem(held) && !held.isEmpty()) {
                return InteractionResult.PASS;
            }

            UUID dogUuid = DogCommandManager.getSelectedDog(player.getUUID());
            if (dogUuid == null) {
                // Direct click on vehicle with passenger dogs owned by the player
                if (entity instanceof AbstractMinecart || entity instanceof Boat || DogSeatHelper.isSitTarget(entity) || !entity.getPassengers().isEmpty()) {
                    if (level instanceof ServerLevel serverLevel) {
                        for (Entity passenger : entity.getPassengers()) {
                            if (passenger instanceof Wolf wolf && wolf.isTame() && wolf.isOwnedBy(player) && wolf.isAlive()) {
                                wolf.stopRiding();
                                DogCommandManager.clearVehicleTarget(wolf.getUUID());
                                DogCommandManager.clearSelection(player.getUUID());

                                // Safely offset position outside vehicle/minecart collision envelope
                                Vec3 offset = DogSeatHelper.calculateDismountOffset(player.getLookAngle(), DogSeatHelper.DEFAULT_DISMOUNT_DISTANCE);
                                wolf.setPos(wolf.getX() + offset.x, wolf.getY(), wolf.getZ() + offset.z);

                                if (entity.entityTags().contains("betterdogs:seat")) {
                                    entity.discard();
                                }

                                player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_dismounted", wolf.getName()));
                                SoundEvent ambientSound = ((WolfAccessor) wolf).betterdogs$invokeGetAmbientSound();
                                serverLevel.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(),
                                        ambientSound, wolf.getSoundSource(), 1.0f, 1.0f);
                                serverLevel.sendParticles(ParticleTypes.CLOUD, wolf.getX(), wolf.getY() + 0.3, wolf.getZ(), 8, 0.2, 0.2, 0.2, 0.05);
                                return InteractionResult.SUCCESS;
                            }
                        }
                    }
                }
                return InteractionResult.PASS;
            }

            if (level instanceof ServerLevel serverLevel) {
                Entity selectedEntity = serverLevel.getEntity(dogUuid);
                if (selectedEntity instanceof Wolf wolf && wolf.isTame() && wolf.isOwnedBy(player) && wolf.isAlive()) {
                    // Range check (12 blocks = 144.0D squared)
                    if (wolf.distanceToSqr(entity) > DogSeatHelper.MAX_COMMAND_DISTANCE_SQ) {
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_too_far", wolf.getName()));
                        return InteractionResult.SUCCESS;
                    }

                    boolean unrestricted = DynamicGameRuleManager.getBoolean(level, BetterDogsGameRules.BD_ALLOW_UNRESTRICTED_RIDING);
                    if (unrestricted || DogSeatHelper.isSitTarget(entity)) {
                        if (DogSeatHelper.hasPassengerSpace(entity)) {
                            DogCommandManager.setVehicleTarget(wolf.getUUID(), entity);
                            DogCommandManager.clearSelection(player.getUUID());
                            wolf.setOrderedToSit(false);
                            player.sendOverlayMessage(Component.translatable("text.betterdogs.dog_commanded_to_board", wolf.getName(), entity.getName()));
                            SoundEvent whineSound = ((WolfAccessor) wolf).betterdogs$invokeGetSoundSet().whineSound().value();
                            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    whineSound, wolf.getSoundSource(), 1.0f, 1.2f);
                            return InteractionResult.SUCCESS;
                        } else {
                            player.sendOverlayMessage(Component.translatable("text.betterdogs.seat_occupied"));
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            return InteractionResult.PASS;
        });
    }
}
