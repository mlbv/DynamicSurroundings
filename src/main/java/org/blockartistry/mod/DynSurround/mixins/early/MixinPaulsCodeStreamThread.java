package org.blockartistry.mod.DynSurround.mixins.early;

import org.blockartistry.mod.DynSurround.mixinHelp.SourceWithRemove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import paulscode.sound.Source;
import paulscode.sound.StreamThread;

import java.util.ListIterator;

@Mixin(value = StreamThread.class, remap = false)
public abstract class MixinPaulsCodeStreamThread {
    @Redirect(method = "run()V",
        at = @At(value = "INVOKE", target = "Ljava/util/ListIterator;next()Ljava/lang/Object;", remap = false),
        remap = false)
    private Object redirectIteratorToFixedVersion(ListIterator<Source> iter) {
        Source source = iter.next();
        if (((SourceWithRemove) source).dynamicSurroundings$getRemoved()) {
            source.cleanup();
            return null;
        }
        return source;
    }
}
