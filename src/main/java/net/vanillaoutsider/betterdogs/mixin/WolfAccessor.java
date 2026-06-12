// Verified against: Wolf.java (26.1.2+)
package net.vanillaoutsider.betterdogs.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Wolf.class)
public interface WolfAccessor {
    @Invoker("getAmbientSound")
    SoundEvent betterdogs$invokeGetAmbientSound();

    @Invoker("getSoundSet")
    net.minecraft.world.entity.animal.wolf.WolfSoundVariant.WolfSoundSet betterdogs$invokeGetSoundSet();

    @Invoker("setSoundVariant")
    void betterdogs$invokeSetSoundVariant(net.minecraft.core.Holder<net.minecraft.world.entity.animal.wolf.WolfSoundVariant> soundVariant);
}
