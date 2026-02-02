package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.jspecify.annotations.Nullable;

public class CorrectionDogEvent implements SocialEvent {
    public static final String ID = "correction";
    private int tickCount = 0;
    private final LivingEntity target;

    public CorrectionDogEvent(LivingEntity target) {
        this.target = target;
    }

    public CorrectionDogEvent() {
        this.target = null;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int getPriorityValue() {
        return 15; // Higher than Normal, lower than Retaliation
    }

    @Override
    public String getTrackId() {
        return "main";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return other.getPriorityValue() < 15;
    }

    @Override
    public void onStart(TickContext context) {
        this.tickCount = 0;
        if (this.target != null && context.entity() instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(this.target, WolfExtensions.SocialAction.DISCIPLINE, 60);
        }
    }

    @Override
    public boolean tick(TickContext context) {
        this.tickCount++;
        if (this.target == null || !this.target.isAlive())
            return true;

        return this.tickCount >= 60; // 3 seconds
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        if (entity instanceof WolfExtensions ext) {
            ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
    }
}
