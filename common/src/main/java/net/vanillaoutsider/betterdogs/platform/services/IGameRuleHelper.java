// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform.services;

import net.minecraft.world.level.GameRules;

public interface IGameRuleHelper {
    GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, GameRules.Category category, boolean defaultValue);

    GameRules.Key<GameRules.IntegerValue> registerInt(String name, GameRules.Category category, int defaultValue);

    GameRules.Key<GameRules.IntegerValue> registerInt(String name, GameRules.Category category, int defaultValue, int min, int max);
}
