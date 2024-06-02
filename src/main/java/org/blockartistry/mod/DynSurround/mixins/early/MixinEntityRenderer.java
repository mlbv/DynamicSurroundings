package org.blockartistry.mod.DynSurround.mixins.early;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import org.blockartistry.mod.DynSurround.client.RenderWeather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/*
To fix weather2's lotr compatibility I changed its subclassing behaviour of entityRenderer to mixins instead.
They just put these methods behind a config, whereas we actually change them here. Which is why I upped the priority here.
 */
@Mixin(value = EntityRenderer.class, priority = 1001)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {

    /**
     * @author Mist475 (adapted from OreCrunchers asm)
     * @reason Replace rain particle rendering with our own
     */
    @Overwrite
    private void addRainParticles() {
        RenderWeather.addRainParticles((EntityRenderer) (Object) this);
    }

    /**
     * @author Mist475 (adapted from OreCrunchers asm)
     * @reason Replace render with our own
     */
    @Overwrite
    protected void renderRainSnow(float p_78474_1_) {
        RenderWeather.renderRainSnow((EntityRenderer) (Object) this, p_78474_1_);
    }
}
