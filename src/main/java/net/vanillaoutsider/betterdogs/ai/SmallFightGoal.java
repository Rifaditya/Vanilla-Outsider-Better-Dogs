package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.minecraft.server.level.ServerLevel;

import java.util.EnumSet;

public class SmallFightGoal extends Goal {

    private final Wolf wolf;
    private LivingEntity target;

    public SmallFightGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(this.wolf instanceof WolfExtensions ext)) return false;
        if (ext.betterdogs$isSocialModeActive() && ext.betterdogs$getSocialAction() == WolfExtensions.SocialAction.PLAY_FIGHT) {
            this.target = ext.betterdogs$getSocialTarget();
            return this.target != null && this.target.isAlive();
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!(this.wolf instanceof WolfExtensions ext)) return false;
        if (!ext.betterdogs$isSocialModeActive()) return false;
        if (ext.betterdogs$getSocialAction() != WolfExtensions.SocialAction.PLAY_FIGHT) return false;
        return this.target != null && this.target.isAlive();
    }

    @Override
    public void start() {}

    @Override
    public void stop() {
        this.target = null;
        this.wolf.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.target == null) return;
        BetterDogsConfig config = BetterDogsConfig.get();
        this.wolf.getLookControl().setLookAt(this.target, config.getPlayFightLookSpeed(), config.getPlayFightLookSpeed());
        this.wolf.getNavigation().moveTo(this.target, config.getPlayFightSpeedModifier());
        double distSqr = this.wolf.distanceToSqr(this.target);
        double reachSqr = (double)(this.wolf.getBbWidth() * 2.0F * this.wolf.getBbWidth() * 2.0F + this.target.getBbWidth());
        if (distSqr <= reachSqr) {
            if (this.wolf.level() instanceof ServerLevel serverLevel) {
                this.wolf.doHurtTarget(serverLevel, this.target);
            }
        }
    }
}
