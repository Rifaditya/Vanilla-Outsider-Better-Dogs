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

    /**
     * Returns true if the dimension has an active celestial daylight cycle.
     */
    public static boolean hasDaylightCycle(Level level) {
        if (level == null) {
            return false;
        }
        try {
            return !level.dimensionType().hasFixedTime() && level.dimensionType().hasSkyLight();
        } catch (Throwable ignored) {
            return true;
        }
    }

    /**
     * Returns the celestial day time ticks for the level.
     */
    public static long getCelestialDayTime(Level level) {
        if (level == null) {
            return 0L;
        }
        return level.getDayTime();
    }

    /**
     * Calculates the calendar day count from celestial day ticks using floor division.
     */
    public static long calculateDayCount(long celestialDayTime) {
        return Math.floorDiv(celestialDayTime, 24000L);
    }

    /**
     * Helper getting current in-game day count.
     */
    public static long getDayCount(Level level) {
        if (level == null) {
            return 0L;
        }
        return calculateDayCount(getCelestialDayTime(level));
    }

    /**
     * Evaluates whether the given celestial tick timestamp falls in morning (0-2000) or owner is sleeping/waking.
     */
    public static boolean isMorningOrWakingTicks(long celestialDayTime, boolean isSleepingOrWaking) {
        long timeOfDay = Math.floorMod(celestialDayTime, 24000L);
        boolean isMorning = timeOfDay >= 0L && timeOfDay <= 2000L;
        return isMorning || isSleepingOrWaking;
    }

    /**
     * Checks if the current time is morning (0-2000 ticks) or the owner is waking up from sleep.
     */
    public static boolean isMorningOrWaking(Level level, Player owner) {
        if (level == null || !hasDaylightCycle(level)) {
            return false;
        }
        boolean isSleepingOrWaking = owner != null && (owner.isSleeping() || owner.getSleepTimer() > 0);
        return isMorningOrWakingTicks(getCelestialDayTime(level), isSleepingOrWaking);
    }

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

        if (!isMorningOrWaking(level, owner)) {
            return false;
        }

        int threshold = net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.getInt(
            level,
            net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_GIFT_FEED_THRESHOLD,
            10
        );

        if (wolf instanceof WolfExtensions ext) {
            if (ext.betterdogs$getFeedCount() < threshold) {
                return false;
            }
            long currentDay = getDayCount(level);
            if (ext.betterdogs$getLastGiftDay() >= currentDay) {
                return false;
            }
        } else {
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

        int threshold = net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.getInt(
            level,
            net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_GIFT_FEED_THRESHOLD,
            10
        );

        long currentDay = getDayCount(level);
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setLastGiftDay(currentDay);
            ext.betterdogs$setFeedCount(Math.max(0, ext.betterdogs$getFeedCount() - threshold));
        }

        ItemStack gift = rollMorningGift(wolf, wolf.getRandom());
        ItemEntity itemEntity = new ItemEntity(level, owner.getX(), owner.getY() + 0.2, owner.getZ(), gift);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);

        WolfFeedbackHelper.sendFeedback(owner, level, Component.literal("§6Your dog brought you a gift!"));
        level.playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.WOLF_SHAKE, SoundSource.NEUTRAL, 1.0f, 1.2f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 2, 0.15, 0.15, 0.15, 0.02);
        }
        WolfAdvancementHelper.grantAdvancement(owner, "morning_gift");
    }
}
