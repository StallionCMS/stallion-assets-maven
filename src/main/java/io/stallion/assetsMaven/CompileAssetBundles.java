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
        arguments.add("--language_out=ES5");
        arguments.add("--compilation_level=SIMPLE_OPTIMIZATIONS");
        arguments.add("--warning_level=QUIET");
        for (AssetFile af: bundle.getFiles()) {
            if (af.getJavaScript().length() > 0) {
                arguments.add("--js");
                arguments.add(af.getAbsolutePath() + ".js");
            }
        }

        arguments.add("--create_source_map="+ outFile.getAbsolutePath() + ".map");

        String[] args = arguments.toArray(new String[arguments.size()]);
        System.out.println("Run closure compiler with arguments: " + String.join(" ", args));


        // Grrrr, the Google closure library always does a system exit after success,
        // and the internal methods are all protected, so I cannot call them.
        // Thus I have to do this hack to prevent the build from exiting.
        PreventExitSecurityManager secManager = new PreventExitSecurityManager();
        SecurityManager orgManager = System.getSecurityManager();
        System.setSecurityManager(secManager);

        try {
            CommandLineRunner.main(args);
        }
        catch (SecurityException e) {
            // Ignore system exit
        } finally {
            System.setSecurityManager(orgManager);
        }

        System.out.println("Finished running closure compiler with arguments: " + String.join(" ", args));

    }
}
