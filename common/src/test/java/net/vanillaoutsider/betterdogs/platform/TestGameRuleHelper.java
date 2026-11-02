// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import net.minecraft.world.level.GameRules;
import net.vanillaoutsider.betterdogs.platform.services.IGameRuleHelper;

public class TestGameRuleHelper implements IGameRuleHelper {
    @Override
    public GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, GameRules.Category category, boolean defaultValue) {
        return null;
    }

    @Override
    public GameRules.Key<GameRules.IntegerValue> registerInt(String name, GameRules.Category category, int defaultValue) {
        return null;
    }

    @Override
    public GameRules.Key<GameRules.IntegerValue> registerInt(String name, GameRules.Category category, int defaultValue, int min, int max) {
        return null;
    }
}
