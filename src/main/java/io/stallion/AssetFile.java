package io.stallion;


import io.stallion.processors.DefaultProcessor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AssetFile {
    private File file;
    private Processor processor;
    private String extension;
    private String relativePath;

    private String rawContent;
    private String css;
    private String headJavaScript;
    private String javaScript;

    private Map<String, String> params = new HashMap<String, String>();

    public AssetFile(File file) {
        this.file = file;
    }

    public AssetFile(String fullPath, String relativePath, Map<String, String> params) {
        file = new File(fullPath);
        extension = FilenameUtils.getExtension(file.getAbsolutePath());
        this.params = params;
        this.relativePath = relativePath;
        if (params.containsKey("processor")) {
            String processorName = params.get("processor");
            if (!"".equals(processorName)) {
                processor = ProcessorRegistry.getProcessorByName(processorName);
            }
        } else if (ProcessorRegistry.processorExistsForExtension(extension)) {
            processor = ProcessorRegistry.getProcessorByExtension(extension);
        } else {
            processor = ProcessorRegistry.DEFAULT;
        }
    }

    public void process() {
        try {
            this.rawContent = FileUtils.readFileToString(file, "utf-8");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        this.css = "";
        this.javaScript = "";
        this.headJavaScript = "";
        processor.process(this);
    }



    protected AssetFile deepCopy() {
        AssetFile af = new AssetFile(file.getAbsolutePath(), getRelativePath(), getParams());
        return af;
    }

    public Processor getProcessor() {
        return processor;
    }

    public AssetFile setProcessor(Processor processor) {
        this.processor = processor;
        return this;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getRawContent() {
        return rawContent;
    }

    public String getExtension() {
        return extension;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public String getCss() {
        return css;
    }

    public AssetFile setCss(String css) {
        this.css = css;
        return this;
    }

    public String getHeadJavaScript() {
        return headJavaScript;
    }

    public AssetFile setHeadJavaScript(String headJavaScript) {
        this.headJavaScript = headJavaScript;
        return this;
    }

    public String getJavaScript() {
        return javaScript;
    }

    public AssetFile setJavaScript(String javaScript) {
        this.javaScript = javaScript;
        return this;
    }
}
