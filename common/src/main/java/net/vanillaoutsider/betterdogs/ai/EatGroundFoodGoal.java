// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.DogFoodHelper;
import net.vanillaoutsider.betterdogs.util.DogTreatHelper;
import net.vanillaoutsider.betterdogs.util.WolfAdvancementHelper;
import net.vanillaoutsider.betterdogs.util.WolfDispositionHelper;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

/**
 * Dedicated single-purpose AI goal for searching, approaching, and consuming dropped food items to heal or satisfy the Hoover quirk.
 */
public class EatGroundFoodGoal extends Goal {

    private final Wolf wolf;
    private final double speedModifier;
    private ItemEntity targetItem;
    private int searchCooldown;
    private int digestionCooldown;

    public EatGroundFoodGoal(Wolf wolf, double speedModifier) {
        this.wolf = wolf;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.digestionCooldown > 0) {
            this.digestionCooldown--;
        }

        if (!this.wolf.isTame() || this.wolf.isOrderedToSit()) {
            return false;
        }

        boolean isHurt = this.wolf.getHealth() < this.wolf.getMaxHealth();
        boolean isHoover = WolfDispositionHelper.isHooverScavenger(this.wolf);

        if (!isHurt && (!isHoover || this.digestionCooldown > 0)) {
            return false;
        }

        if (--this.searchCooldown > 0) {
            return false;
        }
        this.searchCooldown = 20;

        Level level = this.wolf.level();
        if (level == null) {
            return false;
        }

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, this.wolf.getBoundingBox().inflate(10.0),
                item -> item.isAlive() && DogFoodHelper.isEdibleDogFood(level, item.getItem()) && !DogTreatHelper.shouldRefuseFood(this.wolf, item.getItem()));

        if (items.isEmpty()) {
            return false;
        }

        items.sort(Comparator.comparingDouble(this.wolf::distanceToSqr));
        this.targetItem = items.get(0);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetItem == null || !this.targetItem.isAlive() || this.wolf.isOrderedToSit()) {
            return false;
        }
        if (this.wolf.getHealth() >= this.wolf.getMaxHealth() && !WolfDispositionHelper.isHooverScavenger(this.wolf)) {
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        if (this.targetItem != null) {
            this.wolf.getNavigation().moveTo(this.targetItem, this.speedModifier);
        }
    }

    @Override
    public void stop() {
        this.targetItem = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.targetItem == null || !this.targetItem.isAlive()) {
            return;
        }

        this.wolf.getLookControl().setLookAt(this.targetItem, 30.0F, 30.0F);
        this.wolf.getNavigation().moveTo(this.targetItem, this.speedModifier);

        if (this.wolf.distanceToSqr(this.targetItem) <= 2.25) {
            consumeFoodItem();
        }
    }

    private void consumeFoodItem() {
        ItemStack stack = this.targetItem.getItem();
        if (stack.isEmpty()) {
            return;
        }

        float healAmount = DogFoodHelper.calculateHealAmount(this.wolf, stack);
        this.wolf.heal(healAmount);
        this.wolf.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
        this.digestionCooldown = 160;

        if (this.wolf.isTame() && this.wolf instanceof WolfExtensions ext) {
            ext.betterdogs$setFeedCount(ext.betterdogs$getFeedCount() + 1);

            if (this.wolf.getOwner() instanceof Player owner) {
                if (DogTreatHelper.isFavoriteTreat(this.wolf, stack)) {
                    ext.betterdogs$setZoomiesTicks(120);
                    WolfAdvancementHelper.grantAdvancement(owner, "favorite_treat");
                    WolfAdvancementHelper.grantAdvancement(owner, "zoomies");
                }
                DogTreatHelper.tryRollFavoriteTreat(this.wolf, stack, owner);
            }
        }

        stack.shrink(1);
        if (stack.isEmpty()) {
            this.targetItem.discard();
        }
        stop();
    }
}
