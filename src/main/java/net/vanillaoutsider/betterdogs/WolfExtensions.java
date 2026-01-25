package net.vanillaoutsider.betterdogs;

import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;
import net.vanillaoutsider.betterdogs.scheduler.WolfScheduler;

/**
 * Extension interface for Wolf entity to access personality and training data.
 * Implemented via mixin.
 * v1.10.000: Simplified for baby training system.
 */
public interface WolfExtensions {
    
    // ========== Personality ==========
    
    /**
     * Get the wolf's personality (only meaningful for tamed wolves)
     */
    WolfPersonality betterdogs$getPersonality();

    /**
     * Set the wolf's personality (called on tame)
     */
    void betterdogs$setPersonality(WolfPersonality personality);

    /**
     * Check if this wolf has been assigned a personality
     */
    boolean betterdogs$hasPersonality();

    // ========== Last Damage Time (for passive healing) ==========

    /**
     * Get the last time this wolf took damage
     */
    int betterdogs$getLastDamageTime();

    /**
     * Update the last damage time
     */
    void betterdogs$setLastDamageTime(int time);

    // ========== Submissive (baby cannot attack pack after correction) ==========

    /**
     * Check if this wolf is submissive (cannot attack pack members)
     */
    boolean betterdogs$isSubmissive();

    /**
     * Set submissive state
     */
    void betterdogs$setSubmissive(boolean submissive);

    // ========== Blood Feud (permanent vendetta) ==========

    /**
     * Get the UUID of the nemesis wolf (empty string = no feud)
     */
    String betterdogs$getBloodFeudTarget();

    /**
     * Set the blood feud target UUID
     */
    void betterdogs$setBloodFeudTarget(String targetUuid);

    /**
     * Check if this wolf has an active blood feud
     */
    boolean betterdogs$hasBloodFeud();

    // ========== Last Mischief Day (daily mischief tracking) ==========

    /**
     * Get the last game day when this wolf caused mischief
     */
    long betterdogs$getLastMischiefDay();

    /**
     * Set the last mischief day
     */
    void betterdogs$setLastMischiefDay(long day);

    // ========== Dunce Cap (Transient Disciplinary State) ==========

    /**
     * Check if this wolf is currently being disciplined (transient flag).
     * Used to prevent multiple adults from correcting the same baby.
     */
    boolean betterdogs$isBeingDisciplined();

    /**
     * Set disciplinary state (transient).
     */
    void betterdogs$setBeingDisciplined(boolean isBeingDisciplined);
    
    // === SOCIAL CHANNEL SYSTEM (V2.1 - Modular) ===

    enum SocialAction {
        NONE,
        RETALIATION, // Baby biting owner (Provocation)
        DISCIPLINE,  // Adult correcting baby
        PLAY_FIGHT,  // Small fight event (10s)
        ZOOMIES,     // Hyperactive running (5-8s)
        HOWL,        // Social howling (10s)
        PLAY,        // Future expansion
        WARNING      // Future expansion
    }
    
    /**
     * Sets the state for the "Social Brain".
     * @param target The entity to interact with.
     * @param action The context of the interaction (RETALIATION, DISCIPLINE, etc).
     * @param maxDurationTicks Safety timer.
     */
    void betterdogs$setSocialState(@Nullable LivingEntity target, SocialAction action, int maxDurationTicks);

    /**
     * Gets the current Social Target.
     */
    @Nullable LivingEntity betterdogs$getSocialTarget();

    /**
     * Gets the current Social Action (Context).
     */
    SocialAction betterdogs$getSocialAction();

    /**
     * Checks if the "Social Brain" is currently active.
     */
    boolean betterdogs$isSocialModeActive();

    /**
     * Ticks the Social Mode timer. Called every tick by WolfMixin.
     */
     void betterdogs$tickSocialMode();
     
    // === SCHEDULER SYSTEM (V3.0 - Modular) ===

    /**
     * Gets the WolfScheduler instance for this wolf.
     */
    WolfScheduler betterdogs$getScheduler();

    /**
     * Ticks the Long-Term Scheduler. Called every tick by WolfMixin.
     */
    void betterdogs$tickScheduler();
}
