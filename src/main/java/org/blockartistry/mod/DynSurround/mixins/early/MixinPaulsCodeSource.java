package org.blockartistry.mod.DynSurround.mixins.early;

import org.blockartistry.mod.DynSurround.mixinHelp.SourceWithRemove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import paulscode.sound.Source;

/**
 * Based on patches by CreativeMD
 * Mixin version of <a href="https://github.com/MinecraftForge/MinecraftForge/pull/4765">...</a>
 */
@Mixin(value = Source.class, remap = false)
public abstract class MixinPaulsCodeSource implements SourceWithRemove {
    @Unique
    public boolean dynamicSurroundings$removed;

    @Override
    public void dynamicSurroundings$setRemoved(boolean removed) {
        dynamicSurroundings$removed = removed;
    }

    @Override
    public boolean dynamicSurroundings$getRemoved() {
        return dynamicSurroundings$removed;
    }
}
