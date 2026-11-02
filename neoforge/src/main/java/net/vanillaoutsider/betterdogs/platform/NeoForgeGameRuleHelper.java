// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs.platform;

import net.minecraft.world.level.GameRules;
import net.vanillaoutsider.betterdogs.platform.services.IGameRuleHelper;

public class NeoForgeGameRuleHelper implements IGameRuleHelper {
    @Override
    public GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, GameRules.Category category, boolean defaultValue) {
        return GameRules.register(name, category, GameRules.BooleanValue.create(defaultValue));
    }

    @Override
    public GameRules.Key<GameRules.IntegerValue> registerInt(String name, GameRules.Category category, int defaultValue) {
        return GameRules.register(name, category, GameRules.IntegerValue.create(defaultValue));
    }

    @Override
    public GameRules.Key<GameRules.IntegerValue> registerInt(String name, GameRules.Category category, int defaultValue, int min, int max) {
        return GameRules.register(name, category, GameRules.IntegerValue.create(defaultValue, (server, value) -> {
            int current = value.get();
            if (current < min) value.set(min, server);
            else if (current > max) value.set(max, server);
        }));
    }
}
