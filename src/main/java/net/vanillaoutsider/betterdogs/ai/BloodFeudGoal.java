package net.vanillaoutsider.betterdogs.ai;

import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;

/**
 * AI Goal: Blood Feud - permanent vendetta between two wolves.
 * Fight to the death when in range. Ignores friendly fire protection.
 * VO Philosophy: Respects player sit commands - does NOT force wolves to stand.
 */
public class BloodFeudGoal extends Goal {

    private final Wolf wolf;
    private Wolf nemesis;

    public BloodFeudGoal(Wolf wolf) {
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        // Must have a blood feud target
        if (!(wolf instanceof WolfExtensions ext))
            return false;

        if (!ext.betterdogs$hasBloodFeud())
            return false;

        // VO Philosophy: Respect player sit command
        if (wolf.isOrderedToSit())
            return false;

        // Find nemesis by UUID
        String nemesisUuid = ext.betterdogs$getBloodFeudTarget();
        Wolf found = findNemesis(nemesisUuid);
        if (found == null)
            return false;

        this.nemesis = found;
        return true;
    }

    private Wolf findNemesis(String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            AABB searchBox = wolf.getBoundingBox().inflate(20.0); // Detection range
            
            for (Wolf w : wolf.level().getEntitiesOfClass(Wolf.class, searchBox)) {
                if (w.getUUID().equals(uuid) && w.isAlive()) {
                    return w;
                }
            }
        } catch (IllegalArgumentException ignored) {
            // Invalid UUID, clear blood feud
            if (wolf instanceof WolfExtensions ext) {
                ext.betterdogs$setBloodFeudTarget("");
            }
        }
        return null;
    }

    @Override
    public void start() {
        if (nemesis != null) {
            wolf.setTarget(nemesis);
        }
    }

    @Override
    public boolean canContinueToUse() {
        // Fight until one dies
        if (nemesis == null || !nemesis.isAlive())
            return false;

        // VO Philosophy: Stop if player sits the wolf
        if (wolf.isOrderedToSit())
            return false;

        // Keep attacking
        return wolf.getTarget() == nemesis;
    }

    @Override
    public void stop() {
        // Check if nemesis is dead - clear blood feud
        if (nemesis != null && !nemesis.isAlive()) {
            if (wolf instanceof WolfExtensions ext) {
                ext.betterdogs$setBloodFeudTarget("");
            }
        }
        this.nemesis = null;
    }
}
