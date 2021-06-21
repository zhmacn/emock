package com.mzh.emock.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EMObjectMatcher {
    private static final int initSize=2000;
    private static Object[] hasRead = new Object[initSize];
    private static int curr=0;
    private static Object currentTarget=null;
    private static final List<Class<?>> excludeClz=Arrays.asList(
            Class.class, Constructor.class
            ,Method.class,Field.class
    , Type.class, BigDecimal.class, BigInteger.class, AtomicLong.class, AtomicInteger.class);

    private final Map<Object,List<FieldInfo>> holdingObject=new EMObjectMap<>();
    private boolean hasRead(Object o){
        for(int i=0;i<curr;i++){
            if(o==hasRead[i]){
                return true;
            }
        }
        return false;
    }
    private void addRead(Object o){
        if(curr==hasRead.length){
            hasRead=Arrays.copyOf(hasRead,hasRead.length*2);
        }
        hasRead[curr]=o;
        curr++;
    }

    private EMObjectMatcher() {
    }

    public static class FieldInfo {
        public FieldInfo(int index,List<String> trace) {
            this.index = index;
            this.isArrayIndex = true;
            this.fieldTrace=trace;
        }

        public FieldInfo(Field field,List<String> trace) {
            this.nativeField = field;
            this.isArrayIndex = false;
            this.fieldTrace=trace;
        }

        private final boolean isArrayIndex;
        private Field nativeField;
        private int index;
        List<String> fieldTrace;

        public boolean isArrayIndex() {
            return isArrayIndex;
        }

        public Field getNativeField() {
            return nativeField;
        }

        public int getIndex() {
            return index;
        }

        public List<String> getFieldTrace() {
            return fieldTrace;
        }
    }

    public static Map<Object,List<FieldInfo>> match(Object src, Object target) {
        if(target!=currentTarget){
            System.out.println("em-matcher: handle object : "+target);
            hasRead=new Object[initSize];
            curr=0;
            currentTarget=target;
        }
        EMObjectMatcher result = new EMObjectMatcher();
        result.getAllDeclaredFieldsHierarchy(src, result.holdingObject, target,new ArrayList<String>(){{add(src.getClass().getName());}});
        return result.holdingObject;
    }


    private void getAllDeclaredFieldsHierarchy(Object src, Map<Object, List<FieldInfo>> holdingObject, Object target,List<String> trace) {
        if (src == null || !isIncludeField(src.getClass()) ||  hasRead(src)) {
            return;
        }
        try {
            addRead(src);
            List<Field> fields = getAllDeclaredFields(src.getClass());
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(src);
                if (value == null) {
                    continue;
                }
                List<String> newTrace=createTrace(trace,field,value,-1);
                if (field.getType().isArray() && isIncludeField(field.getType())) {
                    findInArray((Object[]) value, holdingObject, target,newTrace);
                    continue;
                }
                if (value == target) {
                    if (holdingObject.get(src) == null)
                        holdingObject.computeIfAbsent(src, k -> new ArrayList<>());
                    holdingObject.get(src).add(new FieldInfo(field,newTrace));
                }
                getAllDeclaredFieldsHierarchy(value, holdingObject, target,newTrace);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void findInArray(Object[] src, Map<Object, List<FieldInfo>> holdingObject, Object target,List<String> trace) {
        if (src == null || hasRead(src)) {
            return;
        }
        addRead(src);
        for (int i = 0; i < src.length; i++) {
            Object value = src[i];
            if (value == null) {
                continue;
            }
            List<String> newTrace=createTrace(trace,null,value,i);
            if (value.getClass().isArray() && isIncludeField(value.getClass())) {
                findInArray((Object[]) value, holdingObject, target,newTrace);
                continue;
            }
            if (value == target) {
                if (holdingObject.get(src) == null)
                    holdingObject.computeIfAbsent(src, k -> new ArrayList<>());
                holdingObject.get(src).add(new FieldInfo(i,newTrace));
            }
            getAllDeclaredFieldsHierarchy(value, holdingObject, target,newTrace);
        }
    }

    private List<String> createTrace(List<String> old,Field field,Object fieldValue,int index){
        List<String> newTrace=new ArrayList<>(old);
        if(field!=null) {
            newTrace.add(field.getType().getSimpleName() +"("+ fieldValue.getClass().getSimpleName()+") : "+ field.getName());
        }else{
            newTrace.add(":"+index);
        }
        return newTrace;
    }

    private Class<?> getRawType(Class<?> srcType){
        if(srcType.isArray()){
            srcType=srcType.getComponentType();
        }
        return srcType;
    }

    private boolean isReferenceField(Class<?> type) {
        return  !type.isEnum() && !type.isPrimitive() && type != String.class
                && type != Character.class && type != Boolean.class
                && type != Byte.class && type != Short.class && type != Integer.class && type != Long.class
                && type != Float.class && type != Double.class;
    }

    private boolean isIncludeField(Class<?> srcType){
        Class<?> type=getRawType(srcType);
        return isReferenceField(type) && !excludeClz.contains(type);
    }

    private List<Field> getAllDeclaredFields(Class<?> clz) {
        List<Field> res = new ArrayList<>();
        EMUtil.optWithParent(clz, c -> {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (isIncludeField(field.getType())) {
                    res.add(field);
                }
            }
        });
        return res;
    }

}
