package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.jspecify.annotations.Nullable;

public class RetaliationDogEvent implements SocialEvent {
    public static final String ID = "retaliation";
    private int tickCount = 0;
    private final LivingEntity target;

    public RetaliationDogEvent(LivingEntity target) {
        this.target = target;
    }

    // Default constructor for registration/reflection if needed
    public RetaliationDogEvent() {
        this.target = null;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int getPriorityValue() {
        return 20; // High priority (Survival/Combat)
    }

    @Override
    public String getTrackId() {
        return "main";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return other.getPriorityValue() < 20;
    }

    @Override
    public void onStart(TickContext context) {
        this.tickCount = 0;
        if (this.target != null && context.entity() instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(this.target, WolfExtensions.SocialAction.RETALIATION, 100);
        }
    }

    @Override
    public boolean tick(TickContext context) {
        this.tickCount++;
        if (this.target == null || !this.target.isAlive())
            return true;

        // Ensure state is maintained (in case something reset it)
        if (context.entity() instanceof WolfExtensions ext) {
            if (!ext.betterdogs$isSocialModeActive()) {
                ext.betterdogs$setSocialState(this.target, WolfExtensions.SocialAction.RETALIATION,
                        100 - this.tickCount);
            }
        }

        return this.tickCount >= 100; // 5 seconds
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        if (entity instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
    }
}
