package io.stallion;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;


public class AssetBundle {
    private File file;

    private List<AssetFile> files = null;




    public AssetBundle(File file) {
        this.file = file;
    }

    public void hydrateFiles() {
        String directory = file.getParent();
        List<AssetFile> files = new ArrayList<AssetFile>();
        try {
            String content = FileUtils.readFileToString(file, "utf-8");
            List<String> lines = Arrays.asList(content.split("\n"));
            for(String line: lines) {
                System.out.println("");
                if ("".equals(line.trim())) {
                    continue;
                }
                int i = line.indexOf("?");
                String query = "";
                if (i > -1) {
                    query = line.substring(i + 1);
                    line = line.substring(0, i);
                }
                Map<String, String> params = splitQuery(query);
                String[] parts = line.split("\\|");
                String path = parts[0];
                System.out.println("Directory: " + directory + " Path: " + path);
                files.add(new AssetFile(directory + "/" + path, path, params));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.files = files;
    }

    private Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > -1) {
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            } else {
                query_pairs.put(pair, "");
            }
        }
        return query_pairs;
    }

    public void writeToDisk() {
        try {
            if (files == null) {
                hydrateFiles();
            }
            for (AssetFile file: files) {
                file.process();
            }
            writeCss();
            writeHeadJavaScript();
            writeJavaScript();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeJavaScript() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (AssetFile f: files) {
            if (!"".equals(f.getJavaScript())) {
                System.out.print("AssetFile " + f.getRelativePath() + " js:" + f.getJavaScript());
                builder.append("\n// from " + f.getRelativePath() + "\n" + f.getJavaScript());
            }
        }
        File out = new File(file.getAbsoluteFile() + ".js");
        String source = builder.toString();
        if (!"".equals(source.trim())) {
            FileUtils.write(out, source, "UTF-8");
        }
    }

    protected void writeHeadJavaScript() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (AssetFile f: files) {
            if (!"".equals(f.getHeadJavaScript())) {
                builder.append("\n// from " + f.getRelativePath() + "\n" + f.getHeadJavaScript());
            }
        }
        File out = new File(file.getAbsoluteFile() + ".head.js");
        String source = builder.toString();
        if (!"".equals(source.trim())) {
            FileUtils.write(out, source, "UTF-8");
        }

    }

    protected void writeCss() throws IOException {
        StringBuilder builder = new StringBuilder();
        for (AssetFile f: files) {
            if (!"".equals(f.getCss())) {
                builder.append("\n // from " + f.getRelativePath() + "\n" + f.getCss());
            }
        }
        File out = new File(file.getAbsoluteFile() + ".css");
        String source = builder.toString();
        if (!"".equals(source.trim())) {
            FileUtils.write(out, source, "UTF-8");
        }

    }

}
