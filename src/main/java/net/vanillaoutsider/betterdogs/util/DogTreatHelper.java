// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.util;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Single-purpose helper for high-value dog favorite treat preferences,
 * deterministic bit-mixing hashing, and potent treat rejuvenation buffs.
 */
public final class DogTreatHelper {

    private static List<Item> ACTIVE_TREAT_POOL = null;

    private DogTreatHelper() {
    }

    /**
     * Retrieves the active treat pool with optional modded items (e.g., Farmer's Delight).
     */
    public static List<Item> getActiveTreatPool() {
        if (ACTIVE_TREAT_POOL == null) {
            List<Item> pool = new ArrayList<>();
            try {
                pool.add(Items.COOKED_MUTTON);
                pool.add(Items.RABBIT_STEW);
                pool.add(Items.SPIDER_EYE);
                pool.add(Items.GOLDEN_APPLE);
                pool.add(Items.BAKED_POTATO);
                pool.add(Items.PORKCHOP);
                pool.add(Items.ROTTEN_FLESH);
                pool.add(Items.PUMPKIN_PIE);
                pool.add(Items.GLOW_BERRIES);

                if (FabricLoader.getInstance() != null && FabricLoader.getInstance().isModLoaded("farmersdelight")) {
                    String[] fdItems = new String[]{
                            "dog_food", "minced_beef", "mutton_chops", "cooked_mutton_chops",
                            "bacon", "cooked_bacon", "chicken_cuts", "cooked_chicken_cuts",
                            "ham", "smoked_ham", "beef_stew", "chicken_soup", "vegetable_soup", "fish_stew"
                    };
                    for (String path : fdItems) {
                        Identifier id = Identifier.fromNamespaceAndPath("farmersdelight", path);
                        BuiltInRegistries.ITEM.getOptional(id).ifPresent(item -> {
                            if (item != Items.AIR) {
                                pool.add(item);
                            }
                        });
                    }
                }
            } catch (Throwable ignored) {
                // Headless test environment fallback
            }
            ACTIVE_TREAT_POOL = pool;
        }
        return ACTIVE_TREAT_POOL;
    }

    /**
     * Pure zero-allocation bit-mixing hash over the wolf UUID to deterministically calculate treat slot index.
     */
    public static int calculateTreatIndex(UUID uuid, int poolSize) {
        if (uuid == null || poolSize <= 0) {
            return 0;
        }
        long seed = uuid.getLeastSignificantBits();
        int hash = (int) (seed ^ (seed >>> 32)) & 0x7FFFFFFF;
        return hash % poolSize;
    }

    /**
     * Retrieves the favorite treat for the given UUID from the active treat pool.
     */
    public static Item getFavoriteTreat(UUID uuid) {
        List<Item> pool = getActiveTreatPool();
        if (pool == null || pool.isEmpty()) {
            return Items.COOKED_BEEF;
        }
        int index = calculateTreatIndex(uuid, pool.size());
        return pool.get(index);
    }

    /**
     * Retrieves the favorite treat for the given wolf.
     */
    public static Item getFavoriteTreat(Wolf wolf) {
        if (wolf == null) {
            return Items.COOKED_BEEF;
        }
        return getFavoriteTreat(wolf.getUUID());
    }

    /**
     * Checks if the given item stack matches the wolf's favorite treat.
     */
    public static boolean isFavoriteTreat(Wolf wolf, ItemStack stack) {
        if (wolf == null || stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.is(getFavoriteTreat(wolf));
    }

    /**
     * Checks if the player is holding the wolf's favorite treat in either hand.
     */
    public static boolean isHoldingFavoriteTreat(Wolf wolf, Player player) {
        if (wolf == null || player == null) {
            return false;
        }
        return isFavoriteTreat(wolf, player.getMainHandItem()) || isFavoriteTreat(wolf, player.getOffhandItem());
    }

    /**
     * Checks if an item stack is general canine food or bone.
     */
    public static boolean isCanineFood(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.is(Items.BONE)) {
            return true;
        }
        try {
            return stack.is(net.minecraft.tags.ItemTags.MEAT) || stack.has(net.minecraft.core.component.DataComponents.FOOD);
        } catch (Throwable ignored) {
            // Headless / test fallback
            return stack.is(Items.BONE) || stack.is(Items.COOKED_BEEF) || stack.is(Items.BEEF) || stack.is(Items.PORKCHOP) || stack.is(Items.COOKED_PORKCHOP) || stack.is(Items.CHICKEN) || stack.is(Items.COOKED_CHICKEN) || stack.is(Items.MUTTON) || stack.is(Items.COOKED_MUTTON) || stack.is(Items.ROTTEN_FLESH);
        }
    }

