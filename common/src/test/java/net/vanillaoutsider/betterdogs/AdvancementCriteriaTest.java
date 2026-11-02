// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

import java.io.InputStream;
import java.util.List;
import net.vanillaoutsider.betterdogs.util.WolfAdvancementHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AdvancementCriteriaTest {

    private static final List<String> ADVANCEMENT_FILES = List.of(
        "tame_wolf.json",
        "pet_dog.json",
        "soothe_dog.json",
        "favorite_treat.json",
        "zoomies.json",
        "fetch_stick.json",
        "guard_mode.json",
        "morning_gift.json",
        "adopt_dog.json",
        "horn_command.json",
        "cure_inbred.json",
        "chorus_howl.json",
        "giant_lineage.json"
    );

    @Test
    public void testAll13AdvancementJsonFilesExistAndAreValid() {
        for (String file : ADVANCEMENT_FILES) {
            String path = "/data/betterdogs/advancements/husbandry/" + file;
            InputStream stream = getClass().getResourceAsStream(path);
            Assertions.assertNotNull(stream, "Advancement JSON file must exist on classpath: " + path);
        }
    }

    @Test
    public void testWolfAdvancementHelperNullSafety() {
        Assertions.assertDoesNotThrow(() -> WolfAdvancementHelper.grantAdvancement(null, null));
        Assertions.assertDoesNotThrow(() -> WolfAdvancementHelper.grantAdvancement(null, "tame_wolf"));
    }
}
