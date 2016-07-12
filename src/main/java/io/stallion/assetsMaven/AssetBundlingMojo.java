package io.stallion.assetsMaven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Goal which compiles all .bundle files into a concatenated, minified version.
 *
  */
@Mojo( name = "bundle-assets", defaultPhase = LifecyclePhase.COMPILE )
public class AssetBundlingMojo
    extends AbstractMojo
{
    /**
     * Location of the file.
     */
    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private File outputDirectory;

    public void execute()
        throws MojoExecutionException
    {
        File f = outputDirectory;

        if (!f.exists()) {
            f.mkdirs();
        }

        new CompileAssetBundles().compile(new File(outputDirectory.getAbsolutePath() + "/classes/assets/"));


    }
}
