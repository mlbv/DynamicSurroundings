package org.blockartistry.mod.DynSurround.mixins.early;

import net.minecraft.world.WorldServer;
import org.blockartistry.mod.DynSurround.server.PlayerSleepHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WorldServer.class)
public abstract class MixinWorldServer {
    /**
     * @author Mist475 (adapted from OreCrunchers asm)
     * @reason Add option to toggle stopping rain on sleep
     */
    @Overwrite
    private void resetRainAndThunder() {
        PlayerSleepHandler.resetRainAndThunder((WorldServer) (Object) this);
    }

}
