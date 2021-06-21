package com.mzh.emock.util;

import java.util.function.Consumer;

public class EMUtil {
    public static void optWithParent(Class<?> curr, Consumer<Class<?>> consumer) {
        while (curr != null) {
            consumer.accept(curr);
            curr = curr.getSuperclass();
        }
    }

    public static String removeSpace(String str) {
        return str == null ? "" : str.replace(" ", "");
    }

    public static String formatResourcePath(String path) {
        path=path.replace(".","/");
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        if (!path.startsWith("classpath")) {
            path = "classpath*:" + path;
        }
        return path + "**/*.class";
    }
}
