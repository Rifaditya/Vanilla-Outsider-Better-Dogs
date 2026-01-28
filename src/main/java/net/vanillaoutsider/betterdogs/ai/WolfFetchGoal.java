package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import java.util.EnumSet;
import java.util.List;

/**
 * Fetch Goal.
 * Behavior: Finds a nearby dropped item and brings it to the owner.
 */
public class WolfFetchGoal extends Goal {
    private final Wolf wolf;
    private final WolfExtensions wolfExt;
    private ItemEntity targetItem;

    public WolfFetchGoal(Wolf wolf) {
        this.wolf = wolf;
        this.wolfExt = (WolfExtensions) wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (wolf.isOrderedToSit()) return false;
        net.vanillaoutsider.social.core.EntitySocialScheduler scheduler = wolfExt.betterdogs$getScheduler();
        if (scheduler != null && scheduler.isEventActive("fetch") && wolf.getMainHandItem().isEmpty()) {
            List<ItemEntity> items = wolf.level().getEntitiesOfClass(ItemEntity.class, wolf.getBoundingBox().inflate(8.0D));
            if (!items.isEmpty()) {
                this.targetItem = items.get(0); // Take the first one for simplicity
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        net.vanillaoutsider.social.core.EntitySocialScheduler scheduler = wolfExt.betterdogs$getScheduler();
        return scheduler != null && scheduler.isEventActive("fetch") && this.targetItem != null && this.targetItem.isAlive() && wolf.getMainHandItem().isEmpty();
    }

    @Override
    public void tick() {
        if (this.targetItem != null) {
            this.wolf.getLookControl().setLookAt(this.targetItem, 10.0F, (float)this.wolf.getMaxHeadXRot());
            this.wolf.getNavigation().moveTo(this.targetItem, 1.2D);
            
            if (this.wolf.distanceToSqr(this.targetItem) < 1.0D) {
                this.wolf.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, this.targetItem.getItem());
                this.targetItem.discard();
                // Return to owner
                if (this.wolf.getOwner() != null) {
                    this.wolf.getNavigation().moveTo(this.wolf.getOwner(), 1.2D);
                }
            }
        }
    }
}
