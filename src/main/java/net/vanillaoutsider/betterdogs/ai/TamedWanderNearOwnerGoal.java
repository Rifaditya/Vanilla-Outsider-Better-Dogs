// Verified against: DefaultRandomPos.java (26.2+)
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.Vec3;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.config.BetterDogsConfig;
import org.jspecify.annotations.Nullable;

public class TamedWanderNearOwnerGoal extends WaterAvoidingRandomStrollGoal {
    private final Wolf wolf;

    public TamedWanderNearOwnerGoal(Wolf wolf, double speedModifier) {
        super(wolf, speedModifier);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (!this.wolf.isTame() || this.wolf.isOrderedToSit() || this.wolf.isLeashed()) {
            return false;
        }
        if (this.wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
            return false;
        }
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null) {
            return false;
        }
        return super.canUse();
    }

    @Override
    protected @Nullable Vec3 getPosition() {
        LivingEntity owner = this.wolf.getOwner();
        if (owner == null) {
            return super.getPosition();
        }

        double distToOwner = this.wolf.distanceTo(owner);
        BetterDogsConfig config = BetterDogsConfig.get();

        double maxRadius = 8.0;
        if (this.wolf instanceof WolfExtensions ext) {
            maxRadius = switch (ext.betterdogs$getPersonality()) {
                case AGGRESSIVE -> 14.0;
                case PACIFIST -> 4.0;
                case NORMAL -> 8.0;
            };
        }

        // Apply follower spacing offset to expand the wander radius and prevent clumping in large groups
        java.util.UUID ownerUuid = owner.getUUID();
        int followerCount = net.vanillaoutsider.betterdogs.ai.PersonalityFollowOwnerGoal.FollowerSpacingCache.getLastKnownCount(ownerUuid);
        if (followerCount > 1) {
            float multiplier = net.dasik.social.api.gamerule.DynamicGameRuleManager.getInt(this.wolf.level(), net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER) / 100.0f;
            float maxExtra = net.dasik.social.api.gamerule.DynamicGameRuleManager.getInt(this.wolf.level(), net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX) / 10.0f;
            double spacingOffset = Math.min(Math.sqrt(followerCount - 1) * multiplier, maxExtra);
            maxRadius += spacingOffset;
        }

        // If the wolf is already outside the allowed radius from the owner,
        // force it to select a position towards the owner to return.
        if (distToOwner > maxRadius) {
            Vec3 target = DefaultRandomPos.getPosTowards(this.wolf, 10, 7, owner.position(), 1.5707963705062866);
            if (target != null) {
                return target;
            }
        }

        // Otherwise, do a standard random stroll, but ensure the target is within the maxRadius of the owner.
        // Try up to 10 times to find a position within the boundary.
        for (int i = 0; i < 10; i++) {
            Vec3 pos = super.getPosition();
            if (pos != null && pos.distanceToSqr(owner.position()) <= maxRadius * maxRadius) {
                return pos;
            }
        }

        // If we couldn't find a position within the radius after 10 attempts,
        // fall back to moving towards the owner.
        return DefaultRandomPos.getPosTowards(this.wolf, 8, 5, owner.position(), 1.5707963705062866);
    }
}
