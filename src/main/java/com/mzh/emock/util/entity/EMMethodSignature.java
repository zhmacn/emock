package com.mzh.emock.util.entity;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class EMMethodSignature {
    private Method nativeMethod;
    private Map<String,String> importMap=new HashMap<>();
    private String simpleSignature;

    public EMMethodSignature(Method method){
        this.nativeMethod=method;
        resolveSignature();
    }

    private void resolveSignature(){
        AtomicReference<String> si= new AtomicReference<>(nativeMethod.toGenericString());
        Map<String,String> typeMap=findTypeMap(si.get());
        typeMap.keySet().stream().sorted((a,b)->-1*a.compareTo(b)).forEach(
                k->{
                    String raw=typeMap.get(k);
                    si.set(si.get().replace(k,raw));
                    int dot=raw.indexOf('.');
                    if(dot==-1){
                        importMap.put(k,raw);
                    }else{
                        importMap.put(k.substring(0,k.length()-raw.length()+dot),raw.substring(0,dot));
                    }
                }
        );
        this.simpleSignature=si.get();
    }
    private Map<String,String> findTypeMap(String si){
        StringBuilder sb=new StringBuilder();
        char[] cs=si.toCharArray();
        Map<String,String> nameMap=new HashMap<>();
        for(int i=0;i<cs.length;i++){
            if(cs[i]=='<' || cs[i]=='>' || cs[i]==',' || cs[i]==' ' || cs[i]=='(' || cs[i]==')'){
                Map.Entry<String,String> kv=simpleName(sb.toString());
                nameMap.put(kv.getKey(),kv.getValue());
                sb.delete(0,sb.length());
            }else{
                sb.append(cs[i]);
            }
        }
        Map.Entry<String,String> kv=simpleName(sb.toString());
        nameMap.put(kv.getKey(),kv.getValue());
        return nameMap;
    }

    public static Map.Entry<String,String> simpleName(String fullName){
        String simpleName=fullName;
        int x=fullName.lastIndexOf('.');
        if(x!=-1){
            if(x>2 && fullName.toCharArray()[x-1]=='.' && fullName.toCharArray()[x-2]=='.'){
                simpleName=fullName.substring(0,fullName.length()-3);
                int x1=simpleName.lastIndexOf('.');
                if(x1!=-1) {
                    simpleName = fullName.substring(x1 + 1);
                }
            }else {
                simpleName = fullName.substring(x + 1);
            }
        }
        return new AbstractMap.SimpleEntry<>(fullName,simpleName);
    }



    public Map<String, String> getImportMap() {
        return importMap;
    }

    public void setImportMap(Map<String, String> importMap) {
        this.importMap = importMap;
    }

    public String getSimpleSignature() {
        return simpleSignature;
    }

    public void setSimpleSignature(String simpleSignature) {
        this.simpleSignature = simpleSignature;
    }

    public Method getNativeMethod() {
        return nativeMethod;
    }

    public void setNativeMethod(Method nativeMethod) {
        this.nativeMethod = nativeMethod;
    }
}
