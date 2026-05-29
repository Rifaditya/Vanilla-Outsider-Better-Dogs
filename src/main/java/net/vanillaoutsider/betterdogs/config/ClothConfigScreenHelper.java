// Verified against: ClothConfigScreenHelper.java (26.1.2+)
package net.vanillaoutsider.betterdogs.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothConfigScreenHelper {
    public static ConfigScreenFactory<?> createFactory() {
        return ClothConfigScreenHelper::createScreen;
    }

    public static Screen createScreen(Screen parent) {
        BetterDogsConfig config = BetterDogsConfig.get();
        
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.betterdogs.title"));

        builder.setSavingRunnable(BetterDogsConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Warning Tooltip Component explaining GameRule mapping
        Component[] warningTooltip = new Component[]{
            Component.translatable("config.betterdogs.warning")
        };

        // --- GENERAL CATEGORY ---
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.betterdogs.category.general"));
        
        general.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.globalSpeedBuff"), config.globalSpeedBuff)
                .setDefaultValue(0.20)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.globalSpeedBuff = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterdogs.enableStormAnxiety"), config.enableStormAnxiety)
                .setDefaultValue(true)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.enableStormAnxiety = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterdogs.enableCliffSafety"), config.enableCliffSafety)
                .setDefaultValue(true)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.enableCliffSafety = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterdogs.enableFleeLowHealth"), config.enableFleeLowHealth)
                .setDefaultValue(true)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.enableFleeLowHealth = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterdogs.enableFriendlyFireProtection"), config.enableFriendlyFireProtection)
                .setDefaultValue(true)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.enableFriendlyFireProtection = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterdogs.enableDogsEatRawGroundFood"), config.enableDogsEatRawGroundFood)
                .setDefaultValue(true)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.enableDogsEatRawGroundFood = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterdogs.enableDogsEatCookedGroundFood"), config.enableDogsEatCookedGroundFood)
                .setDefaultValue(true)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.enableDogsEatCookedGroundFood = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterdogs.pacifistGuardBuffs"), config.pacifistGuardBuffs)
                .setDefaultValue(false)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistGuardBuffs = val)
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterdogs.enableInbredCuring"), config.enableInbredCuring)
                .setDefaultValue(false)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.enableInbredCuring = val)
                .build());

        general.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.followCatchUpSpeed"), config.followCatchUpSpeed)
                .setDefaultValue(1.5)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.followCatchUpSpeed = val)
                .build());

        general.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.followCatchUpThreshold"), config.followCatchUpThreshold)
                .setDefaultValue(10.0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.followCatchUpThreshold = val)
                .build());

        general.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.teleportToOwnerSpread"), config.teleportToOwnerSpread)
                .setDefaultValue(4.0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.teleportToOwnerSpread = val)
                .build());

        general.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.teleportMultiplier"), config.teleportMultiplier)
                .setDefaultValue(2.0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.teleportMultiplier = val)
                .build());

        general.addEntry(entryBuilder.startFloatField(Component.translatable("config.betterdogs.stormAnxietyTriggerChance"), config.stormAnxietyTriggerChance)
                .setDefaultValue(0.01f)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.stormAnxietyTriggerChance = val)
                .build());

        general.addEntry(entryBuilder.startFloatField(Component.translatable("config.betterdogs.stormWhineChance"), config.stormWhineChance)
                .setDefaultValue(0.02f)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.stormWhineChance = val)
                .build());

        // --- PERSONALITIES CATEGORY ---
        ConfigCategory personalities = builder.getOrCreateCategory(Component.translatable("config.betterdogs.category.personalities"));

        // Aggressive
        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.aggressiveHealthBonus"), config.aggressiveHealthBonus)
                .setDefaultValue(-10.0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveHealthBonus = val)
                .build());

        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.aggressiveSpeedModifier"), config.aggressiveSpeedModifier)
                .setDefaultValue(0.15)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveSpeedModifier = val)
                .build());

        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.aggressiveDetectionRange"), config.aggressiveDetectionRange)
                .setDefaultValue(20.0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveDetectionRange = val)
                .build());

        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.aggressiveChaseDistance"), config.aggressiveChaseDistance)
                .setDefaultValue(50.0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveChaseDistance = val)
                .build());

        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.aggressiveDamageModifier"), config.aggressiveDamageModifier)
                .setDefaultValue(0.50)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveDamageModifier = val)
                .build());

        personalities.addEntry(entryBuilder.startFloatField(Component.translatable("config.betterdogs.aggressiveFollowStart"), config.aggressiveFollowStart)
                .setDefaultValue(50.0f)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveFollowStart = val)
                .build());

        personalities.addEntry(entryBuilder.startFloatField(Component.translatable("config.betterdogs.aggressiveFollowStop"), config.aggressiveFollowStop)
                .setDefaultValue(2.0f)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveFollowStop = val)
                .build());

        personalities.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.aggressiveFleeChance"), config.aggressiveFleeChance)
                .setDefaultValue(10)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveFleeChance = val)
                .build());

        // Pacifist
        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.pacifistHealthBonus"), config.pacifistHealthBonus)
                .setDefaultValue(20.0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistHealthBonus = val)
                .build());

        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.pacifistSpeedModifier"), config.pacifistSpeedModifier)
                .setDefaultValue(-0.10)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistSpeedModifier = val)
                .build());

        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.pacifistDamageModifier"), config.pacifistDamageModifier)
                .setDefaultValue(-0.30)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistDamageModifier = val)
                .build());

        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.pacifistKnockbackModifier"), config.pacifistKnockbackModifier)
                .setDefaultValue(0.5)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistKnockbackModifier = val)
                .build());

        personalities.addEntry(entryBuilder.startFloatField(Component.translatable("config.betterdogs.pacifistFollowStart"), config.pacifistFollowStart)
                .setDefaultValue(5.0f)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistFollowStart = val)
                .build());

        personalities.addEntry(entryBuilder.startFloatField(Component.translatable("config.betterdogs.pacifistFollowStop"), config.pacifistFollowStop)
                .setDefaultValue(2.0f)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistFollowStop = val)
                .build());

        personalities.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.pacifistFleeChance"), config.pacifistFleeChance)
                .setDefaultValue(100)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistFleeChance = val)
                .build());

        // Normal
        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.normalHealthBonus"), config.normalHealthBonus)
                .setDefaultValue(0.0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.normalHealthBonus = val)
                .build());

        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.normalSpeedModifier"), config.normalSpeedModifier)
                .setDefaultValue(0.0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.normalSpeedModifier = val)
                .build());

        personalities.addEntry(entryBuilder.startDoubleField(Component.translatable("config.betterdogs.normalDamageModifier"), config.normalDamageModifier)
                .setDefaultValue(0.0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.normalDamageModifier = val)
                .build());

        personalities.addEntry(entryBuilder.startFloatField(Component.translatable("config.betterdogs.normalFollowStart"), config.normalFollowStart)
                .setDefaultValue(10.0f)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.normalFollowStart = val)
                .build());

        personalities.addEntry(entryBuilder.startFloatField(Component.translatable("config.betterdogs.normalFollowStop"), config.normalFollowStop)
                .setDefaultValue(2.0f)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.normalFollowStop = val)
                .build());

        personalities.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.normalFleeChance"), config.normalFleeChance)
                .setDefaultValue(50)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.normalFleeChance = val)
                .build());

        // --- BREEDING & GENETICS CATEGORY ---
        ConfigCategory breeding = builder.getOrCreateCategory(Component.translatable("config.betterdogs.category.breeding"));

        breeding.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.spawnChanceNormal"), config.spawnChanceNormal)
                .setDefaultValue(60)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.spawnChanceNormal = val)
                .build());

        breeding.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.spawnChanceAggressive"), config.spawnChanceAggressive)
                .setDefaultValue(20)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.spawnChanceAggressive = val)
                .build());

        breeding.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.spawnChancePacifist"), config.spawnChancePacifist)
                .setDefaultValue(20)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.spawnChancePacifist = val)
                .build());

        breeding.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.breedingSameParentChance"), config.breedingSameParentChance)
                .setDefaultValue(80)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.breedingSameParentChance = val)
                .build());

        breeding.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.breedingSameParentOtherChance"), config.breedingSameParentOtherChance)
                .setDefaultValue(10)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.breedingSameParentOtherChance = val)
                .build());

        breeding.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.breedingMixedDominantChance"), config.breedingMixedDominantChance)
                .setDefaultValue(40)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.breedingMixedDominantChance = val)
                .build());

        breeding.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.breedingMixedRecessiveChance"), config.breedingMixedRecessiveChance)
                .setDefaultValue(20)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.breedingMixedRecessiveChance = val)
                .build());

        breeding.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.breedingDilutedNormalChance"), config.breedingDilutedNormalChance)
                .setDefaultValue(50)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.breedingDilutedNormalChance = val)
                .build());

        breeding.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.breedingDilutedOtherChance"), config.breedingDilutedOtherChance)
                .setDefaultValue(25)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.breedingDilutedOtherChance = val)
                .build());

        // --- TERRITORIALITY CATEGORY ---
        ConfigCategory territoriality = builder.getOrCreateCategory(Component.translatable("config.betterdogs.category.territoriality"));

        territoriality.addEntry(entryBuilder.startFloatField(Component.translatable("config.betterdogs.wildHuntHealthThreshold"), config.wildHuntHealthThreshold)
                .setDefaultValue(0.5f)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.wildHuntHealthThreshold = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixAAWar"), config.terrMatrixAAWar)
                .setDefaultValue(80)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixAAWar = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixAAMerge"), config.terrMatrixAAMerge)
                .setDefaultValue(10)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixAAMerge = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixANWar"), config.terrMatrixANWar)
                .setDefaultValue(50)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixANWar = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixANMerge"), config.terrMatrixANMerge)
                .setDefaultValue(40)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixANMerge = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixAPWar"), config.terrMatrixAPWar)
                .setDefaultValue(10)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixAPWar = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixAPMerge"), config.terrMatrixAPMerge)
                .setDefaultValue(50)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixAPMerge = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixNNWar"), config.terrMatrixNNWar)
                .setDefaultValue(20)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixNNWar = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixNNMerge"), config.terrMatrixNNMerge)
                .setDefaultValue(50)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixNNMerge = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixNPWar"), config.terrMatrixNPWar)
                .setDefaultValue(5)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixNPWar = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixNPMerge"), config.terrMatrixNPMerge)
                .setDefaultValue(45)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixNPMerge = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixPPWar"), config.terrMatrixPPWar)
                .setDefaultValue(0)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixPPWar = val)
                .build());

        territoriality.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.terrMatrixPPMerge"), config.terrMatrixPPMerge)
                .setDefaultValue(50)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.terrMatrixPPMerge = val)
                .build());

        // --- GIFTS CATEGORY ---
        ConfigCategory gifts = builder.getOrCreateCategory(Component.translatable("config.betterdogs.category.gifts"));

        gifts.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.giftCooldownMin"), config.giftCooldownMin)
                .setDefaultValue(12000)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.giftCooldownMin = val)
                .build());

        gifts.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.giftCooldownMax"), config.giftCooldownMax)
                .setDefaultValue(18000)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.giftCooldownMax = val)
                .build());

        gifts.addEntry(entryBuilder.startFloatField(Component.translatable("config.betterdogs.giftTriggerChance"), config.giftTriggerChance)
                .setDefaultValue(0.01f)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.giftTriggerChance = val)
                .build());

        gifts.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.aggressiveGiftBone"), config.aggressiveGiftBone)
                .setDefaultValue(40)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveGiftBone = val)
                .build());

        gifts.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.aggressiveGiftFlesh"), config.aggressiveGiftFlesh)
                .setDefaultValue(35)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveGiftFlesh = val)
                .build());

        gifts.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.aggressiveGiftArrow"), config.aggressiveGiftArrow)
                .setDefaultValue(15)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.aggressiveGiftArrow = val)
                .build());

        gifts.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.pacifistGiftBerries"), config.pacifistGiftBerries)
                .setDefaultValue(30)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistGiftBerries = val)
                .build());

        gifts.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.pacifistGiftSeeds"), config.pacifistGiftSeeds)
                .setDefaultValue(25)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistGiftSeeds = val)
                .build());

        gifts.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.pacifistGiftFlower"), config.pacifistGiftFlower)
                .setDefaultValue(20)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistGiftFlower = val)
                .build());

        gifts.addEntry(entryBuilder.startIntField(Component.translatable("config.betterdogs.pacifistGiftMushroom"), config.pacifistGiftMushroom)
                .setDefaultValue(15)
                .setTooltip(warningTooltip)
                .setSaveConsumer(val -> config.pacifistGiftMushroom = val)
                .build());

        return builder.build();
    }
}
