package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.dasik.social.api.group.GroupMember;

/**
 * Helper class for Wolf social logic.
 * Decoupled from WolfMixin for modularity.
 * Verified against: Wolf.java (Snapshot 10/11)
 */
public class WolfSocialHelper {

    /**
     * Efficiently calculates the number of nearby pack members.
     * Uses SocialRegistry if available for O(1) or O(log N) lookup.
     */
    public static int getGroupSize(Wolf wolf) {
        if (wolf.level().isClientSide()) return 0;
        
        // OPTIMIZATION: Use GroupMember interface
        if (wolf instanceof GroupMember member) {
            return member.getGroupSize();
        }

        // Fallback to bounding box (Obsolescent)
        AABB box = wolf.getBoundingBox().inflate(16.0);
        return wolf.level().getEntitiesOfClass(Wolf.class, box, 
            other -> other != wolf && other.isTame() && 
            (other.getOwner() != null && other.getOwner().equals(wolf.getOwner()))).size();
    }
    
    /**
     * Handles social scaling logic.
     */
    public static float calculateSocialScale(Wolf wolf, long dna) {
        // DNA-based scaling logic (Simplified for helper)
        float base = 1.0f;
        if ((dna & 1L) == 1L) base += 0.1f;
        if ((dna & 2L) == 2L) base -= 0.1f;
        return base;
    }
}
