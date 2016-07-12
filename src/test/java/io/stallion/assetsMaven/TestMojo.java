package io.stallion.assetsMaven;


import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.Test;


public class TestMojo {
    @Test
    public void testMojo() throws Exception {
        URL url = TestMojo.class.getResource("/site.bundle");
        File file = new File(url.toURI());

        new CompileAssetBundles().compile(file.getParentFile());
    }
}
