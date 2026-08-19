// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
// Verified against: Minecraft 26.2
package net.vanillaoutsider.betterdogs;

import net.vanillaoutsider.betterdogs.util.WolfAdvancementHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Step 35: Visual Advancements & Milestones Tests")
class AdvancementCriteriaTest {

    private static final List<String> ADVANCEMENT_FILES = List.of(
            "a_pack_of_guardians.json",
            "a_pack_of_personalities.json",
            "cure_runt.json",
            "inbred_runt.json",
            "litter_four.json",
            "litter_legend.json",
            "litter_three.json",
            "litter_two.json",
            "on_guard.json",
            "on_patrol.json",
            "outcross_runt.json",
            "put_up_for_adoption.json",
            "self_service.json"
    );

    @Test
    @DisplayName("Assert all 13 advancement JSON files exist on classpath")
    void testAllAdvancementJsonFilesExist() {
        for (String file : ADVANCEMENT_FILES) {
            String path = "/data/minecraft/advancement/husbandry/" + file;
            InputStream stream = getClass().getResourceAsStream(path);
            assertNotNull(stream, "Advancement JSON file must exist on classpath: " + path);
        }
    }

    @Test
    @DisplayName("Assert WolfAdvancementHelper strict null safety")
    void testWolfAdvancementHelperNullSafety() {
        assertDoesNotThrow(() -> WolfAdvancementHelper.grantAdvancement(null, null));
        assertDoesNotThrow(() -> WolfAdvancementHelper.grantAdvancement(null, "cure_runt"));
        assertDoesNotThrow(() -> WolfAdvancementHelper.grantAdvancement(null, "cure_runt", "cure_inbred_wolf"));
    }
}
