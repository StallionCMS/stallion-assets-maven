package io.stallion;

import io.stallion.processors.CommandProcessor;
import io.stallion.processors.DefaultProcessor;
import io.stallion.processors.SassProcessor;
import io.stallion.processors.VueProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProcessorRegistry {
    public static final Processor DEFAULT = new DefaultProcessor();
    private static Map<String, Processor> processorByExtension = new HashMap<String, Processor>();
    private static Map<String, Processor> processorByName = new HashMap<String, Processor>();

    static {
        register(new CommandProcessor());
        register(new DefaultProcessor());
        register(new SassProcessor());
        register(new VueProcessor());
    }

    public static void register(Processor processor) {
        for(String ext: processor.getExtensions()) {
            processorByExtension.put(ext, processor);
        }
        processorByName.put(processor.getName(), processor);
    }

    public static Processor getProcessorByName(String name) {
        return processorByName.get(name);
    }

    public static boolean processorExistsForExtension(String extension) {
        boolean result = processorByExtension.containsKey(extension);
        System.out.println("Processor for extension " + extension + " result is " + result);
        return result;
    }

    public static Processor getProcessorByExtension(String extension) {

        Processor p = processorByExtension.get(extension);
        System.out.println("Processor is " + p.getClass().getCanonicalName());
        return p;
    }
}
