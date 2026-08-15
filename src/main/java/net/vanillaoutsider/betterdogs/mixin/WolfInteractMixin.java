// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.vanillaoutsider.betterdogs.util.DogFoodHelper;
import net.vanillaoutsider.betterdogs.util.DogTreatHelper;
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
        Level level = wolf.getCommandSenderWorld();
        if (level == null) {
            return;
        }

        ItemStack itemInHand = player.getItemInHand(hand);

        if (net.vanillaoutsider.betterdogs.util.WolfCureHelper.canCure(wolf, itemInHand)) {
            InteractionResult result = net.vanillaoutsider.betterdogs.util.WolfCureHelper.tryCureInbredWolf(wolf, player, hand);
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
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide));
                return;
            }

            DogTreatHelper.tryRollFavoriteTreat(wolf, itemInHand);
        }
    }
}
