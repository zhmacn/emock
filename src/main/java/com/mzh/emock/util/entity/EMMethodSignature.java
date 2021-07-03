package com.mzh.emock.util.entity;


import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class EMMethodSignature {
    private static final Map<String,String> typeRelation=new HashMap<String,String>(){{
        put("void","Void");
        put("boolean","Boolean");
        put("char","Character");
        put("byte","Byte");
        put("short","Short");
        put("int","Integer");
        put("long","Long");
        put("float","Float");
        put("double","Double");
        }};
    private static final List<String> importIgnore=Arrays.asList("java.lang","com.mzh.emock.manager.code");
    private Method nativeMethod;
    private Map<String,String> fullTypeMap=new HashMap<>();
    private List<String> importList=new ArrayList<>();
    private String simpleSignature;
    private String returnType;
    private String parameterList;



    public EMMethodSignature(Method method){
        this.nativeMethod=method;
        resolveSignature();
    }

    private List<String> splitBefore(String before){
        List<String> ls=new ArrayList<>();
        char[] cs=before.trim().toCharArray();
        int lq=0;
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<cs.length;i++){
            if(cs[i]==' ' && lq==0 && sb.length()!=0){
                ls.add(sb.toString());
                sb.delete(0,sb.length());
            }
            if(cs[i]=='<'){
                lq--;
            }
            if(cs[i]=='>'){
                lq++;
            }
            sb.append(cs[i]);
        }
        if(sb.length()>0){
            ls.add(sb.toString());
        }
        return ls;
    }


    private void resolveSignature(){
        String des=nativeMethod.toGenericString();
        List<String> bl=splitBefore(des.substring(0,des.indexOf('(')));
        String lModifier=bl.get(0);
        String lReturnType=bl.get(bl.size()-2);
        String lParameter=des.substring(des.indexOf('(')+1,des.indexOf(')'));
        this.fullTypeMap.putAll(findShortType(lReturnType));
        this.fullTypeMap.putAll(findShortType(lParameter));
        this.returnType=shortType(wrapPrimaryType(lReturnType),this.fullTypeMap);
        this.parameterList=shortType(lParameter,this.fullTypeMap);
        this.simpleSignature=lModifier+" "+returnType+" " +this.getNativeMethod().getName()+"("+addArgHolder(parameterList)+")";
        this.fullTypeMap.keySet().forEach(s->{
            AtomicBoolean add= new AtomicBoolean(true);
            importIgnore.forEach(i->{if(s.startsWith(i)){ add.set(false);}});
            if(add.get()) { this.importList.add(s); }
        });

    }

    private String shortType(String old,Map<String,String> rel){
        AtomicReference<String> auc=new AtomicReference<>(old);
        rel.keySet().stream().sorted((a,b)->-1*a.compareTo(b)).forEach(
                k->{
                    String v=rel.get(k);
                    auc.set(auc.get().replace(k,v));
                }
        );
        return auc.get();
    }


    private String wrapPrimaryType(String type){
        type=type.replace(" ","");
       for(String key:typeRelation.keySet()){
           if((type.startsWith(key) && type.length()>key.length() && type.substring(key.length()).startsWith("["))
                   || type.equals(key)){
               return typeRelation.get(key)+type.substring(key.length());
           }
       }
       return type;
    }

    private String addArgHolder(String params){
        int id=0,lp=0;
        char[] cs=params.toCharArray();
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<cs.length;i++){
            if(cs[i]==',' && lp==0){
                sb.append(" arg").append(id++);
            }
            if(cs[i]=='<'){
                lp--;
            }
            if(cs[i]=='>'){
                lp++;
            }
            sb.append(cs[i]);
        }
        return sb.length()==0?sb.toString():sb+" arg"+id;
    }

    private Map<String,String> findShortType(String si){
        si=si.replace(" ","");
        StringBuilder sb=new StringBuilder();
        char[] cs=si.toCharArray();
        Map<String,String> nameMap=new HashMap<>();
        for(int i=0;i<cs.length;i++){
            if(cs[i]=='<' || cs[i]=='>' || cs[i]==','){
                simpleName(nameMap,sb.toString());
                sb.delete(0,sb.length());
            }else{
                sb.append(cs[i]);
            }
        }
        simpleName(nameMap,sb.toString());
        return nameMap;
    }

    public static void simpleName(Map<String,String> map,String fullName){
        if(fullName.trim().length()==0){
            return;
        }
        String simpleName;
        int d1=fullName.lastIndexOf('.');
        if(d1==-1){
            return;
        }
        if(fullName.toCharArray()[d1-1]=='.' && fullName.toCharArray()[d1-2]=='.'){
            String temp=fullName.substring(0,d1-2);
            int d2=temp.lastIndexOf('.');
            if(d2==-1){
                return;
            }
            fullName=temp;
            simpleName=fullName.substring(d2+1);
        }else{
            simpleName=fullName.substring(d1+1);
        }
        fullName=fullName.replace("[]","");
        simpleName=simpleName.replace("[]","");
        map.put(fullName,simpleName);
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

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getParameterList() {
        return parameterList;
    }

    public void setParameterList(String parameterList) {
        this.parameterList = parameterList;
    }

    public Map<String, String> getFullTypeMap() {
        return fullTypeMap;
    }

    public void setFullTypeMap(Map<String, String> fullTypeMap) {
        this.fullTypeMap = fullTypeMap;
    }

    public List<String> getImportList() {
        return importList;
    }

    public void setImportList(List<String> importList) {
        this.importList = importList;
    }
}
