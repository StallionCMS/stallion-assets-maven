package io.stallion.processors;


import io.stallion.AssetFile;
import io.stallion.Processor;

import java.util.List;
import java.util.Map;




public class SassProcessor extends Processor {

    @Override
    public void process(AssetFile af) {

    }

    @Override
    public String getName() {
        return "sass";
    }

    @Override
    public String[] getExtensions() {
        return new String[] {"sass"};
    }
}
