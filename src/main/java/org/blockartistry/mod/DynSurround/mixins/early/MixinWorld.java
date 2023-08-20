package org.blockartistry.mod.DynSurround.mixins.early;

import net.minecraft.world.World;
import org.blockartistry.mod.DynSurround.server.WorldHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(World.class)
public abstract class MixinWorld {
    /**
     * @author Mist475 (adapted from OreCrunchers asm)
     * @reason handle weather ourselves
     */
    @Overwrite(remap = false)
    public void updateWeatherBody() {
        WorldHandler.updateWeatherBody((World) (Object) this);
    }
}
