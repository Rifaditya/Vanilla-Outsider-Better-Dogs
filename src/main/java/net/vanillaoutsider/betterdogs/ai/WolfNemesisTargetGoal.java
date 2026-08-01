// SPDX-License-Identifier: GPL-3.0-or-later
package net.vanillaoutsider.betterdogs.ai;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.phys.AABB;
import net.vanillaoutsider.betterdogs.WolfPersistentData;
import net.dasik.social.api.gamerule.DynamicGameRuleManager;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

public class WolfNemesisTargetGoal extends TargetGoal {
    private final Wolf wolf;
    private LivingEntity target;

    public WolfNemesisTargetGoal(Wolf wolf) {
        super(wolf, false);
        this.wolf = wolf;
    }

    @Override
    public boolean canUse() {
        if (!wolf.isTame()) return false;
        
        if (!DynamicGameRuleManager.getBoolean(wolf.level(), BetterDogsGameRules.BD_NEMESIS_SYSTEM)) return false;

        String nemesisType = WolfPersistentData.getPersistedNemesisType(wolf);
        if (nemesisType.isEmpty()) return false;
        
        long expiry = WolfPersistentData.getPersistedNemesisExpiry(wolf);
        if (wolf.level().getGameTime() > expiry) return false;

        this.findTarget(nemesisType);
        return this.target != null;
    }

    private void findTarget(String nemesisType) {
        AABB aabb = this.wolf.getBoundingBox().inflate(this.getFollowDistance());
        List<LivingEntity> entities = this.wolf.level().getEntitiesOfClass(LivingEntity.class, aabb,
                e -> e.isAlive() && BuiltInRegistries.ENTITY_TYPE.getKey(e.getType()).toString().equals(nemesisType));
        
        if (!entities.isEmpty()) {
            this.target = entities.get(0); // Take the first matched one
        } else {
            this.target = null;
        }
    }

    @Override
    public void start() {
        this.wolf.setTarget(this.target);
        this.wolf.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 1200, 0, true, false));
        this.wolf.addEffect(new MobEffectInstance(MobEffects.SPEED, 1200, 0, true, false));
        super.start();
    }

    @Override
    public void stop() {
        this.wolf.removeEffect(MobEffects.STRENGTH);
        this.wolf.removeEffect(MobEffects.SPEED);
        super.stop();
    }
}
