package org.blockartistry.mod.DynSurround.mixins.early;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.util.ResourceLocation;
import org.blockartistry.mod.DynSurround.client.sound.cache.SoundCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URL;
import java.util.Map;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager {
    /**
     * @author Mist475 (adapted from OreCrunchers asm)
     * @reason Cache urls?
     */
    @Overwrite
    public static URL getURLForSoundResource(ResourceLocation p_148612_0_) {
        return SoundCache.getURLForSoundResource(p_148612_0_);
    }

    /**
     * @author Mist475 (adapted from OreCrunchers asm)
     * @reason Handle pitch ourselves
     */
    @Overwrite
    public float getNormalizedPitch(ISound p_148606_1_, SoundPoolEntry p_148606_2_) {
        return org.blockartistry.mod.DynSurround.client.sound.SoundManager.getNormalizedPitch(p_148606_1_, p_148606_2_);
    }

    /**
     * @author Mist475 (adapted from OreCrunchers asm)
     * @reason Handle volume ourselves
     */
    @Overwrite
    public float getNormalizedVolume(ISound p_148594_1_, SoundPoolEntry p_148594_2_, SoundCategory p_148594_3_) {
        return org.blockartistry.mod.DynSurround.client.sound.SoundManager.getNormalizedVolume(p_148594_1_, p_148594_2_, p_148594_3_);
    }

    /**
     * Modify this.playingSoundsStopTime.put(s, this.playTime + 20);
     * Into this.playingSoundsStopTime.put(s, this.playTime + 0);
     */
    @Redirect(method = "playSound(Lnet/minecraft/client/audio/ISound;)V",
        at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
            , ordinal = 0,
            remap = false))
    private Object reducePlayingSoundsStopTime(Map<Object, Object> instance, Object k, Object v) {
        if (v instanceof Integer i) {
            instance.put(k, i - 20);
        }
        return null;
    }

    @Inject(
        method = "playSound(Lnet/minecraft/client/audio/ISound;)V",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;play(Ljava/lang/String;)V",
            shift = At.Shift.AFTER)
        , remap = false)
    private void flushSound(ISound p_148611_1_, CallbackInfo ci) {
        org.blockartistry.mod.DynSurround.client.sound.SoundManager.flushSound();
    }
}
