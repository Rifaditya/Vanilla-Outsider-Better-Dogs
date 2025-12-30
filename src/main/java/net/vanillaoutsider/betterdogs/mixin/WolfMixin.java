package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.vanillaoutsider.betterdogs.WolfExtensions;
import net.vanillaoutsider.betterdogs.WolfPersonality;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Wolf.class)
public abstract class WolfMixin extends TamableAnimal implements WolfExtensions {

    // Dummy constructor required for extending TamableAnimal
    protected WolfMixin() {
        super(null, null);
    }

    @Unique
    private byte betterdogs$personality = -1; // -1 = not assigned yet

    @Unique
    private int betterdogs$lastDamageTime = 0;

    @Unique
    private int betterdogs$healTimer = 0;

    // ========== WolfExtensions Implementation ==========

    @Override
    public WolfPersonality betterdogs$getPersonality() {
        return WolfPersonality.Companion.fromId(betterdogs$personality);
    }

    @Override
    public void betterdogs$setPersonality(WolfPersonality personality) {
        this.betterdogs$personality = (byte) personality.getId();
    }

    @Override
    public boolean betterdogs$hasPersonality() {
        return betterdogs$personality >= 0;
    }

    @Override
    public int betterdogs$getLastDamageTime() {
        return betterdogs$lastDamageTime;
    }

    @Override
    public void betterdogs$setLastDamageTime(int time) {
        this.betterdogs$lastDamageTime = time;
    }

    // ========== NBT Save/Load ==========

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void betterdogs$saveData(CompoundTag tag, CallbackInfo ci) {
        tag.putByte("BetterDogsPersonality", betterdogs$personality);
        tag.putInt("BetterDogsLastDamage", betterdogs$lastDamageTime);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void betterdogs$loadData(CompoundTag tag, CallbackInfo ci) {
        // 1.21.11 API: getByte returns Optional<Byte>
        tag.getByte("BetterDogsPersonality").ifPresent(value -> {
            betterdogs$personality = value;
        });
        tag.getInt("BetterDogsLastDamage").ifPresent(value -> {
            betterdogs$lastDamageTime = value;
        });
    }

    // ========== On Tame - Assign Personality ==========
    // Override setTame from TamableAnimal

    @Override
    public void setTame(boolean tamed, boolean applyBoost) {
        super.setTame(tamed, applyBoost);

        if (!tamed)
            return; // Only process when becoming tamed

        // Only assign if not already assigned
        if (betterdogs$personality < 0) {
            WolfPersonality personality = WolfPersonality.Companion.random();
            betterdogs$personality = (byte) personality.getId();

            // Spawn particles based on personality
            if (this.level() instanceof ServerLevel serverLevel) {
                switch (personality) {
                    case AGGRESSIVE -> {
                        // Angry villager particles
                        for (int i = 0; i < 7; i++) {
                            serverLevel.sendParticles(
                                    ParticleTypes.ANGRY_VILLAGER,
                                    this.getX() + this.getRandom().nextGaussian() * 0.5,
                                    this.getY() + 0.5 + this.getRandom().nextDouble() * 0.5,
                                    this.getZ() + this.getRandom().nextGaussian() * 0.5,
                                    1, 0, 0, 0, 0);
                        }
                    }
                    case PACIFIST -> {
                        // Heart particles
                        for (int i = 0; i < 7; i++) {
                            serverLevel.sendParticles(
                                    ParticleTypes.HEART,
                                    this.getX() + this.getRandom().nextGaussian() * 0.5,
                                    this.getY() + 0.5 + this.getRandom().nextDouble() * 0.5,
                                    this.getZ() + this.getRandom().nextGaussian() * 0.5,
                                    1, 0, 0, 0, 0);
                        }
                    }
                    case NORMAL -> {
                        // Happy villager particles
                        for (int i = 0; i < 7; i++) {
                            serverLevel.sendParticles(
                                    ParticleTypes.HAPPY_VILLAGER,
                                    this.getX() + this.getRandom().nextGaussian() * 0.5,
                                    this.getY() + 0.5 + this.getRandom().nextDouble() * 0.5,
                                    this.getZ() + this.getRandom().nextGaussian() * 0.5,
                                    1, 0, 0, 0, 0);
                        }
                    }
                }
            }
        }
    }

    // ========== Passive Healing ==========

    @Inject(method = "tick", at = @At("TAIL"))
    private void betterdogs$passiveHeal(CallbackInfo ci) {
        // Only for tamed wolves
        if (!this.isTame())
            return;

        int currentTick = this.tickCount;

        // Only heal if not in combat (60 ticks = 3 seconds since last damage)
        if (currentTick - betterdogs$lastDamageTime > 60 && this.getHealth() < this.getMaxHealth()) {
            betterdogs$healTimer++;
            if (betterdogs$healTimer >= 1200) { // 60 seconds (20 ticks * 60)
                this.heal(1.0f);
                betterdogs$healTimer = 0;
            }
        } else {
            betterdogs$healTimer = 0;
        }
    }

    // ========== Track Damage Time + Friendly Fire Protection ==========

    @Override
    protected void actuallyHurt(ServerLevel level, DamageSource source, float amount) {
        betterdogs$lastDamageTime = this.tickCount;

        // Friendly fire protection - only for tamed wolves
        if (this.isTame() && source.getEntity() instanceof Player player) {
            if (this.isOwnedBy(player)) {
                // Allow damage if sneaking (emergency kill)
                if (!player.isShiftKeyDown()) {
                    return; // Cancel damage
                }
            }
        }

        super.actuallyHurt(level, source, amount);
    }
}
