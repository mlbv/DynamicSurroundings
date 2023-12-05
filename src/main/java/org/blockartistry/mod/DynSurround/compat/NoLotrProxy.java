package org.blockartistry.mod.DynSurround.compat;

public class NoLotrProxy implements ILOTRProxy {
    @Override
    public String getSeason() {
        return "noseason";
    }

    @Override
    public void registerLOTRBiomes() {

    }

}
