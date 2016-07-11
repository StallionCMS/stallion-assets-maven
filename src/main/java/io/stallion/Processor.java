package io.stallion;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class Processor {

    public AssetFile submitForProcessing(AssetFile original) {
        AssetFile af = original.deepCopy();
        process(af);
        return af;
    }

    public abstract void process(AssetFile af);

    public abstract String getName();

    public abstract String[] getExtensions();

}
