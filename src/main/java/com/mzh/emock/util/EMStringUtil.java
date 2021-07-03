package com.mzh.emock.util;

public class EMStringUtil {
    public static boolean isEmpty(String str){
        return str==null || str.trim().length()==0;
    }

    public static String removeSpace(String str) {
        return str == null ? "" : str.replace(" ", "");
    }
}
