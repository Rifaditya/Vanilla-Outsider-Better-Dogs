package net.vanillaoutsider.betterdogs.scheduler.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.scheduler.WolfEvent;
import net.vanillaoutsider.betterdogs.BetterDogs;

public class CorrectionDogEvent implements WolfEvent {

    public static final String ID = "betterdogs:correction";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean canTrigger(Wolf wolf) {
        return false;
    }

    @Override
    public void onStart(Wolf wolf, Entity contextEntity) {
        if (contextEntity instanceof Wolf && wolf instanceof WolfExtensions) {
            Wolf baby = (Wolf) contextEntity;
            WolfExtensions ext = (WolfExtensions) wolf;

            ext.betterdogs$setSocialState(baby, WolfExtensions.SocialAction.DISCIPLINE, 100);

            if (baby instanceof WolfExtensions) {
                ((WolfExtensions) baby).betterdogs$setBeingDisciplined(true);
            }
        }
    }

    @Override
    public void tick(Wolf wolf) {
    }

    @Override
    public void onEnd(Wolf wolf) {
        if (wolf instanceof WolfExtensions) {
            WolfExtensions ext = (WolfExtensions) wolf;
            if (ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.DISCIPLINE) {
                LivingEntity target = ext.betterdogs$getSocialTarget();
                if (target instanceof Wolf && target instanceof WolfExtensions) {
                    ((WolfExtensions) target).betterdogs$setBeingDisciplined(false);
                }
                ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
            }
        }
    }
}
