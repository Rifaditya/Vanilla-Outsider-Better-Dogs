// Verified against: TameAnimalTrigger.java (26.2+)
// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class TameWolfPersonalityTrigger extends SimpleCriterionTrigger<TameWolfPersonalityTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, WolfPersonality personality) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(personality));
    }

    public record TriggerInstance(
        Optional<ContextAwarePredicate> player,
        Optional<String> personality
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            Codec.STRING.optionalFieldOf("personality").forGetter(TriggerInstance::personality)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(WolfPersonality personality) {
            if (this.personality.isEmpty()) {
                return true;
            }
            return this.personality.get().equalsIgnoreCase(personality.name());
        }
    }
}
