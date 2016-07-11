package io.stallion.assetsMaven;


import com.google.javascript.jscomp.CommandLineRunner;
import io.stallion.assetBundling.AssetBundle;
import io.stallion.assetBundling.AssetFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.*;

public class CompileAssetBundles {
    public void compile(File targetDirectory) {
        Iterator<File> iter = FileUtils.iterateFiles(targetDirectory, new String[]{"bundle"}, true);
        while(iter.hasNext()) {
            File file = iter.next();
            AssetBundle bundle = new AssetBundle(file);
            bundle.writeToDisk();
            for(AssetFile af: bundle.getFiles()) {
                af.writeProcessedContents();
            }
            writeMinifiedJavascript(bundle);
        }
    }

    public void writeMinifiedJavascript(AssetBundle bundle) {
        File bundleFile = bundle.getBundleFile();
        File outFile = new File(bundleFile.getAbsolutePath() + ".min.js");
        List<String> arguments = new ArrayList<String>();
        arguments.add("--js_output_file=" + outFile.getAbsolutePath());



        for (AssetFile af: bundle.getFiles()) {
            if (af.getJavaScript().length() > 0) {

                arguments.add(af.getAbsolutePath() + ".js");
            }
        }

        arguments.add("--create_source_map="+ outFile.getAbsolutePath() + ".map");

        String[] args = arguments.toArray(new String[arguments.size()]);
        System.out.println("Run clojure compiler with arguments: " + String.join(" ", args));
        CommandLineRunner.main(args);

    }
}
