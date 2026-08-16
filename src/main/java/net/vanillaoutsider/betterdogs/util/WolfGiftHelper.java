// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;

/**
 * Dedicated single-purpose helper for morning gift eligibility, loot rolls, and delivery.
 */
public class WolfGiftHelper {

    public static boolean canDeliverGift(Wolf wolf, Player owner) {
        if (wolf == null || owner == null) {
            return false;
        }
        if (!wolf.isTame() || !wolf.isOwnedBy(owner)) {
            return false;
        }
        if (wolf.getHealth() < wolf.getMaxHealth() - 0.01f) {
            return false;
        }
        Level level = wolf.level();
        if (level == null) {
            return false;
        }

        long currentDay = level.getDayTime() / 24000L;
        if (wolf instanceof WolfExtensions ext && ext.betterdogs$getLastGiftDay() >= currentDay) {
            return false;
        }

        if (wolf.distanceToSqr(owner) > 256.0) {
            return false;
        }

        List<Monster> monsters = level.getEntitiesOfClass(Monster.class, wolf.getBoundingBox().inflate(16.0));
        return monsters.isEmpty();
    }

    public static ItemStack rollMorningGift(Wolf wolf, RandomSource random) {
        WolfPersonality personality = wolf instanceof WolfExtensions ext ? ext.betterdogs$getPersonality() : WolfPersonality.NORMAL;
        if (personality == null) {
            personality = WolfPersonality.NORMAL;
        }

        // 5% rare treasure roll
        if (random.nextFloat() < 0.05f) {
            ItemStack[] rareItems = new ItemStack[] {
                new ItemStack(Items.GOLD_NUGGET, 1 + random.nextInt(3)),
                new ItemStack(Items.EMERALD),
                new ItemStack(Items.NAME_TAG),
                new ItemStack(Items.BONE_MEAL, 1 + random.nextInt(3))
            };
            return rareItems[random.nextInt(rareItems.length)];
        }

        return switch (personality) {
            case AGGRESSIVE -> {
                ItemStack[] aggro = new ItemStack[] {
                    new ItemStack(Items.BONE, 1 + random.nextInt(2)),
                    new ItemStack(Items.LEATHER),
                    new ItemStack(Items.ROTTEN_FLESH, 1 + random.nextInt(2)),
                    new ItemStack(Items.RABBIT_HIDE),
                    new ItemStack(Items.SPIDER_EYE),
                    new ItemStack(Items.ARROW, 1 + random.nextInt(3))
                };
                yield aggro[random.nextInt(aggro.length)];
            }
            case PACIFIST -> {
                ItemStack[] paci = new ItemStack[] {
                    new ItemStack(Items.SWEET_BERRIES, 1 + random.nextInt(3)),
                    new ItemStack(Items.APPLE),
                    new ItemStack(Items.DANDELION),
                    new ItemStack(Items.POPPY),
                    new ItemStack(Items.HONEYCOMB),
                    new ItemStack(Items.WHEAT_SEEDS, 1 + random.nextInt(3))
                };
                yield paci[random.nextInt(paci.length)];
            }
            default -> {
                ItemStack[] norm = new ItemStack[] {
                    new ItemStack(Items.STICK, 1 + random.nextInt(2)),
                    new ItemStack(Items.FEATHER, 1 + random.nextInt(2)),
                    new ItemStack(Items.FLINT),
                    new ItemStack(Items.STRING),
                    new ItemStack(Items.CLAY_BALL, 1 + random.nextInt(2))
                };
                yield norm[random.nextInt(norm.length)];
            }
        };
    }

    public static void deliverGift(Wolf wolf, Player owner) {
        if (wolf == null || owner == null) {
            return;
        }
        Level level = wolf.level();
        if (level == null || level.isClientSide()) {
            return;
        }

        long currentDay = level.getDayTime() / 24000L;
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setLastGiftDay(currentDay);
        }

        ItemStack gift = rollMorningGift(wolf, wolf.getRandom());
        ItemEntity itemEntity = new ItemEntity(level, owner.getX(), owner.getY() + 0.2, owner.getZ(), gift);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);

        WolfFeedbackHelper.sendFeedback(owner, level, Component.literal("§6Your dog brought you a morning gift!"));
        level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.WOLF_SHAKE, SoundSource.NEUTRAL, 1.0f, 1.2f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
        }
    }
}
