// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.util.DogFoodHelper;
import net.vanillaoutsider.betterdogs.util.DogTreatHelper;
import net.vanillaoutsider.betterdogs.util.WolfAdvancementHelper;
import net.vanillaoutsider.betterdogs.util.WolfPettingHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Dedicated single-purpose Mixin for intercepting player interactions (petting, food refusal, treat affinity).
 */
@Mixin(Wolf.class)
public abstract class WolfInteractMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void betterdogs$onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Wolf wolf = (Wolf) (Object) this;
        Level level = wolf.level();
        if (level == null) {
            return;
        }

        ItemStack itemInHand = player.getItemInHand(hand);

        if (wolf.isTame() && wolf.isOwnedBy(player) && player.isSecondaryUseActive() && net.vanillaoutsider.betterdogs.util.DogCommandManager.isCommandItem(itemInHand) && hand == InteractionHand.MAIN_HAND) {
            if (!level.isClientSide()) {
                if (wolf.isPassenger()) {
                    net.minecraft.world.entity.Entity vehicle = wolf.getVehicle();
                    wolf.stopRiding();
                    if (vehicle != null && vehicle.getTags().contains("betterdogs:seat")) {
                        vehicle.discard();
                    }
                    net.vanillaoutsider.betterdogs.util.DogCommandManager.clearVehicleTarget(wolf.getUUID());
                    net.vanillaoutsider.betterdogs.util.DogCommandManager.clearSelection(player.getUUID());
                    wolf.playSound(net.minecraft.sounds.SoundEvents.WOLF_AMBIENT, 1.0f, 1.0f);
                    net.vanillaoutsider.betterdogs.util.WolfFeedbackHelper.sendFeedback(player, level, net.minecraft.network.chat.Component.translatable("text.betterdogs.dog_dismounted", wolf.getName()));
                } else {
                    net.vanillaoutsider.betterdogs.util.DogCommandManager.selectDog(player.getUUID(), wolf.getUUID());
                    wolf.playSound(net.minecraft.sounds.SoundEvents.WOLF_AMBIENT, 1.0f, 1.2f);
                    net.vanillaoutsider.betterdogs.util.WolfFeedbackHelper.sendFeedback(player, level, net.minecraft.network.chat.Component.translatable("text.betterdogs.dog_selected", wolf.getName()));
                    if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.NOTE, wolf.getX(), wolf.getY() + 0.5, wolf.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
                    }
                }
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
            return;
        }

        if (net.vanillaoutsider.betterdogs.util.WolfCureHelper.canCure(wolf, itemInHand)) {
            InteractionResult result = net.vanillaoutsider.betterdogs.util.WolfCureHelper.tryCureInbredWolf(wolf, player, hand);
            if (result.consumesAction()) {
                cir.setReturnValue(result);
                return;
            }
        }

        InteractionResult adoptionResult = net.vanillaoutsider.betterdogs.util.WolfAdoptionHelper.tryHandleAdoption(wolf, player, itemInHand);
        if (adoptionResult.consumesAction()) {
            cir.setReturnValue(adoptionResult);
            return;
        }

        if (net.vanillaoutsider.betterdogs.util.WolfGuardHelper.canToggleGuard(wolf, player, itemInHand)) {
            InteractionResult result = net.vanillaoutsider.betterdogs.util.WolfGuardHelper.toggleGuardMode(wolf, player);
            if (result.consumesAction()) {
                cir.setReturnValue(result);
                return;
            }
        }

        if (WolfPettingHelper.canPet(wolf, player, hand, itemInHand)) {
            InteractionResult result = WolfPettingHelper.petWolf(wolf, player);
            cir.setReturnValue(result);
            return;
        }

        if (!itemInHand.isEmpty() && DogFoodHelper.isEdibleDogFood(level, itemInHand)) {
            if (DogTreatHelper.shouldRefuseFood(wolf, itemInHand)) {
                DogTreatHelper.performRefusal(wolf);
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
                return;
            }

            if (DogTreatHelper.isFavoriteTreat(wolf, itemInHand)) {
                if (wolf instanceof WolfExtensions ext) {
                    ext.betterdogs$setZoomiesTicks(120);
                }
                WolfAdvancementHelper.grantAdvancement(player, "favorite_treat");
                WolfAdvancementHelper.grantAdvancement(player, "zoomies");
            }

            DogTreatHelper.tryRollFavoriteTreat(wolf, itemInHand, player);
        }
    }
}
