// Verified against: WolfGroupMixin.java (26.1.2+)
package net.vanillaoutsider.betterdogs.mixin;

import java.util.UUID;
import net.dasik.social.api.group.FlockType;
import net.dasik.social.api.group.GroupMember;
import net.dasik.social.core.group.FlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Mixin to handle flocking/group behavior for wolves by implementing GroupMember.
 */
@Mixin(Wolf.class)
public abstract class WolfGroupMixin implements GroupMember {

    @Unique
    private LivingEntity betterdogs$leader = null;

    @Unique
    private FlockState betterdogs$flockState = null;

    @Override
    public LivingEntity getLeader() {
        if (this.betterdogs$leader == null || !this.betterdogs$leader.isAlive()) {
            Wolf wolf = (Wolf) (Object) this;
            if (wolf instanceof WolfExtensions ext) {
                UUID uuid = ext.betterdogs$getLeaderUuid();
                if (uuid != null && wolf.level() instanceof ServerLevel serverLevel) {
                    Entity entity = serverLevel.getEntity(uuid);
                    if (entity instanceof LivingEntity living) {
                        this.betterdogs$leader = living;
                    }
                }
            }
        }
        return this.betterdogs$leader;
    }

    @Override
    public boolean hasLeader() {
        if ((Object) this instanceof WolfExtensions ext) {
            return ext.betterdogs$getLeaderUuid() != null;
        }
        return false;
    }

    @Override
    public void setLeader(@Nullable LivingEntity leader) {
        this.betterdogs$leader = leader;
        if ((Object) this instanceof WolfExtensions ext) {
            ext.betterdogs$setLeaderUuid(leader != null ? leader.getUUID() : null);
        }
    }

    @Override
    public int getGroupSize() {
        FlockState state = this.betterdogs$flockState;
        if (state != null) {
            return state.getMemberCount();
        }
        if (this.betterdogs$leader instanceof GroupMember leaderGM) {
            FlockState leaderState = leaderGM.getFlockState();
            if (leaderState != null) {
                return leaderState.getMemberCount();
            }
        }
        return 1;
    }

    @Override
    public FlockType getFlockType() {
        return FlockType.TERRESTRIAL;
    }

    @Override
    public FlockState getFlockState() {
        return this.betterdogs$flockState;
    }

    @Override
    public void setFlockState(FlockState state) {
        this.betterdogs$flockState = state;
    }
}
