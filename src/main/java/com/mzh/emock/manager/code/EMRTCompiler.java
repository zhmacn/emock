package com.mzh.emock.manager.code;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

public class EMRTCompiler {

    private static final JavaCompiler compiler;
    private static final StandardJavaFileManager stdManager;
    private static final DiagnosticCollector<JavaFileObject> diagnosticCollector;
    static{
        compiler = ToolProvider.getSystemJavaCompiler();
        diagnosticCollector=new DiagnosticCollector<>();
        stdManager = compiler.getStandardFileManager(diagnosticCollector, Locale.getDefault(), StandardCharsets.UTF_8);
    }

    public static Map<String, byte[]> compile(String fileName, String source) throws IOException {
        try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {
            JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);
            CompilationTask task = compiler.getTask(null, manager, diagnosticCollector, null, null, Arrays.asList(javaFileObject));
            Boolean result = task.call();
            if (result == null || !result) {
                String diagnostic= diagnosticCollector.getDiagnostics().toString().replace(fileName,"EM:");
                throw new RuntimeException("Compilation failed:"+diagnostic);
            }
            return manager.getClassBytes();
        }
    }

    public static Class<?> loadClass(String name, Map<String, byte[]> classBytes) throws ClassNotFoundException, IOException {
        try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
            return classLoader.loadClass(name);
        }
    }
}
