package net.vanillaoutsider.betterdogs.scheduler;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.Entity;

public interface WolfEvent {
    String getId();

    boolean canTrigger(Wolf wolf);

    void onStart(Wolf wolf, Entity contextEntity);

    void tick(Wolf wolf);

    void onEnd(Wolf wolf);
}
