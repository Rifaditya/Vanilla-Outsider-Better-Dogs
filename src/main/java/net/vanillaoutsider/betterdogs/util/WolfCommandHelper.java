package net.vanillaoutsider.betterdogs.util;

import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.scheduler.events.HowlDogEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.CommandSourceStack;

import java.util.Collection;

/**
 * Command execution logic for Better Dogs debugging.
 * Strictly adheres to Modularity Mandate and Zenith Execution Guardrails.
 * Target validation and execution environment checks enforced.
 */
public class WolfCommandHelper {

    public static int setPersonality(CommandSourceStack source, Collection<? extends net.minecraft.world.entity.Entity> targets, String personalityStr) {
        int count = 0;
        WolfPersonality selectedPersonality;
        
        switch (personalityStr.toLowerCase()) {
            case "aggressive" -> selectedPersonality = WolfPersonality.AGGRESSIVE;
            case "pacifist" -> selectedPersonality = WolfPersonality.PACIFIST;
            case "normal" -> selectedPersonality = WolfPersonality.NORMAL;
            default -> {
                source.sendFailure(Component.literal("Invalid personality. Choose: aggressive, pacifist, normal."));
                return 0;
            }
        }

        for (net.minecraft.world.entity.Entity entity : targets) {
            if (entity instanceof Wolf wolf) {
                // Modularity mandate: Use extensions
                ((WolfExtensions) wolf).betterdogs$setPersonality(selectedPersonality);
                WolfStatManager.applyPersonalityStats(wolf, selectedPersonality);
                count++;
            }
        }

        if (count > 0) {
            final int finalCount = count;
            source.sendSuccess(() -> Component.literal("Successfully updated personality to " + personalityStr + " for " + finalCount + " wolves."), true);
        } else {
            source.sendFailure(Component.literal("No valid wolves selected."));
        }
        return count;
    }

    public static int executeAction(CommandSourceStack source, Collection<? extends net.minecraft.world.entity.Entity> targets, String actionStr) {
        int count = 0;

        for (net.minecraft.world.entity.Entity entity : targets) {
            if (entity instanceof Wolf wolf && !wolf.level().isClientSide()) {
                WolfExtensions ext = (WolfExtensions) wolf;
                boolean actionApplied = true;

                switch (actionStr.toLowerCase()) {
                    case "howl" -> {
                        EntitySocialScheduler scheduler = ext.betterdogs$getOrInitializeScheduler();
                        if (scheduler != null) scheduler.schedule(new HowlDogEvent());
                    }
                    case "zoomies" -> {
                        EntitySocialScheduler scheduler = ext.betterdogs$getOrInitializeScheduler();
                        if (scheduler != null) scheduler.schedule(new ZoomiesDogEvent());
                    }
                    case "mischief" -> {
                        // For mischievous target selection (requires BabyMischiefGoal active)
                        // Setting dummy target to trigger action logic if possible
                        // Note: For true mischief, rely on the scheduler or goal.
                        ext.betterdogs$setSocialState(null, WolfExtensions.SocialAction.PLAY_FIGHT, 100);
                        source.sendSuccess(() -> Component.literal("Triggering mischief generic SocialState..."), false);
                    }
                    case "disciplined" -> ext.betterdogs$setBeingDisciplined(true);
                    default -> actionApplied = false;
                }

                if (actionApplied) {
                    count++;
                }
            }
        }

        if (count > 0) {
            final int finalCount = count;
            source.sendSuccess(() -> Component.literal("Successfully triggered action '" + actionStr + "' on " + finalCount + " wolves."), true);
        } else {
            source.sendFailure(Component.literal("No valid wolves selected or invalid action (playbow, stopplaybow, howl, zoomies, mischief, disciplined)."));
        }
        return count;
    }
}
