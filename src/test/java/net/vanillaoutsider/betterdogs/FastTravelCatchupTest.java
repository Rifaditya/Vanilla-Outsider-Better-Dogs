// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.3
package net.vanillaoutsider.betterdogs;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import net.dasik.social.core.EntitySocialScheduler;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.vanillaoutsider.betterdogs.personality.CanineArchetype;
import net.vanillaoutsider.betterdogs.registry.BetterDogsGameRules;
import net.vanillaoutsider.betterdogs.util.WolfCatchupHelper;
import org.jspecify.annotations.Nullable;
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
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        if (BetterDogsGameRules.BD_AGGRO_FOLLOW_START == null) {
            BetterDogsGameRules.register();
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
        Assertions.assertDoesNotThrow(() -> WolfCatchupHelper.checkAndPerformCatchUp(null));
    }

    @Test
    @DisplayName("BL-BD-046: Verify WolfCatchupHelper.getCatchupTeleportDistance parity across personalities and custom GameRules")
    public void testCatchupTeleportDistanceByPersonalityAndGameRule() {
        // Null wolf falls back to base follow/flying ceilings
        Assertions.assertEquals(40.0D, WolfCatchupHelper.getCatchupTeleportDistance(null, false), 1e-4D);
        Assertions.assertEquals(32.0D, WolfCatchupHelper.getCatchupTeleportDistance(null, true), 1e-4D);

        GameRules rules = new GameRules(List.of(
                BetterDogsGameRules.BD_AGGRO_FOLLOW_START,
                BetterDogsGameRules.BD_PACI_FOLLOW_START,
                BetterDogsGameRules.BD_NORMAL_FOLLOW_START,
                BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MULTIPLIER,
                BetterDogsGameRules.BD_TAMED_PACK_SPREAD_MAX
        ));
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

        rules.set(BetterDogsGameRules.BD_AGGRO_FOLLOW_START, 60, null);
        Assertions.assertEquals(76.0D, WolfCatchupHelper.getCatchupTeleportDistance(aggressiveWolf, false), 1e-4D);
        Assertions.assertEquals(76.0D, WolfCatchupHelper.getCatchupTeleportDistance(aggressiveWolf, true), 1e-4D);
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
            super(null, null, null, null, null, null, false, 0L, null, false);
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
        @Override public EntityType<?> getType() { return EntityTypes.WOLF; }
        @Override public Level level() { return this.level; }
        @Override public @Nullable LivingEntity getOwner() { return null; }
        @Override public @Nullable EntityReference<LivingEntity> getOwnerReference() { return EntityReference.of(this.ownerUuid); }
        @Override public CanineArchetype betterdogs$getCanineArchetype() { return CanineArchetype.GUARDIAN; }
        @Override public WolfPersonality betterdogs$getPersonality() { return this.personality; }
        @Override public void betterdogs$setPersonality(WolfPersonality p) { this.personality = p; }
        @Override public boolean betterdogs$hasPersonality() { return this.personality != null; }
        @Override public int betterdogs$getLastDamageTime() { return 0; }
        @Override public void betterdogs$setLastDamageTime(int time) {}
        @Override public boolean betterdogs$isSubmissive() { return false; }
        @Override public void betterdogs$setSubmissive(boolean submissive) {}
        @Override public String betterdogs$getBloodFeudTarget() { return ""; }
        @Override public void betterdogs$setBloodFeudTarget(String targetUuid) {}
        @Override public boolean betterdogs$hasBloodFeud() { return false; }
        @Override public long betterdogs$getLastMischiefDay() { return 0L; }
        @Override public void betterdogs$setLastMischiefDay(long day) {}
        @Override public boolean betterdogs$isBeingDisciplined() { return false; }
        @Override public void betterdogs$setBeingDisciplined(boolean isBeingDisciplined) {}
        @Override public void betterdogs$setSocialState(@Nullable LivingEntity target, SocialAction action, int maxDurationTicks) {}
        @Override public @Nullable LivingEntity betterdogs$getSocialTarget() { return null; }
        @Override public SocialAction betterdogs$getSocialAction() { return SocialAction.NONE; }
        @Override public boolean betterdogs$isSocialModeActive() { return false; }
        @Override public void betterdogs$tickSocialMode() {}
        @Override public long betterdogs$getDNA() { return 0L; }
        @Override public void betterdogs$setDNA(long dna) {}
        @Override public float betterdogs$getSocialScale() { return 1.0f; }
        @Override public void betterdogs$setSocialScale(float scale) {}
        @Override public @Nullable EntitySocialScheduler betterdogs$getScheduler() { return null; }
        @Override public EntitySocialScheduler betterdogs$getOrInitializeScheduler() { return null; }
        @Override public void betterdogs$tickScheduler() {}
        @Override public int betterdogs$getAffinity(String targetUuid) { return 0; }
        @Override public void betterdogs$adjustAffinity(String targetUuid, int delta) {}
        @Override public @Nullable UUID betterdogs$getLeaderUuid() { return null; }
        @Override public void betterdogs$setLeaderUuid(@Nullable UUID uuid) {}
        @Override public boolean betterdogs$isGuardMode() { return false; }
        @Override public void betterdogs$setGuardMode(boolean guardMode) {}
        @Override public @Nullable BlockPos betterdogs$getGuardPos() { return null; }
        @Override public void betterdogs$setGuardPos(@Nullable BlockPos pos) {}
        @Override public boolean betterdogs$isSittingManually() { return false; }
        @Override public void betterdogs$setSittingManually(boolean sitting) {}
        @Override public boolean betterdogs$isAdoptable() { return false; }
        @Override public void betterdogs$setAdoptable(boolean adoptable) {}
        @Override public float betterdogs$getHealthBonus() { return 0.0f; }
        @Override public void betterdogs$setHealthBonus(float hp) {}
        @Override public float betterdogs$getDamageMod() { return 0.0f; }
        @Override public void betterdogs$setDamageMod(float dmg) {}
        @Override public float betterdogs$getSpeedMod() { return 0.0f; }
        @Override public void betterdogs$setSpeedMod(float speed) {}
        @Override public boolean betterdogs$areStatsRolled() { return false; }
        @Override public void betterdogs$setStatsRolled(boolean rolled) {}
        @Override public int betterdogs$getPushWaitTimer() { return 0; }
        @Override public void betterdogs$setPushWaitTimer(int ticks) {}
        @Override public @Nullable BlockPos betterdogs$getPathfindAvoidPos() { return null; }
        @Override public void betterdogs$setPathfindAvoidPos(@Nullable BlockPos pos) {}
        @Override public GoalSelector betterdogs$getGoalSelector() { return null; }
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
        @Override public boolean betterdogs$hasFetchedItem() { return false; }
        @Override public void betterdogs$setHasFetchedItem(boolean fetched) {}
        @Override public @Nullable ItemStack betterdogs$getFetchedItemStack() { return null; }
        @Override public void betterdogs$setFetchedItemStack(@Nullable ItemStack stack) {}
        @Override public int betterdogs$getZoomiesTicks() { return 0; }
        @Override public void betterdogs$setZoomiesTicks(int ticks) {}
        @Override public int betterdogs$getHowlingTicks() { return 0; }
        @Override public void betterdogs$setHowlingTicks(int ticks) {}
    }
}
