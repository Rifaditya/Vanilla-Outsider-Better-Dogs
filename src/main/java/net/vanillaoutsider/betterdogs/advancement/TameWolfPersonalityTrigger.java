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
import net.vanillaoutsider.betterdogs.WolfPersonality;

public class TameWolfPersonalityTrigger extends SimpleCriterionTrigger<TameWolfPersonalityTrigger.TriggerInstance> {
    public static final ResourceLocation ID = new ResourceLocation("betterdogs", "tame_wolf_personality");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public TriggerInstance createInstance(JsonObject json, ContextAwarePredicate playerPredicate, DeserializationContext context) {
        String personality = json.has("personality") ? json.get("personality").getAsString() : null;
        return new TriggerInstance(playerPredicate, personality);
    }

    public void trigger(ServerPlayer player, WolfPersonality personality) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(personality));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final String personality;

        public TriggerInstance(ContextAwarePredicate playerPredicate, String personality) {
            super(ID, playerPredicate);
            this.personality = personality;
        }

        public boolean matches(WolfPersonality personality) {
            if (this.personality == null) {
                return true;
            }
            if (personality == null) {
                return false;
            }
            return this.personality.equalsIgnoreCase(personality.name());
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            if (this.personality != null) {
                json.addProperty("personality", this.personality);
            }
            return json;
        }
    }
}
