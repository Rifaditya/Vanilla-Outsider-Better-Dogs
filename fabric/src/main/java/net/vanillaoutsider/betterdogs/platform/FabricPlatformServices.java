package net.vanillaoutsider.betterdogs.platform;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.vanillaoutsider.betterdogs.WolfPersistentData;

import java.nio.file.Path;

/**
 * Fabric implementation of platform services.
 */
public class FabricPlatformServices implements PlatformServices {

    public static final AttachmentType<WolfPersistentData> WOLF_DATA = AttachmentRegistry.createPersistent(
            Identifier.parse("betterdogs:wolf_data"),
            WolfPersistentData.CODEC);

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public WolfPersistentData getWolfData(Wolf wolf) {
        return wolf.getAttachedOrCreate(WOLF_DATA, () -> WolfPersistentData.DEFAULT);
    }

    @Override
    public void setWolfData(Wolf wolf, WolfPersistentData data) {
        wolf.setAttached(WOLF_DATA, data);
    }

    @Override
    public void initAttachments() {
        // Force class loading to register the attachment
        @SuppressWarnings("unused")
        var ignored = WOLF_DATA;
    }
}
