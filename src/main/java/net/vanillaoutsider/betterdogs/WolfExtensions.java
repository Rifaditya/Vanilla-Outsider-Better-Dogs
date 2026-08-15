// Copyright (C) 2026 Dasik (Rifaditya) | GNU GPLv3
package net.vanillaoutsider.betterdogs;

public interface WolfExtensions {
    WolfPersonality betterdogs$getPersonality();
    void betterdogs$setPersonality(WolfPersonality personality);
    boolean betterdogs$hasPersonality();

    float betterdogs$getSocialScale();
    void betterdogs$setSocialScale(float scale);

    long betterdogs$getDnaSeed();

    String betterdogs$getFavoriteTreat();

    void betterdogs$setFavoriteTreat(String treat);

    long betterdogs$getSoothedTime();

    void betterdogs$setSoothedTime(long time);

    net.minecraft.core.BlockPos betterdogs$getSoundLocationTarget();
    void betterdogs$setSoundLocationTarget(net.minecraft.core.BlockPos pos);

    int betterdogs$getPassiveOverrideTicks();
    void betterdogs$setPassiveOverrideTicks(int ticks);

    String betterdogs$getNemesisEntityType();
    void betterdogs$setNemesisEntityType(String type);

    long betterdogs$getNemesisExpiryTime();
    void betterdogs$setNemesisExpiryTime(long time);
}
