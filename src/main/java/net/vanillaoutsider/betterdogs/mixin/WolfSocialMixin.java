// Verified against: Wolf.java (26.1.2+)
package net.vanillaoutsider.betterdogs.mixin;

import java.util.UUID;
import net.dasik.social.api.SocialEntity;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.vanillaoutsider.betterdogs.scheduler.events.BeggingDogEvent;
import net.vanillaoutsider.betterdogs.scheduler.events.ZoomiesDogEvent;
import net.vanillaoutsider.betterdogs.util.WolfDebugLogger;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin for Wolf entity to handle event-driven social scheduling, pack leader persistence, and group behavior logic.
 */
@Mixin(Wolf.class)
public abstract class WolfSocialMixin implements SocialEntity, WolfExtensions {

    // ========== SocialEntity Implementation & DNA ==========

    @Override
    public long betterdogs$getDNA() {
        return WolfPersistentData.getDNA((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setDNA(long dna) {
        WolfPersistentData.setDNA((Wolf) (Object) this, dna);
    }

    @Override
    public float betterdogs$getSocialScale() {
        return WolfPersistentData.getScale((Wolf) (Object) this);
    }

    @Override
    public void betterdogs$setSocialScale(float scale) {
        WolfPersistentData current = WolfPersistentData.getWolfData((Wolf) (Object) this);
        WolfPersistentData.setScale(current.personalityId(), current.lastDamageTime(), current.submissive(),
                current.bloodFeudTarget(), current.lastMischiefDay(), current.dna(), (Wolf) (Object) this, scale, current.affinityMap(), current.leaderUuid());
    }

    @Override
    public int betterdogs$getAffinity(String targetUuid) {
        return WolfPersistentData.getPersistedAffinity((Wolf) (Object) this, targetUuid);
    }

    @Override
    public void betterdogs$adjustAffinity(String targetUuid, int delta) {
        WolfPersistentData.adjustPersistedAffinity((Wolf) (Object) this, targetUuid, delta);
    }

    @Override
    public UUID betterdogs$getLeaderUuid() {
        return WolfPersistentData.getPersistedLeaderUuid((Wolf) (Object) this).orElse(null);
    }

    @Override
    public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {
        WolfPersistentData.setPersistedLeaderUuid((Wolf) (Object) this, uuid);
    }

    // ========== Dasik SocialEntity Interface Bridge ==========

    @Override
    public long dasik$getDNA() {
        return betterdogs$getDNA();
    }

    @Override
    public String dasik$getSpeciesId() {
        return "wolf";
    }

    @Override
    public LivingEntity dasik$asEntity() {
        return (LivingEntity) (Object) this;
    }

    @Override
    public float dasik$getSocialScale() {
        return betterdogs$getSocialScale();
    }

    @Override
    public net.dasik.social.api.SocialScheduler dasik$getScheduler() {
        return betterdogs$getScheduler();
    }

    // === SOCIAL CHANNEL SYSTEM ===

    @Unique
    private LivingEntity betterdogs$socialTarget = null;
    @Unique
    private WolfExtensions.SocialAction betterdogs$socialAction = WolfExtensions.SocialAction.NONE;
    @Unique
    private int betterdogs$socialModeTimer = 0;

    @Override
    public void betterdogs$setSocialState(@Nullable LivingEntity target, WolfExtensions.SocialAction action,
            int maxDurationTicks) {
        if (target != null && action != WolfExtensions.SocialAction.NONE) {
            this.betterdogs$socialTarget = target;
            this.betterdogs$socialAction = action;
            this.betterdogs$socialModeTimer = maxDurationTicks;
        } else {
            this.betterdogs$socialTarget = null;
            this.betterdogs$socialAction = WolfExtensions.SocialAction.NONE;
            this.betterdogs$socialModeTimer = 0;
            ((Wolf) (Object) this).setTarget(null);
        }
    }

    @Override
    public @Nullable LivingEntity betterdogs$getSocialTarget() {
        return this.betterdogs$socialTarget;
    }

    @Override
    public WolfExtensions.SocialAction betterdogs$getSocialAction() {
        return this.betterdogs$socialAction;
    }

    @Override
    public boolean betterdogs$isSocialModeActive() {
        return this.betterdogs$socialTarget != null && this.betterdogs$socialTarget.isAlive()
                && this.betterdogs$socialModeTimer > 0;
    }

    @Override
    public void betterdogs$tickSocialMode() {
        if (this.betterdogs$socialModeTimer > 0) {
            this.betterdogs$socialModeTimer--;
            if (this.betterdogs$socialModeTimer <= 0) {
                betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
            }
        } else if (this.betterdogs$socialTarget != null) {
            betterdogs$setSocialState(null, WolfExtensions.SocialAction.NONE, 0);
        }
    }

    // === SCHEDULER SYSTEM ===

    @Unique
    private transient EntitySocialScheduler betterdogs$socialScheduler;

    @Override
    public @Nullable EntitySocialScheduler betterdogs$getScheduler() {
        return betterdogs$socialScheduler;
    }

    @Override
    public EntitySocialScheduler betterdogs$getOrInitializeScheduler() {
        if (betterdogs$socialScheduler == null) {
            betterdogs$socialScheduler = new EntitySocialScheduler(this);
        }
        return betterdogs$socialScheduler;
    }

    @Override
    public void betterdogs$tickScheduler() {
        Wolf wolf = (Wolf) (Object) this;
        if (!wolf.level().isClientSide() && betterdogs$socialScheduler != null) {
            betterdogs$socialScheduler.tick();
            betterdogs$pollAmbientEvents();

            if (betterdogs$socialScheduler.isIdle()) {
                betterdogs$socialScheduler = null;
            }
        }
    }

    @Unique
    private long betterdogs$lastBeggingTime = 0;

    @Unique
    private void betterdogs$pollAmbientEvents() {
        Wolf wolf = (Wolf) (Object) this;
        if (wolf.tickCount % 20 != 0)
            return;

        if (wolf.isOrderedToSit())
            return;

        // Begging
        if (wolf.isTame() && wolf.onGround() && !betterdogs$socialScheduler.isEventActive(BeggingDogEvent.ID)) {
            long currentTime = wolf.level().getGameTime();
            if (currentTime - betterdogs$lastBeggingTime >= 1200) {
                betterdogs$lastBeggingTime = currentTime;
                betterdogs$socialScheduler.schedule(new BeggingDogEvent());
            }
        }

        // Morning Zoomies
        long timeOfDay = wolf.level().getDefaultClockTime() % 24000;
        if (wolf.isTame() && wolf.onGround() && timeOfDay < 3000) {
            if (!betterdogs$socialScheduler.isEventActive(ZoomiesDogEvent.ID)) {
                if (wolf.getRandom().nextFloat() < 0.01f) {
                    betterdogs$socialScheduler.schedule(new ZoomiesDogEvent());
                    WolfDebugLogger.log(wolf, "Ambient", "Morning zoomies triggered!");
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void betterdogs$onTickHead(CallbackInfo ci) {
        betterdogs$tickSocialMode();
    }

    @Unique
    private boolean betterdogs$socialInitialized = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$onTickTail(CallbackInfo ci) {
        Wolf wolf = (Wolf) (Object) this;
        if (!wolf.level().isClientSide()) {
            if (!betterdogs$socialInitialized || wolf.tickCount % 100 == 0) {
                if (!net.dasik.social.core.SocialRegistry.contains((net.dasik.social.api.SocialEntity) (Object) this)) {
                    net.dasik.social.core.SocialRegistry.register(this);
                }

                if (betterdogs$getDNA() == 0L) {
                    betterdogs$setDNA(wolf.getUUID().getMostSignificantBits());
                    float scale = 0.9f + (wolf.getRandom().nextFloat() * 0.2f);
                    betterdogs$setSocialScale(scale);
                }

                betterdogs$socialInitialized = true;
            }
        }

        if (!wolf.isTame())
            return;

        this.betterdogs$tickScheduler();
    }
}
