package io.stallion;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProcessAssetBundles {
    public void process(File targetDirectory) {
        Iterator<File> iter = FileUtils.iterateFiles(targetDirectory, new String[]{"bundle"}, true);
        while(iter.hasNext()) {
            File file = iter.next();
            AssetBundle bundle = new AssetBundle(file);
            bundle.writeToDisk();
        }

    }
}
