// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import net.vanillaoutsider.betterdogs.platform.services.IGameRuleHelper;

public class FabricGameRuleHelper implements IGameRuleHelper {
    @Override
    public GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, GameRules.Category category, boolean defaultValue) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
    }

    @Override
    public GameRules.Key<GameRules.IntegerValue> registerInt(String name, GameRules.Category category, int defaultValue) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createIntRule(defaultValue));
    }

    @Override
    public GameRules.Key<GameRules.IntegerValue> registerInt(String name, GameRules.Category category, int defaultValue, int min, int max) {
        return GameRuleRegistry.register(name, category, GameRuleFactory.createIntRule(defaultValue, min, max));
    }
}
