// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class WolfLitterTrigger extends SimpleCriterionTrigger<WolfLitterTrigger.TriggerInstance> {
    public static final ResourceLocation ID = new ResourceLocation("betterdogs", "wolf_litter");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject json, ContextAwarePredicate playerPredicate, DeserializationContext context) {
        Integer minSize = json.has("min_size") ? json.get("min_size").getAsInt() : null;
        Integer size = json.has("size") ? json.get("size").getAsInt() : null;
        return new TriggerInstance(playerPredicate, minSize, size);
    }

    public void trigger(ServerPlayer player, int litterSize) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(litterSize));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final Integer minSize;
        private final Integer size;

        public TriggerInstance(ContextAwarePredicate playerPredicate, Integer minSize, Integer size) {
            super(ID, playerPredicate);
            this.minSize = minSize;
            this.size = size;
        }

        public boolean matches(int litterSize) {
            if (this.minSize != null && litterSize < this.minSize) {
                return false;
            }
            if (this.size != null && litterSize != this.size) {
                return false;
            }
            return true;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            if (this.minSize != null) {
                json.addProperty("min_size", this.minSize);
            }
            if (this.size != null) {
                json.addProperty("size", this.size);
            }
            return json;
        }
    }
}
