package org.blockartistry.mod.DynSurround.mixins.early;

import org.blockartistry.mod.DynSurround.mixinHelp.SourceWithRemove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import paulscode.sound.Library;
import paulscode.sound.Source;

@Mixin(value = Library.class, remap = false)
public abstract class MixinPaulsCodeSoundLibrary {

    @Redirect(method = "removeSource(Ljava/lang/String;)V"
        , remap = false,
        at = @At(value = "INVOKE", target = "Lpaulscode/sound/Source;cleanup()V", remap = false)
    )
    private void fixSourceCleanup(Source source) {
        if (source.toStream) {
            ((SourceWithRemove) source).dynamicSurroundings$setRemoved(true);
        }
        else {
            source.cleanup();
        }
    }
}
