// Verified against: Wolf.java (26.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityReference;
import java.util.UUID;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfDebugLogger;
import net.vanillaoutsider.betterdogs.util.WolfParticleHandler;
import net.vanillaoutsider.betterdogs.util.WolfStatManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to handle player interactions, taming side-effects, and taming particles.
 */
@Mixin(Wolf.class)
public abstract class WolfInteractMixin extends TamableAnimal {

    protected WolfInteractMixin() {
        super(null, null);
    }
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        ItemStack itemStack = player.getItemInHand(hand);

        if (wolf.isTame() && wolf.isOwnedBy(player) && itemStack.is(Items.GOLDEN_APPLE) && hand == InteractionHand.MAIN_HAND) {
            if (wolf instanceof WolfExtensions ext && net.vanillaoutsider.betterdogs.WolfPersistentData.isPersistedInbred(wolf)) {
                if (DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_ENABLE_INBRED_CURING)) {
                    if (!wolf.level().isClientSide()) {
                        itemStack.consume(1, player);
                        
                        float finalHp = net.vanillaoutsider.betterdogs.WolfPersistentData.getPersistedHealthBonus(wolf);
                        float finalDmg = net.vanillaoutsider.betterdogs.WolfPersistentData.getPersistedDamageMod(wolf);
                        float finalSpeed = net.vanillaoutsider.betterdogs.WolfPersistentData.getPersistedSpeedMod(wolf);
                        
                        // Inverse formulas:
                        // finalHp = avgHp * 0.6f - 6.0f;  =>  avgHp = (finalHp + 6.0f) / 0.6f;
                        // finalDmg = avgDmg * 0.6f - 0.20f; => avgDmg = (finalDmg + 0.20f) / 0.6f;
                        // finalSpeed = avgSpeed * 0.6f - 0.15f; => avgSpeed = (finalSpeed + 0.15f) / 0.6f;
                        float avgHp = (finalHp + 6.0f) / 0.6f;
                        float avgDmg = (finalDmg + 0.20f) / 0.6f;
                        float avgSpeed = (finalSpeed + 0.15f) / 0.6f;
                        
                        avgHp = Math.max(avgHp, -30.0f);
                        avgDmg = Math.max(avgDmg, -0.8f);
                        avgSpeed = Math.max(avgSpeed, -0.6f);
                        
                        net.vanillaoutsider.betterdogs.WolfPersistentData.setPersistedHealthBonus(wolf, avgHp);
                        net.vanillaoutsider.betterdogs.WolfPersistentData.setPersistedDamageMod(wolf, avgDmg);
                        net.vanillaoutsider.betterdogs.WolfPersistentData.setPersistedSpeedMod(wolf, avgSpeed);
                        
                        java.util.Optional<UUID> p1 = net.vanillaoutsider.betterdogs.WolfPersistentData.getWolfData(wolf).parent1Uuid();
                        java.util.Optional<UUID> p2 = net.vanillaoutsider.betterdogs.WolfPersistentData.getWolfData(wolf).parent2Uuid();
                        net.vanillaoutsider.betterdogs.WolfPersistentData.setPersistedParentsAndInbred(wolf, p1.orElse(null), p2.orElse(null), false);
                        
                        WolfPersonality personality = ext.betterdogs$getPersonality();
                        WolfStatManager.applyPersonalityStats(wolf, personality);
                        
                        wolf.setHealth(wolf.getMaxHealth());
                        
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, wolf.getSoundSource(), 1.0f, 1.0f);
                        
                        if (wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, wolf.getRandomX(1.0), wolf.getRandomY() + 0.5, wolf.getRandomZ(1.0), 10, 0.2, 0.2, 0.2, 0.05);
                        }
                        
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.cured_inbred", wolf.getName()));
                    }
                    cir.setReturnValue(InteractionResult.SUCCESS);
                    return;
                }
            }
        }

        if (wolf.isTame() && wolf instanceof WolfExtensions ext) {
            // 1. Triggering / Toggling Pending Adoption
            if (wolf.isOwnedBy(player) && player.isSecondaryUseActive() && itemStack.is(Items.PAPER) && hand == InteractionHand.MAIN_HAND) {
                if (!wolf.level().isClientSide()) {
                    boolean currentAdoptable = ext.betterdogs$isAdoptable();
                    boolean newAdoptable = !currentAdoptable;
                    ext.betterdogs$setAdoptable(newAdoptable);

                    if (newAdoptable) {
                        wolf.setOrderedToSit(true);
                        wolf.getNavigation().stop();
                        wolf.setTarget(null);
                        wolf.stopBeingAngry();
                        if (ext.betterdogs$isGuardMode()) {
                            ext.betterdogs$setSittingManually(true);
                        }
                        itemStack.consume(1, player);
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.BOOK_PAGE_TURN, wolf.getSoundSource(), 1.0f, 1.0f);
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.adoption_pending", wolf.getName()));
                    } else {
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.BOOK_PAGE_TURN, wolf.getSoundSource(), 1.0f, 1.0f);
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.adoption_cancelled", wolf.getName()));
                    }
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }

            // 2. Claiming / Adopting by another player
            if (!wolf.isOwnedBy(player) && ext.betterdogs$isAdoptable() && itemStack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
                if (!wolf.level().isClientSide()) {
                    EntityReference<LivingEntity> oldOwnerRef = wolf.getOwnerReference();
                    UUID oldOwnerUuid = oldOwnerRef != null ? oldOwnerRef.getUUID() : null;

                    // Tame/Adopt the wolf
                    wolf.tame(player);
                    wolf.setOrderedToSit(false);
                    ext.betterdogs$setAdoptable(false);

                    // Re-apply personality stats for the new owner relationship
                    WolfPersonality personality = ext.betterdogs$getPersonality();
                    WolfStatManager.applyPersonalityStats(wolf, personality);

                    // Play effects
                    wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), ((WolfAccessor) this).betterdogs$invokeGetSoundSet().ambientSound().value(), wolf.getSoundSource(), 1.0f, 1.0f);
                    if (wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.HEART, wolf.getRandomX(1.0), wolf.getRandomY() + 0.5, wolf.getRandomZ(1.0), 5, 0.2, 0.2, 0.2, 0.02);
                    }

                    // Notify players
                    player.sendOverlayMessage(Component.translatable("text.betterdogs.adopted_new_owner", wolf.getName()));
                    if (oldOwnerUuid != null) {
                        Player oldOwner = wolf.level().getPlayerByUUID(oldOwnerUuid);
                        if (oldOwner != null) {
                            oldOwner.sendOverlayMessage(Component.translatable("text.betterdogs.adopted_old_owner", wolf.getName(), player.getName()));
                        }
                    }
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }

            // 3. Cancelling adoption by normal owner interaction (not shifting with paper)
            if (wolf.isOwnedBy(player) && ext.betterdogs$isAdoptable()) {
                if (!player.isSecondaryUseActive() || !itemStack.is(Items.PAPER)) {
                    if (!wolf.level().isClientSide()) {
                        ext.betterdogs$setAdoptable(false);
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), SoundEvents.BOOK_PAGE_TURN, wolf.getSoundSource(), 1.0f, 1.0f);
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.adoption_cancelled", wolf.getName()));
                    }
                    cir.setReturnValue(InteractionResult.SUCCESS);
                    return;
                }
            }
        }

        // Shift + Right click empty hand: calm down interaction
        if (wolf.isTame() && wolf.isOwnedBy(player) && player.isSecondaryUseActive() && itemStack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
            if (!wolf.level().isClientSide()) {
                wolf.setOrderedToSit(true);
                wolf.getNavigation().stop();
                wolf.setTarget(null);
                
                // Clear persistent anger (calm down)
                wolf.stopBeingAngry();
                
                if (wolf instanceof WolfExtensions ext) {
                    if (ext.betterdogs$isGuardMode()) {
                        ext.betterdogs$setSittingManually(true);
                    }
                }
                
                net.minecraft.sounds.SoundEvent sound = ((WolfAccessor) this).betterdogs$invokeGetSoundSet().whineSound().value();
                wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), sound, wolf.getSoundSource(), 1.0f, 1.2f);
                
                player.sendOverlayMessage(Component.translatable("text.betterdogs.calmed_down", wolf.getName()));
                
                if (wolf.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SMOKE, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 5, 0.2, 0.2, 0.2, 0.02);
                }
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        if (wolf.isTame() && wolf.isOwnedBy(player) && itemStack.is(Items.BONE) && player.isSecondaryUseActive()) {
            if (wolf instanceof WolfExtensions ext) {
                boolean currentGuard = ext.betterdogs$isGuardMode();
                boolean newGuard = !currentGuard;
                
                if (!wolf.level().isClientSide()) {
                    ext.betterdogs$setGuardMode(newGuard);
                    if (newGuard) {
                        ext.betterdogs$setGuardPos(wolf.blockPosition());
                        BlockPos pos = wolf.blockPosition();
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.guard_activated", wolf.getName(), pos.getX(), pos.getY(), pos.getZ()));
                        
                        WolfPersonality personality = ext.betterdogs$getPersonality();
                        float pitch = switch (personality) {
                            case AGGRESSIVE -> 0.8f;
                            case NORMAL -> 1.2f;
                            case PACIFIST -> 1.5f;
                        };
                        net.minecraft.sounds.SoundEvent sound = personality == WolfPersonality.PACIFIST ? 
                            ((WolfAccessor) this).betterdogs$invokeGetSoundSet().whineSound().value() : 
                            ((WolfAccessor) this).betterdogs$invokeGetSoundSet().ambientSound().value();
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), sound, wolf.getSoundSource(), 1.0f, pitch);
                    } else {
                        ext.betterdogs$setGuardPos(null);
                        ext.betterdogs$setSittingManually(false);
                        player.sendOverlayMessage(Component.translatable("text.betterdogs.guard_deactivated", wolf.getName()));
                        wolf.level().playSound(null, wolf.getX(), wolf.getY(), wolf.getZ(), ((WolfAccessor) this).betterdogs$invokeGetSoundSet().ambientSound().value(), wolf.getSoundSource(), 1.0f, 1.0f);
                    }
                    itemStack.consume(1, player);
                }
                
                // Force standing pose on both client and server when toggling guard mode
                wolf.setOrderedToSit(false);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        if (wolf.isTame() && wolf.isOwnedBy(player) && hand == InteractionHand.MAIN_HAND) {
            if (wolf instanceof WolfExtensions ext && ext.betterdogs$isGuardMode()) {
                if (!itemStack.is(Items.BONE)
                    && !wolf.isFood(itemStack) && !itemStack.is(net.minecraft.tags.ItemTags.WOLF_COLLAR_DYES) 
                    && !wolf.isEquippableInSlot(itemStack, net.minecraft.world.entity.EquipmentSlot.BODY)
                    && !(wolf.isInSittingPose() && wolf.isWearingBodyArmor() && wolf.getBodyArmorItem().isDamaged() && wolf.getBodyArmorItem().isValidRepairItem(itemStack))) {
                    
                    if (!wolf.level().isClientSide()) {
                        boolean currentSitting = ext.betterdogs$isSittingManually();
                        boolean newSitting = !currentSitting;
                        ext.betterdogs$setSittingManually(newSitting);
                        wolf.setOrderedToSit(newSitting);
                        wolf.getNavigation().stop();
                        wolf.setTarget(null);
                    }
                    cir.setReturnValue(InteractionResult.SUCCESS);
                    return;
                }
            }
        }

        if (player.getItemInHand(hand).getItem() instanceof DebugStickItem) {
            if (DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_DEBUGGING)
                && player.permissions().hasPermission(net.minecraft.server.permissions.Permissions.COMMANDS_GAMEMASTER)) {
                if (!wolf.level().isClientSide()) {
                    if (wolf instanceof WolfExtensions ext) {
                        if (player.isSecondaryUseActive()) {
                            // Shift + Click: Cycle Scale
                            float currentScale = ext.betterdogs$getSocialScale();
                            float nextScale = currentScale + 0.1f;
                            if (nextScale > 1.5f) nextScale = 0.5f;
                            ext.betterdogs$setSocialScale(nextScale);
                            player.sendOverlayMessage(Component.literal("§b[Debug] §fScale: " + String.format("%.1f", nextScale)));
                            WolfDebugLogger.log(wolf, "DebugStick", "Scale changed to " + nextScale);
                        } else {
                            // Normal Click: Cycle Personality
                            WolfPersonality current = ext.betterdogs$getPersonality();
                            WolfPersonality next = current.next();
                            ext.betterdogs$setPersonality(next);
                            // Force re-apply stats
                            WolfStatManager.applyPersonalityStats(wolf, next);
                            player.sendOverlayMessage(Component.literal("§b[Debug] §fPersonality: " + next.name()));
                            WolfDebugLogger.log(wolf, "DebugStick", "Personality changed to " + next.name());
                        }
                    }
                }
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "applyTamingSideEffects", at = @At("HEAD"))
    private void betterdogs$onApplyTamingSideEffects(CallbackInfo ci) {
        if (!this.isTame() || this.level().isClientSide())
            return;

        Wolf wolf = (Wolf) (Object) this;
        if (wolf instanceof WolfExtensions ext) {
            if (!ext.betterdogs$hasPersonality()) {
                WolfPersonality personality = WolfPersonality.random(this.level());
                ext.betterdogs$setPersonality(personality);
                WolfDebugLogger.log(wolf, "Tame", "Assigned initial personality: " + personality.name());
            }

            WolfPersonality personality = ext.betterdogs$getPersonality();
            WolfStatManager.applyPersonalityStats(wolf, personality);
            // statsApplied flag will be set/re-applied in WolfMixin's tick handler if needed, 
            // but we also apply immediately here for responsiveness.

            betterdogs$playTameParticles(personality);
        }
    }

    @Override
    public void spawnTamingParticles(boolean success) {
        if (success) {
            for (int i = 0; i < 3; i++) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5,
                        this.getRandomZ(1.0), d, e, f);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.SMOKE, this.getRandomX(1.0), this.getRandomY() + 0.5,
                        this.getRandomZ(1.0), d, e, f);
            }
        }
    }

    @Unique
    private void betterdogs$playTameParticles(WolfPersonality personality) {
        WolfParticleHandler.playTameParticles((Wolf) (Object) this, personality);
    }
}
