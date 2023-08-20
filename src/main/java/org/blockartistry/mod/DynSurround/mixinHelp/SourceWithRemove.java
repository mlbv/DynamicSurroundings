package org.blockartistry.mod.DynSurround.mixinHelp;

/**
 * Based on patches by CreativeMD
 * Add a removed boolean field to Source to keep track of sources
 */
public interface SourceWithRemove {
    void dynamicSurroundings$setRemoved(boolean removed);
    boolean dynamicSurroundings$getRemoved();
}
