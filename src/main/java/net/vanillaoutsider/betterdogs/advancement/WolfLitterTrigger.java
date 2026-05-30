// Verified against: TameAnimalTrigger.java (26.2+)
package net.vanillaoutsider.betterdogs.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
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
        Optional<Integer> minSize,
        Optional<Integer> size
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            Codec.INT.optionalFieldOf("min_size").forGetter(TriggerInstance::minSize),
            Codec.INT.optionalFieldOf("size").forGetter(TriggerInstance::size)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(int litterSize) {
            if (this.minSize.isPresent() && litterSize < this.minSize.get()) {
                return false;
            }
            if (this.size.isPresent() && litterSize != this.size.get()) {
                return false;
            }
            return true;
        }
    }
}