    /**
     * Checks if the player is holding the dog's favorite treat, meat, or bones in either hand.
     */
    public static boolean isHoldingFoodOrTreat(Wolf wolf, Player player) {
        if (wolf == null || player == null) {
            return false;
        }
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return isFavoriteTreat(wolf, main) || isFavoriteTreat(wolf, off) || isCanineFood(main) || isCanineFood(off);
    }

    /**
     * Checks if the wolf can be fed its favorite treat.
     */
    public static boolean canFeedFavoriteTreat(Wolf wolf, Player player, InteractionHand hand, ItemStack stack) {
        if (wolf == null || player == null || stack == null || stack.isEmpty()) {
            return false;
        }

        if (hand != InteractionHand.MAIN_HAND) {
            return false;
        }

        if (!wolf.isTame() || !wolf.isOwnedBy(player)) {
            return false;
        }

        if (!DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_FAVORITE_TREATS)) {
            return false;
        }

        Item favorite = getFavoriteTreat(wolf);
        if (!stack.is(favorite)) {
            return false;
        }

        // Allow feeding if hurt or non-toxic treat
        return wolf.getHealth() < wolf.getMaxHealth() || !stack.is(Items.ROTTEN_FLESH);
    }

    /**
     * Attempts to feed a dog its favorite treat with 6D guards, applying full healing,
     * Regeneration II, Zoomies running bursts, Jade tooltip discovery, and particle feedback.
     */
    public static InteractionResult tryFeedFavoriteTreat(Wolf wolf, Player player, InteractionHand hand, ItemStack stack) {
        if (!canFeedFavoriteTreat(wolf, player, hand, stack)) {
            return InteractionResult.PASS;
        }

        if (wolf.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // 1. Consume treat (Creative Bypass)
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        // 2. Mark treat as discovered in persistent data (shows in Jade tooltip)
        WolfPersistentData.setDiscoveredTreat(wolf, true);

        // 3. Full Health Restoration & Regeneration II (45s)
        wolf.setHealth(wolf.getMaxHealth());
        wolf.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));

        // 4. Trigger Zoomies & Record Soothed Time
        if (wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setSoothedTime(wolf.level().getGameTime());
            var scheduler = ext.betterdogs$getScheduler();
            WolfZoomiesHelper.triggerZoomies(wolf);
            if (scheduler != null && !scheduler.isEventActive(ZoomiesDogEvent.ID)) {
                scheduler.schedule(new ZoomiesDogEvent());
            }
        }

        // 5. Play Audio Feedback
        if (wolf instanceof WolfAccessor accessor) {
            var ambientSound = accessor.betterdogs$invokeGetAmbientSound();
            if (ambientSound != null) {
                wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), ambientSound, wolf.getSoundSource(), 1.0f, 1.5f);
            }
        }

        // 6. Visual Particles
        if (wolf.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getRandomX(1.0), wolf.getRandomY() + 0.5, wolf.getRandomZ(1.0), 10, 0.2, 0.2, 0.2, 0.05);
            serverLevel.sendParticles(ParticleTypes.HEART, wolf.getRandomX(1.0), wolf.getRandomY() + 0.5, wolf.getRandomZ(1.0), 5, 0.2, 0.2, 0.2, 0.05);
        }

        return InteractionResult.SUCCESS;
    }
}
