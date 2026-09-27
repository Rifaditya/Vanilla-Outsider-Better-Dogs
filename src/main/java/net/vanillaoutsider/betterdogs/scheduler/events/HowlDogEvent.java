// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.scheduler.events;

import net.dasik.social.api.SocialEntity;
import net.dasik.social.api.SocialEvent;
import net.dasik.social.api.TickContext;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.registry.BetterDogsSoundEvents;

public class HowlDogEvent implements SocialEvent {
    public static final String ID = "howl";
    private int timer = 0;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public int getPriorityValue() {
        return 20;
    }

    @Override
    public String getTrackId() {
        return "main";
    }

    @Override
    public boolean canPreempt(SocialEvent other) {
        return false;
    }

    @Override
    public void onStart(TickContext context) {
        if (context.entity().dasik$asEntity() instanceof Wolf wolf) {
            wolf.playSound(BetterDogsSoundEvents.WOLF_HOWL, 1.3f, 0.5f + wolf.getRandom().nextFloat() * 0.2f);
            if (wolf instanceof WolfExtensions ext) {
                ext.betterdogs$setHowlingTicks(60);
            }
        }
    }

    @Override
    public boolean tick(TickContext context) {
        timer++;
        return timer >= 60;
    }

    @Override
    public void onEnd(SocialEntity entity, EndReason reason) {
        timer = 0;
    }
}
