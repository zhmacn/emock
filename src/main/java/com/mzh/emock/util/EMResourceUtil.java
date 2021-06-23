package com.mzh.emock.util;

public class EMResourceUtil {
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
