package org.blockartistry.mod.DynSurround.compat;

import lotr.common.LOTRDate;
import lotr.common.LOTRDimension;
import org.blockartistry.mod.DynSurround.data.BiomeRegistry;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class LotrProxy implements ILOTRProxy {

    @Override
    public String getSeason() {
        return LOTRDate.ShireReckoning.getSeason().name();
    }

    @Override
    public void registerLOTRBiomes() {
        Stream.concat(
                Arrays.stream(LOTRDimension.MIDDLE_EARTH.biomeList),
                Arrays.stream(LOTRDimension.UTUMNO.biomeList))
            .filter(Objects::nonNull)
            .forEach(lotrBiome ->  BiomeRegistry.registry.put(lotrBiome.biomeName, new BiomeRegistry.BiomeRegistryEntry(lotrBiome)));
    }

}
