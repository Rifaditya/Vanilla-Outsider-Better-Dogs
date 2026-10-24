// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.1.2
package net.vanillaoutsider.betterdogs.combat;

import net.vanillaoutsider.betterdogs.ai.WolfProjectileInterceptHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 5: Heroic Sacrifice Damage Routing & Anti-One-Shot Protection [BL-BD-021]")
public class WolfDamageMitigationTest {

    @Test
    @DisplayName("Assert anti-one-shot protection clamps lethal damage leaving wolf at >= 1.0 HP")
    public void testAntiOneShotDamageClamping() {
        // High lethal damage (25.0 HP) against 20.0 HP dog during active intercept
        assertTrue(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, true, 20.0F, 25.0F),
                "Direct projectile lethal hit during intercept must qualify for mitigation");
        float postDamageHealth = WolfProjectileInterceptHelper.calculateMitigatedHealth(20.0F, 25.0F, true);
        assertEquals(1.0F, postDamageHealth, 1e-6,
                "Lethal hit mitigation must clamp post-damage health to exactly 1.0 HP");

        // Exact lethal hit (amount == health) at health >= 3.0 HP
        assertTrue(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, true, 4.0F, 4.0F),
                "Exact lethal hit against healthy dog must qualify for mitigation");
        assertEquals(1.0F, WolfProjectileInterceptHelper.calculateMitigatedHealth(4.0F, 4.0F, true));

        // Exact minimum health boundary (3.0 HP)
        assertTrue(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, true, 3.0F, 10.0F),
                "Dog at exactly 3.0 HP must be protected from one-shot killing blow");
        assertEquals(1.0F, WolfProjectileInterceptHelper.calculateMitigatedHealth(3.0F, 10.0F, true));
    }

    @Test
    @DisplayName("Assert critical health dog (< 3.0 HP) is not protected by anti-one-shot")
    public void testCriticalHealthDogNoMitigation() {
        // Dog with only 2.0 HP (already critically wounded) cannot absorb another fatal hit
        assertFalse(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, true, 2.0F, 5.0F),
                "Dog with < 3.0 HP must NOT trigger anti-one-shot protection");
        float postDamageHealth = WolfProjectileInterceptHelper.calculateMitigatedHealth(2.0F, 5.0F, false);
        assertEquals(0.0F, postDamageHealth, 1e-6,
                "Dog with < 3.0 HP takes full fatal damage");

        assertFalse(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, true, 1.0F, 1.0F),
                "Dog with 1.0 HP must NOT trigger anti-one-shot protection");
    }

    @Test
    @DisplayName("Assert non-intercept projectile damage is unmodified")
    public void testNonInterceptProjectileDamageUnmodified() {
        // Projectile hit when NOT intercepting (isIntercepting = false)
        assertFalse(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, false, 20.0F, 25.0F),
                "Projectile hitting non-intercepting wolf must not be mitigated");
        assertEquals(0.0F, WolfProjectileInterceptHelper.calculateMitigatedHealth(20.0F, 25.0F, false),
                "Fatal projectile damage to non-intercepting wolf is unmodified");

        // Non-lethal projectile hit to non-intercepting wolf
        assertEquals(15.0F, WolfProjectileInterceptHelper.calculateMitigatedHealth(20.0F, 5.0F, false), 1e-6,
                "Normal projectile damage to non-intercepting wolf is unmodified");
    }

    @Test
    @DisplayName("Assert non-projectile damage (melee/environmental) is never mitigated")
    public void testNonProjectileDamageNeverMitigated() {
        assertFalse(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(false, true, 20.0F, 30.0F),
                "Melee or explosion damage must never trigger projectile intercept mitigation");
    }

    @Test
    @DisplayName("Assert non-lethal projectile damage during intercept is unmodified")
    public void testNonLethalInterceptDamageUnmodified() {
        // Intercepting dog taking 5 damage with 20 health: non-lethal hit
        assertFalse(WolfProjectileInterceptHelper.shouldMitigateInterceptDamage(true, true, 20.0F, 5.0F),
                "Non-lethal hit during intercept must NOT trigger anti-one-shot clamping");
        assertEquals(15.0F, WolfProjectileInterceptHelper.calculateMitigatedHealth(20.0F, 5.0F, false), 1e-6,
                "Dog takes normal damage from non-lethal projectile during intercept");
    }

    @Test
    @DisplayName("Assert invulnerability duration and intercept window constants")
    public void testConstantsAndWindowDuration() {
        assertEquals(30L, WolfProjectileInterceptHelper.INTERCEPT_WINDOW_TICKS,
                "Active intercept window must be exactly 30 ticks");
    }

    @Test
    @DisplayName("Assert strict null safety across damage handling methods")
    public void testStrictNullSafety() {
        assertFalse(WolfProjectileInterceptHelper.handleInterceptDamage(null, null, 10.0F));
        assertFalse(WolfProjectileInterceptHelper.isRecentlyIntercepting(null));
        assertDoesNotThrow(() -> WolfProjectileInterceptHelper.recordIntercept(null));
        assertDoesNotThrow(() -> WolfProjectileInterceptHelper.clearIntercept(null));
    }

    @Test
    @DisplayName("Pillar 5 (Side-Safety): Assert zero client classes in WolfProjectileInterceptHelper")
    public void testZeroClientClassesInWolfProjectileInterceptHelper() throws IOException {
        Path helperPath = Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "ai", "WolfProjectileInterceptHelper.java");
        assertTrue(Files.exists(helperPath), "WolfProjectileInterceptHelper.java must exist on disk");

        List<String> lines = Files.readAllLines(helperPath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            assertFalse(line.startsWith("import net.minecraft.client."),
                    "WolfProjectileInterceptHelper must not import net.minecraft.client.* (line " + (i + 1) + ")");
            assertFalse(line.startsWith("import com.mojang.blaze3d."),
                    "WolfProjectileInterceptHelper must not import com.mojang.blaze3d.* (line " + (i + 1) + ")");
            assertFalse(line.contains("net.fabricmc.api.EnvType.CLIENT"),
                    "WolfProjectileInterceptHelper must not reference client environment (line " + (i + 1) + ")");
        }
    }

    @Test
    @DisplayName("Assert single-line GPLv3 header and single-purpose LOC ceiling")
    public void testLicenseHeaderAndLocCeiling() throws IOException {
        Path helperPath = Paths.get("src", "main", "java", "net", "vanillaoutsider", "betterdogs", "ai", "WolfProjectileInterceptHelper.java");
        List<String> lines = Files.readAllLines(helperPath);
        assertFalse(lines.isEmpty(), "WolfProjectileInterceptHelper.java must not be empty");

        assertEquals("// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3", lines.get(0).trim(),
                "Must feature exact single-line GPLv3 license header");

        assertTrue(lines.size() <= 350,
                "WolfProjectileInterceptHelper must adhere to the single-purpose LOC law (currently: " + lines.size() + " LOC)");
    }
}
