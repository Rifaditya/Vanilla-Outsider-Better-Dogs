package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.sounds.SoundEvent;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.mixin.WolfAccessor;
import java.util.EnumSet;

/**
 * Howl Goal.
 * Behavior: Sits down and howls at the sky.
 */
public class HowlGoal extends Goal {
    private final Wolf wolf;
    private final WolfExtensions wolfExt;
    private int howlTimer = 0;
    private boolean wasAlreadySitting = false;

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
        this.wasAlreadySitting = wolf.isOrderedToSit();
        if (!wasAlreadySitting) {
            wolf.setOrderedToSit(true);
        }
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
            // Use Accessor to get correct variant sound
            SoundEvent ambient = ((WolfAccessor)this.wolf).betterdogs$invokeGetAmbientSound();
            if (ambient != null) {
                wolf.playSound(ambient, 1.0f, pitch);
            }
        }
    }
    
    @Override
    public void stop() {
        if (!wasAlreadySitting) {
            wolf.setOrderedToSit(false);
        }
        wolf.getNavigation().stop();
    }
}
