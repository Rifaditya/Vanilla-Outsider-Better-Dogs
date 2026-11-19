// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 1.20.1
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
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

        GameRules.Key<GameRules.IntegerValue> previousKey = BetterDogsGameRules.BD_AGGRO_FOLLOW_START;
        GameRules.Key<GameRules.IntegerValue> activeKey = previousKey != null
                ? previousKey
                : new GameRules.Key<>("bd_aggro_follow_start", GameRules.Category.MISC);

        try {
            BetterDogsGameRules.BD_AGGRO_FOLLOW_START = activeKey;

            final int[] aggroFollowStart = { 25 };
            GameRules rules = new GameRules() {
                @Override
                public int getInt(GameRules.Key<GameRules.IntegerValue> key) {
                    if (activeKey.equals(key)) {
                        return aggroFollowStart[0];
                    }
                    return super.getInt(key);
                }
            };
            CatchupStubLevel level = createStubLevel(rules);

            CatchupStubWolf normalWolf = createStubWolf(WolfPersonality.NORMAL, level);
            Assertions.assertEquals(40.0D, WolfCatchupHelper.getCatchupTeleportDistance(normalWolf, false), 1e-4D);
            Assertions.assertEquals(32.0D, WolfCatchupHelper.getCatchupTeleportDistance(normalWolf, true), 1e-4D);

            CatchupStubWolf pacifistWolf = createStubWolf(WolfPersonality.PACIFIST, level);
            Assertions.assertEquals(40.0D, WolfCatchupHelper.getCatchupTeleportDistance(pacifistWolf, false), 1e-4D);
            Assertions.assertEquals(32.0D, WolfCatchupHelper.getCatchupTeleportDistance(pacifistWolf, true), 1e-4D);

            CatchupStubWolf aggressiveWolf = createStubWolf(WolfPersonality.AGGRESSIVE, level);
            Assertions.assertEquals(41.0D, WolfCatchupHelper.getCatchupTeleportDistance(aggressiveWolf, false), 1e-4D);
            Assertions.assertEquals(41.0D, WolfCatchupHelper.getCatchupTeleportDistance(aggressiveWolf, true), 1e-4D);

            aggroFollowStart[0] = 60;
            Assertions.assertEquals(76.0D, WolfCatchupHelper.getCatchupTeleportDistance(aggressiveWolf, false), 1e-4D);
            Assertions.assertEquals(76.0D, WolfCatchupHelper.getCatchupTeleportDistance(aggressiveWolf, true), 1e-4D);
        } finally {
            BetterDogsGameRules.BD_AGGRO_FOLLOW_START = previousKey;
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

    private static final class CatchupStubLevel extends ServerLevel {
        GameRules gameRules;

        private CatchupStubLevel() {
            super(null, null, null, null, null, null, null, false, 0L, null, false, null);
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
        @Override public Level getCommandSenderWorld() { return this.level; }
        @Override public @Nullable LivingEntity getOwner() { return null; }
        @Override public @Nullable UUID getOwnerUUID() { return this.ownerUuid; }
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
