package org.blockartistry.mod.DynSurround.compat;

import lotr.common.LOTRDate;

public class LotrProxy implements ILOTRProxy {
    @Override
    public String getSeason() {
        return LOTRDate.ShireReckoning.getSeason().name();
    }
}
