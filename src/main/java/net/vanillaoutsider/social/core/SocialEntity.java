package net.vanillaoutsider.social.core;

import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;
import java.util.UUID;

/**
 * Interface for any entity that participates in the Hive Mind social system.
 */
public interface SocialEntity {
    
    /**
     * @return The unique DNA seed for this individual, derived from UUID and Species.
     */
    long betterdogs$getDNA();

    /**
     * @return The species identifier (e.g., "wolf", "cat").
     */
    String betterdogs$getSpeciesId();

    /**
     * @return The underlying Minecraft entity.
     */
    LivingEntity betterdogs$asEntity();

    /**
     * @return The persistent scale of this entity (derived from DNA).
     */
    float betterdogs$getSocialScale();

    /**
     * @return The scheduler instance, or null if this mob opts out of scheduling or is idle.
     */
    @Nullable
    EntitySocialScheduler betterdogs$getScheduler();

    /**
     * @return The scheduler instance, creating it if it doesn't already exist.
     */
    EntitySocialScheduler betterdogs$getOrInitializeScheduler();
}
