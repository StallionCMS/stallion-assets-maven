package io.stallion.assetsMaven;


import com.google.javascript.jscomp.CommandLineRunner;
import io.stallion.assetBundling.AssetBundle;
import io.stallion.assetBundling.AssetFile;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class CompileAssetBundles {
    public void compile(File targetDirectory) throws IOException {
        Iterator<File> iter = FileUtils.iterateFiles(targetDirectory, new String[]{"bundle"}, true);
        while(iter.hasNext()) {
            File file = iter.next();
            AssetBundle bundle = new AssetBundle(file);
            File jsFull = new File(bundle.getBundleFile().getAbsolutePath() + ".js");
            String originalMd5 = "";
            if (jsFull.exists()) {
                FileInputStream fis = new FileInputStream(jsFull);
                originalMd5 = DigestUtils.md5Hex(fis);
                fis.close();
            }

            bundle.writeToDisk();

            FileInputStream fis = new FileInputStream(jsFull);
            String newMd5 = DigestUtils.md5Hex(fis);
            fis.close();


            // If no changes, and a minified file exists, no need to minify again
            if (newMd5.equals(originalMd5) && new File(bundle.getBundleFile().getAbsolutePath() + ".min.js").exists()) {
                System.out.println("No changes to the combined asset bundle, skipping minification, for " + bundle.getBundleFile().getAbsolutePath());
                continue;
            }

            for(AssetFile af: bundle.getFiles()) {
                af.writeProcessedContents();
            }
            writeMinifiedJavascript(bundle);
        }
    }

    public void writeMinifiedJavascript(AssetBundle bundle) throws IOException {
        File bundleFile = bundle.getBundleFile();


        FileInputStream fis = new FileInputStream(new File(bundleFile.getAbsolutePath() + ".js"));
        String md5 = DigestUtils.md5Hex(fis);
        fis.close();



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

        String sourceMapPath = outFile.getAbsolutePath() + ".map";
        arguments.add("--create_source_map=" + sourceMapPath);

            // Ignore system exit
            String[] args = arguments.toArray(new String[arguments.size()]);
            System.out.println("************RUN CLOSURE COMPILER********");
            System.out.println(String.join("\n", args) + "\n");


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
        } finally {
            System.setSecurityManager(orgManager);
        }

        // Read the source map file
        File sourceMapFile = new File(sourceMapPath);
        try {
            String sourceMap = FileUtils.readFileToString(sourceMapFile, "utf-8");
            sourceMap = sourceMap.replace(sourceMapFile.getParent() + "/", "");
            FileUtils.writeStringToFile(sourceMapFile, sourceMap, "utf-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println("Finished running closure compiler with argument count of " + args.length);

    }
}
