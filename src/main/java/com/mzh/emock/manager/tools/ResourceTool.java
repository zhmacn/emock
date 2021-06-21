package com.mzh.emock.manager.tools;

import com.mzh.emock.manager.controller.EMManagerController;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ResourceTool {
    private static final Charset resCharset= StandardCharsets.UTF_8;
    public static String loadResourceAsString(String pathName){
        try(InputStream is= EMManagerController.class.getResourceAsStream(pathName)){
            assert is != null;
            byte[] ba=new byte[is.available()];
            is.read(ba,0,ba.length);
            return new String(ba,resCharset);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
