// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.21.11
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleMap;
import net.minecraft.world.level.gamerules.GameRules;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfCatchupHelper;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

public class FastTravelCatchupTest {

    private static final Unsafe unsafe = initUnsafe();

    private static Unsafe initUnsafe() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Unsafe", e);
        }
    }

    @BeforeAll
    static void initBootstrap() {
        try {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
        } catch (Throwable ignored) {
        }
        try {
            if (BetterDogsGameRules.BD_AGGRO_FOLLOW_START == null) {
                BetterDogsGameRules.register();
            }
        } catch (Throwable ignored) {
        }
    }

    @Test
    @DisplayName("Verify Fast Travel Speed Scaling Null Safety")
    public void testCatchupSpeedNullSafety() {
        double speed = WolfCatchupHelper.calculateCatchupSpeed(null, null, 1.25);
        Assertions.assertEquals(1.25, speed);
    }

    @Test
    @DisplayName("Verify Interdimensional Teleport Sync Null Safety")
    public void testSyncDimensionNullSafety() {
        Assertions.assertDoesNotThrow(() -> WolfCatchupHelper.syncOwnerDimensionTeleport(null, null, null, null));
    }

    @Test
    @DisplayName("BL-BD-046: Verify WolfCatchupHelper.getCatchupTeleportDistance parity across personalities and custom GameRules")
    public void testCatchupTeleportDistanceByPersonalityAndGameRule() {
        Assertions.assertEquals(40.0D, WolfCatchupHelper.getCatchupTeleportDistance(null, false), 1e-4D);
        Assertions.assertEquals(32.0D, WolfCatchupHelper.getCatchupTeleportDistance(null, true), 1e-4D);

        CatchupStubLevel defaultLevel = createStubLevel(createFollowGameRules(25));

        CatchupStubWolf normalWolf = createStubWolf(WolfPersonality.NORMAL, defaultLevel);
        Assertions.assertEquals(40.0D, WolfCatchupHelper.getCatchupTeleportDistance(normalWolf, false), 1e-4D);
        Assertions.assertEquals(32.0D, WolfCatchupHelper.getCatchupTeleportDistance(normalWolf, true), 1e-4D);

        CatchupStubWolf pacifistWolf = createStubWolf(WolfPersonality.PACIFIST, defaultLevel);
        Assertions.assertEquals(40.0D, WolfCatchupHelper.getCatchupTeleportDistance(pacifistWolf, false), 1e-4D);
        Assertions.assertEquals(32.0D, WolfCatchupHelper.getCatchupTeleportDistance(pacifistWolf, true), 1e-4D);

        CatchupStubWolf aggressiveWolf = createStubWolf(WolfPersonality.AGGRESSIVE, defaultLevel);
        Assertions.assertEquals(41.0D, WolfCatchupHelper.getCatchupTeleportDistance(aggressiveWolf, false), 1e-4D);
        Assertions.assertEquals(41.0D, WolfCatchupHelper.getCatchupTeleportDistance(aggressiveWolf, true), 1e-4D);

        CatchupStubLevel highLevel = createStubLevel(createFollowGameRules(60));
        aggressiveWolf.level = highLevel;
        Assertions.assertEquals(76.0D, WolfCatchupHelper.getCatchupTeleportDistance(aggressiveWolf, false), 1e-4D);
        Assertions.assertEquals(76.0D, WolfCatchupHelper.getCatchupTeleportDistance(aggressiveWolf, true), 1e-4D);
    }

    private static GameRules createFollowGameRules(int aggroFollowStart) {
        GameRuleMap map = GameRuleMap.of();
        if (BetterDogsGameRules.BD_AGGRO_FOLLOW_START != null) {
            map.set(BetterDogsGameRules.BD_AGGRO_FOLLOW_START, aggroFollowStart);
        }
        if (BetterDogsGameRules.BD_PACI_FOLLOW_START != null) {
            map.set(BetterDogsGameRules.BD_PACI_FOLLOW_START, 5);
        }
        if (BetterDogsGameRules.BD_NORMAL_FOLLOW_START != null) {
            map.set(BetterDogsGameRules.BD_NORMAL_FOLLOW_START, 10);
        }
        if (BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER != null) {
            map.set(BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER, 100);
        }
        if (BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX != null) {
            map.set(BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX, 50);
        }
        try {
            CatchupStubGameRules rules = (CatchupStubGameRules) unsafe.allocateInstance(CatchupStubGameRules.class);
            rules.map = map;
            return rules;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate CatchupStubGameRules", e);
        }
    }

    private static CatchupStubLevel createStubLevel(GameRules rules) {
        try {
            CatchupStubLevel level = (CatchupStubLevel) unsafe.allocateInstance(CatchupStubLevel.class);
            level.gameRules = rules;
            return level;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate CatchupStubLevel", e);
        }
    }

    private static CatchupStubWolf createStubWolf(WolfPersonality personality, Level level) {
        try {
            CatchupStubWolf wolf = (CatchupStubWolf) unsafe.allocateInstance(CatchupStubWolf.class);
            wolf.uuid = UUID.randomUUID();
            wolf.ownerUuid = UUID.randomUUID();
            wolf.personality = personality;
            wolf.level = level;
            return wolf;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate CatchupStubWolf", e);
        }
    }

    private static final class CatchupStubGameRules extends GameRules {
        GameRuleMap map;

        private CatchupStubGameRules() {
            super(FeatureFlags.DEFAULT_FLAGS);
        }

        @Override
        public <T> T get(GameRule<T> rule) {
            T val = this.map != null ? this.map.get(rule) : null;
            if (val == null) {
                throw new IllegalArgumentException("Rule not set in CatchupStubGameRules: " + rule);
            }
            return val;
        }
    }

    private static final class CatchupStubLevel extends ServerLevel {
        GameRules gameRules;

        private CatchupStubLevel() {
            super(null, null, null, null, null, null, false, 0L, null, false, null);
        }

        @Override
        public GameRules getGameRules() {
            return this.gameRules;
        }

        @Override
        public boolean isClientSide() {
            return false;
        }
    }

    private static final class CatchupStubWolf extends Wolf implements WolfExtensions {
        UUID uuid;
        UUID ownerUuid;
        WolfPersonality personality;
        Level level;

        private CatchupStubWolf() {
            super(null, null);
        }

        @Override public UUID getUUID() { return this.uuid; }
        @Override public EntityType<?> getType() { return EntityType.WOLF; }
        @Override public Level level() { return this.level; }
        @Override public @Nullable LivingEntity getOwner() { return null; }
        @Override public @Nullable EntityReference<LivingEntity> getOwnerReference() { return EntityReference.of(this.ownerUuid); }
        @Override public CanineArchetype betterdogs$getCanineArchetype() { return CanineArchetype.GUARDIAN; }
        @Override public WolfPersonality betterdogs$getPersonality() { return this.personality; }
        @Override public void betterdogs$setPersonality(WolfPersonality p) { this.personality = p; }
        @Override public boolean betterdogs$hasPersonality() { return this.personality != null; }
        @Override public float betterdogs$getSocialScale() { return 1.0f; }
        @Override public void betterdogs$setSocialScale(float scale) {}
        @Override public long betterdogs$getDnaSeed() { return 0L; }
        @Override public String betterdogs$getFavoriteTreat() { return ""; }
        @Override public void betterdogs$setFavoriteTreat(String treat) {}
        @Override public long betterdogs$getSoothedTime() { return 0L; }
        @Override public void betterdogs$setSoothedTime(long time) {}
        @Override public @Nullable BlockPos betterdogs$getSoundLocationTarget() { return null; }
        @Override public void betterdogs$setSoundLocationTarget(@Nullable BlockPos pos) {}
        @Override public int betterdogs$getPassiveOverrideTicks() { return 0; }
        @Override public void betterdogs$setPassiveOverrideTicks(int ticks) {}
        @Override public String betterdogs$getNemesisEntityType() { return ""; }
        @Override public void betterdogs$setNemesisEntityType(String type) {}
        @Override public long betterdogs$getNemesisExpiryTime() { return 0L; }
        @Override public void betterdogs$setNemesisExpiryTime(long time) {}
        @Override public void betterdogs$setCollarColor(DyeColor color) {}
        @Override public @Nullable UUID betterdogs$getParentUUID1() { return null; }
        @Override public void betterdogs$setParentUUID1(@Nullable UUID uuid) {}
        @Override public @Nullable UUID betterdogs$getParentUUID2() { return null; }
        @Override public void betterdogs$setParentUUID2(@Nullable UUID uuid) {}
        @Override public boolean betterdogs$isInbred() { return false; }
        @Override public void betterdogs$setInbred(boolean inbred) {}
        @Override public boolean betterdogs$isGuarding() { return false; }
        @Override public void betterdogs$setGuarding(boolean guarding) {}
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return null; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) {}
        @Override public boolean betterdogs$isUpForAdoption() { return false; }
        @Override public void betterdogs$setUpForAdoption(boolean adoption) {}
        @Override public long betterdogs$getLastGiftDay() { return -1L; }
        @Override public void betterdogs$setLastGiftDay(long day) {}
        @Override public int betterdogs$getZoomiesTicks() { return 0; }
        @Override public void betterdogs$setZoomiesTicks(int ticks) {}
        @Override public boolean betterdogs$hasFetchedStick() { return false; }
        @Override public void betterdogs$setHasFetchedStick(boolean fetched) {}
        @Override public int betterdogs$getCalmTicks() { return 0; }
        @Override public void betterdogs$setCalmTicks(int ticks) {}
        @Override public int betterdogs$getHowlingTicks() { return 0; }
        @Override public void betterdogs$setHowlingTicks(int ticks) {}
        @Override public @Nullable Holder<WolfVariant> betterdogs$getVariant() { return null; }
        @Override public void betterdogs$setVariant(Holder<WolfVariant> variant) {}
        @Override public @Nullable Holder<WolfSoundVariant> betterdogs$getSoundVariant() { return null; }
        @Override public int betterdogs$getFeedCount() { return 0; }
        @Override public void betterdogs$setFeedCount(int count) {}
        @Override public @Nullable LivingEntity betterdogs$getRetaliationTarget() { return null; }
        @Override public void betterdogs$setRetaliationTarget(@Nullable LivingEntity target, int ticks) {}
        @Override public int betterdogs$getRetaliationTicks() { return 0; }
        @Override public String betterdogs$getBloodFeudTarget() { return ""; }
        @Override public void betterdogs$setBloodFeudTarget(String targetUuid) {}
        @Override public boolean betterdogs$hasBloodFeud() { return false; }
        @Override public int betterdogs$getPlayFightCooldown() { return 0; }
        @Override public void betterdogs$setPlayFightCooldown(int ticks) {}
        @Override public @Nullable UUID betterdogs$getLeaderUUID() { return null; }
        @Override public void betterdogs$setLeaderUUID(@Nullable UUID uuid) {}
        @Override public boolean betterdogs$isPackLeader() { return false; }
        @Override public void betterdogs$setPackLeader(boolean isLeader) {}
        @Override public int betterdogs$getWanderlustTicks() { return 0; }
        @Override public void betterdogs$setWanderlustTicks(int ticks) {}
    }
}
