// SPDX-License-Identifier: GPL-3.0-or-later
/*
 * Vanilla Outsider: Better Dogs
 * Verified against: BreedGoal.java (26.2+)
 */
package net.vanillaoutsider.betterdogs.mixin;

import java.util.List;
import net.dasik.social.api.genetics.EntityGenetics;
import net.dasik.social.api.genetics.GeneticsEngine;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BreedGoal.class)
public abstract class BreedGoalMixin {

    @Shadow @Final protected Animal animal;
    @Shadow @Final private Class<? extends Animal> partnerClass;
    @Shadow @Final protected ServerLevel level;
    @Shadow @Final private static TargetingConditions PARTNER_TARGETING;

    /**
     * Intercepts partner selection in BreedGoal to prioritize unrelated wolves over related ones.
     * If no unrelated wolves are available, falls back to related ones (incest) to prevent softlocks.
     */
    @Inject(method = "getFreePartner", at = @At("HEAD"), cancellable = true)
    private void betterdogs$getFreePartnerUnrelatedFirst(CallbackInfoReturnable<Animal> cir) {
        if (this.animal instanceof Wolf wolf) {
            List<? extends Animal> animals = this.level.getNearbyEntities(
                this.partnerClass,
                PARTNER_TARGETING,
                this.animal,
                this.animal.getBoundingBox().inflate(8.0)
            );

            double distUnrelated = Double.MAX_VALUE;
            Animal partnerUnrelated = null;

            double distRelated = Double.MAX_VALUE;
            Animal partnerRelated = null;

            EntityGenetics d1 = GeneticsEngine.getGenetics(wolf);

            for (Animal candidate : animals) {
                if (!wolf.canMate(candidate) || candidate.isPanicking()) {
                    continue;
                }

                EntityGenetics d2 = GeneticsEngine.getGenetics(candidate);
                boolean related = GeneticsEngine.checkInbreeding(wolf, candidate, d1, d2);

                double distance = wolf.distanceToSqr(candidate);
                if (related) {
                    if (distance < distRelated) {
                        partnerRelated = candidate;
                        distRelated = distance;
                    }
                } else {
                    if (distance < distUnrelated) {
                        partnerUnrelated = candidate;
                        distUnrelated = distance;
                    }
                }
            }

            // Return unrelated partner if found; otherwise, fall back to related partner
            if (partnerUnrelated != null) {
                cir.setReturnValue(partnerUnrelated);
            } else if (partnerRelated != null) {
                cir.setReturnValue(partnerRelated);
            } else {
                cir.setReturnValue(null);
            }
        }
    }
}
