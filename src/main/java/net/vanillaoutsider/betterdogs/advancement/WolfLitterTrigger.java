// Verified against: BredAnimalsTrigger.java (26.1.2+)
package net.vanillaoutsider.betterdogs.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class WolfLitterTrigger extends SimpleCriterionTrigger<WolfLitterTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, int litterSize) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(litterSize));
    }

    public record TriggerInstance(
        Optional<ContextAwarePredicate> player,
        Optional<Integer> minLitterSize
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            Codec.INT.optionalFieldOf("min_litter_size").forGetter(TriggerInstance::minLitterSize)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(int litterSize) {
            if (this.minLitterSize.isEmpty()) {
                return true;
            }
            return litterSize >= this.minLitterSize.get();
        }
    }
}
