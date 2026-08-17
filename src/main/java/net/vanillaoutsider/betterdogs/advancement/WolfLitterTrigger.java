// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class WolfLitterTrigger extends SimpleCriterionTrigger<WolfLitterTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, int puppyCount) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(puppyCount));
    }

    public record TriggerInstance(
        Optional<Holder<LootItemCondition>> player,
        Optional<Integer> minPuppies
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LootItemCondition.CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            Codec.INT.optionalFieldOf("min_puppies").forGetter(TriggerInstance::minPuppies)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(int puppyCount) {
            if (this.minPuppies.isEmpty()) {
                return true;
            }
            return puppyCount >= this.minPuppies.get();
        }
    }
}
