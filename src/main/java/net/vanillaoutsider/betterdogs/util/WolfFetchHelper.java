// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * Dedicated single-purpose helper for stick fetching detection, pickup, and return delivery.
 */
public class WolfFetchHelper {

    public static ItemEntity findNearbyDroppedStick(Wolf wolf, double radius) {
        if (wolf == null) {
            return null;
        }
        Level level = wolf.getCommandSenderWorld();
        if (level == null) {
            return null;
        }

        List<ItemEntity> items = level.getEntitiesOfClass(
            ItemEntity.class,
            wolf.getBoundingBox().inflate(radius),
            item -> item.isAlive() && item.getItem().is(Items.STICK)
        );

        ItemEntity closest = null;
        double closestDistSq = Double.MAX_VALUE;
        for (ItemEntity item : items) {
            double distSq = wolf.distanceToSqr(item);
            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                closest = item;
            }
        }
        return closest;
    }

    public static void pickupStick(Wolf wolf, ItemEntity stickEntity) {
        if (wolf == null || stickEntity == null || !stickEntity.isAlive()) {
            return;
        }
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setHasFetchedStick(true);
        }
        stickEntity.discard();
    }

    public static void dropStickToOwner(Wolf wolf, Player owner) {
        if (wolf == null || owner == null) {
            return;
        }
        Level level = wolf.getCommandSenderWorld();
        if (level == null || level.isClientSide()) {
            return;
        }

        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setHasFetchedStick(false);
        }

        ItemEntity itemEntity = new ItemEntity(level, owner.getX(), owner.getY() + 0.2, owner.getZ(), new ItemStack(Items.STICK));
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);

        level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.WOLF_AMBIENT, SoundSource.NEUTRAL, 1.0f, 1.2f);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 6, 0.2, 0.2, 0.2, 0.05);
        }
        WolfAdvancementHelper.grantAdvancement(owner, "fetch_stick");
    }
}
