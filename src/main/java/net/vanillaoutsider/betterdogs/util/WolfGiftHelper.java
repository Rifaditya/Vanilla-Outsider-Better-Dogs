// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs.util;

import java.util.List;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;

/**
 * Single-purpose helper managing morning gift eligibility, data-driven loot rolls, and delivery feedback.
 */
public final class WolfGiftHelper {

    public static final int DEFAULT_FEED_THRESHOLD = 10;

    public static final ResourceKey<LootTable> AGGRESSIVE_GIFT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "morning_gift/aggressive"));
    public static final ResourceKey<LootTable> PACIFIST_GIFT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "morning_gift/pacifist"));
    public static final ResourceKey<LootTable> NORMAL_GIFT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "morning_gift/normal"));
    public static final ResourceKey<LootTable> RARE_TREASURE_TABLE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("vanilla-outsider-better-dogs", "morning_gift/rare_treasure"));

    private WolfGiftHelper() {
    }

    /**
     * Helper getting current in-game day count.
     */
    public static long getDayCount(Level level) {
        if (level == null) {
            return 0L;
        }
        return level.getGameTime() / 24000L;
    }

    /**
     * Checks if the current time is morning (0-2000 ticks) or the owner is waking up from sleep.
     */
    public static boolean isMorningOrWaking(Level level, Player owner) {
        if (level == null) {
            return false;
        }
        long timeOfDay = level.getGameTime() % 24000L;
        boolean isMorning = timeOfDay >= 0 && timeOfDay <= 2000L;
        boolean isSleepingOrWaking = owner != null && (owner.isSleeping() || owner.getSleepTimer() > 0);
        return isMorning || isSleepingOrWaking;
    }

    /**
     * Evaluates if a wolf is eligible to deliver a morning gift to its owner.
     */
    public static boolean canDeliverGift(Wolf wolf, Player owner) {
        if (wolf == null || owner == null || !wolf.isAlive() || !owner.isAlive()) {
            return false;
        }
        if (!wolf.isTame() || !wolf.isOwnedBy(owner)) {
            return false;
        }
        // 1. Strict 100% full health requirement
        if (wolf.getHealth() < wolf.getMaxHealth()) {
            return false;
        }

        Level level = wolf.level();
        if (level == null) {
            return false;
        }

        // 2. Dual Morning / Wake-up Trigger
        if (!isMorningOrWaking(level, owner)) {
            return false;
        }

        // 3. Feed merits threshold
        int threshold = DEFAULT_FEED_THRESHOLD;
        try {
            threshold = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_GIFT_FEED_THRESHOLD);
            if (threshold <= 0) {
                return false;
            }
        } catch (Throwable ignored) {
            // Test fallback
        }

        int currentFeeds = WolfPersistentData.getPersistedFeedCount(wolf);
        if (currentFeeds < threshold) {
            return false;
        }

        // 4. Once per calendar day limit
        long currentDay = getDayCount(level);
        long lastGiftDay = WolfPersistentData.getLastGiftDay(wolf);
        if (lastGiftDay >= currentDay) {
            return false;
        }

        if (wolf.distanceToSqr(owner) > 256.0D) {
            return false;
        }

        // 5. Peaceful area (zero hostile monsters within 16 blocks)
        List<Monster> monsters = level.getEntitiesOfClass(Monster.class, wolf.getBoundingBox().inflate(16.0D));
        return monsters.isEmpty();
    }

    /**
     * Rolls a personality-themed scavenged morning gift using data-driven loot tables with in-code fallback.
     */
    public static ItemStack rollMorningGift(Level level, Wolf wolf, WolfPersonality personality, RandomSource random) {
        if (personality == null) {
            personality = WolfPersonality.NORMAL;
        }

        if (level instanceof ServerLevel serverLevel && wolf != null) {
            try {
                boolean isRare = random.nextFloat() < 0.05f;
                ResourceKey<LootTable> tableKey = isRare ? RARE_TREASURE_TABLE : switch (personality) {
                    case AGGRESSIVE -> AGGRESSIVE_GIFT_TABLE;
                    case PACIFIST -> PACIFIST_GIFT_TABLE;
                    case NORMAL -> NORMAL_GIFT_TABLE;
                };

                LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(tableKey);
                if (table != null && table != LootTable.EMPTY) {
                    LootParams params = new LootParams.Builder(serverLevel)
                            .withParameter(LootContextParams.ORIGIN, wolf.position())
                            .withParameter(LootContextParams.THIS_ENTITY, wolf)
                            .create(LootContextParamSets.GIFT);
                    List<ItemStack> items = table.getRandomItems(params);
                    if (!items.isEmpty()) {
                        return items.get(0).copy();
                    }
                }
            } catch (Throwable ignored) {
                // Fallback to hardcoded pool below
            }
        }

        return rollMorningGift(personality, random);
    }

    /**
     * Rolls a personality-themed scavenged morning gift directly from hardcoded items (headless tests & fallback).
     */
    public static ItemStack rollMorningGift(WolfPersonality personality, RandomSource random) {
        if (personality == null) {
            personality = WolfPersonality.NORMAL;
        }

        // 5% rare treasure roll
        if (random.nextFloat() < 0.05f) {
            ItemStack[] rareItems = new ItemStack[] {
                    new ItemStack(Items.GOLD_NUGGET, 1 + random.nextInt(3)),
                    new ItemStack(Items.EMERALD),
                    new ItemStack(Items.NAME_TAG),
                    new ItemStack(Items.LEAD)
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
            case NORMAL -> {
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

    /**
     * Delivers the morning gift to the player and consumes feed merits.
     */
    public static void deliverGift(Wolf wolf, Player owner) {
        if (wolf == null || owner == null || wolf.level() == null || wolf.level().isClientSide()) {
            return;
        }

        Level level = wolf.level();
        int threshold = DEFAULT_FEED_THRESHOLD;
        try {
            threshold = DynamicGameRuleManager.getInt(level, BetterDogsGameRules.BD_GIFT_FEED_THRESHOLD);
        } catch (Throwable ignored) {
            threshold = DEFAULT_FEED_THRESHOLD;
        }

        long currentDay = getDayCount(level);
        WolfPersistentData.setLastGiftDay(wolf, currentDay);
        int currentFeeds = WolfPersistentData.getPersistedFeedCount(wolf);
        WolfPersistentData.setPersistedFeedCount(wolf, Math.max(0, currentFeeds - threshold));

        WolfPersonality personality = WolfPersistentData.getPersistedPersonality(wolf);
        ItemStack gift = rollMorningGift(level, wolf, personality, wolf.getRandom());

        ItemEntity itemEntity = new ItemEntity(level, owner.getX(), owner.getY() + 0.2D, owner.getZ(), gift);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);

        owner.sendOverlayMessage(Component.translatable("text.betterdogs.gift_received", wolf.getName()));

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(
                    null,
                    wolf.getX(),
                    wolf.getY(),
                    wolf.getZ(),
                    SoundEvents.WOLF_AMBIENT_BABY.value(),
                    SoundSource.NEUTRAL,
                    1.0F,
                    1.2F
            );
            WolfParticleHelper.spawnSubtleParticles(
                    wolf,
                    ParticleTypes.HAPPY_VILLAGER,
                    0.5D,
                    0.15D,
                    0.15D,
                    0.15D,
                    0.02D
            );
        }
    }
}
