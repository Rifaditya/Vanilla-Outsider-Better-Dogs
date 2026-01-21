package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;

import java.util.EnumSet;
import java.util.Random;

/**
 * Handles gift bring behavior for Aggressive and Pacifist wolves.
 */
public class WolfGiftGoal extends Goal {

    private final Wolf wolf;
    private int cooldown;
    private static final Random RANDOM = new Random();

    public WolfGiftGoal(Wolf wolf) {
        this.wolf = wolf;
        this.cooldown = BetterDogsConfig.get().giftCooldownMin;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!wolf.isTame() || !WolfPersistentData.hasPersistedPersonality(wolf))
            return false;

        // Decrement cooldown
        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        // Random chance to trigger logic this tick once cooldown is done
        return wolf.getRandom().nextFloat() < BetterDogsConfig.get().giftTriggerChance;
    }

    @Override
    public void start() {
        // Reset cooldown from config
        BetterDogsConfig config = BetterDogsConfig.get();
        int range = config.giftCooldownMax - config.giftCooldownMin;
        cooldown = config.giftCooldownMin + (range > 0 ? wolf.getRandom().nextInt(range) : 0);

        WolfPersonality personality = WolfPersistentData.getPersistedPersonality(wolf);
        switch (personality) {
            case AGGRESSIVE -> attemptAggressiveGift();
            case PACIFIST -> attemptPacifistGift();
            default -> {
            }
        }
    }

    private void attemptAggressiveGift() {
        // "Kills off-screen mob" - spawn loot near wolf
        int roll = RANDOM.nextInt(100);
        ItemStack loot;

        if (roll < 40)
            loot = new ItemStack(Items.BONE);
        else if (roll < 75)
            loot = new ItemStack(Items.ROTTEN_FLESH);
        else if (roll < 90)
            loot = new ItemStack(Items.ARROW);
        else
            loot = new ItemStack(Items.IRON_NUGGET);

        spawnGift(loot);
    }

    private void attemptPacifistGift() {
        // "Forager" - spawns nature items
        int roll = RANDOM.nextInt(100);
        ItemStack loot;

        if (roll < 30)
            loot = new ItemStack(Items.SWEET_BERRIES);
        else if (roll < 55)
            loot = new ItemStack(Items.WHEAT_SEEDS);
        else if (roll < 75)
            loot = new ItemStack(Items.DANDELION); // Placeholder for flowers
        else if (roll < 90)
            loot = new ItemStack(Items.RED_MUSHROOM);
        else
            loot = new ItemStack(Items.GLOW_BERRIES);

        spawnGift(loot);
    }

    private void spawnGift(ItemStack stack) {
        Level level = wolf.level();
        ItemEntity itemEntity = new ItemEntity(level, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), stack);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);

        // Wolf barks to alert owner (Optional)
        // wolf.playSound(SoundEvents.WOLF_AMBIENT, 1.0f, 1.0f);
    }
}
