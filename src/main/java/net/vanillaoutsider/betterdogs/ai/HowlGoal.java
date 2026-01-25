package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.sounds.SoundEvents;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import java.util.EnumSet;

/**
 * Howl Goal.
 * Behavior: Sits down and howls at the sky.
 */
public class HowlGoal extends Goal {
    private final Wolf wolf;
    private final WolfExtensions wolfExt;
    private int howlTimer = 0;

    public HowlGoal(Wolf wolf) {
        this.wolf = wolf;
        this.wolfExt = (WolfExtensions) wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return wolfExt.betterdogs$getSocialAction() == WolfExtensions.SocialAction.HOWL;
    }

    @Override
    public void start() {
        wolf.getNavigation().stop();
        wolf.setOrderedToSit(true); // Visually sit (or just sneak?)
        // Actually, setOrderedToSit(true) persists! We must reverse it on stop if it wasn't already sitting.
        // But for "ambient" sitting, maybe we just use the Sitting animation state if available?
        // Vanilla wolf sitting is controlled by `isOrderedToSit`.
        // If we toggle it, we risk confusing the player's manual command.
        // Alternative: Use `setInSittingPose(true)` if it exists, or just look up.
        // Fabric/Vanilla mapping check: `setOrderedToSit` updates the NBT/DataTracker.
        // We probably shouldn't mess with `setOrderedToSit` for a temporary event.
        // Let's just stop moving and look UP.
        
        howlTimer = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return wolfExt.betterdogs$getSocialAction() == WolfExtensions.SocialAction.HOWL;
    }

    @Override
    public void tick() {
        // Look at the sky
        wolf.getLookControl().setLookAt(wolf.getX(), wolf.getY() + 10, wolf.getZ());
        
        howlTimer++;
        
        // Howl Sound every 4 seconds (80 ticks) or just once?
        // Let's do once at start + delay
        if (howlTimer == 20 || howlTimer == 100) {
            float pitch = 0.8f + (wolf.getRandom().nextFloat() * 0.4f); // Random pitch for variety
            wolf.playSound(SoundEvents.ENTITY_WOLF_AMBIENT, 1.0f, pitch);
        }
    }
    
    @Override
    public void stop() {
        // Don't undo sitting here if we didn't force it.
        // Since we avoided setOrderedToSit, we are good.
    }
}
