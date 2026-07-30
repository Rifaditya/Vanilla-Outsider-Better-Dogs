// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: BredAnimalsTrigger.java (26.3+)
package net.vanillaoutsider.betterdogs.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class GuardWolfPersonalityTrigger extends SimpleCriterionTrigger<GuardWolfPersonalityTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, WolfPersonality personality) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(personality));
    }

    public record TriggerInstance(
        Optional<Holder<LootItemCondition>> player,
        Optional<String> personality
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
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
