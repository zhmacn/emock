package com.mzh.emock.util;

import com.mzh.emock.util.entity.EMFieldInfo;
import com.mzh.emock.util.entity.EMObjectMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EMObjectUtil {
    private static final int initSize=2000;
    private static final Set<Object> hasRead=new HashSet<>(initSize);
    private static Object currentTarget=null;
    private static final List<Class<?>> excludeClz=Arrays.asList(
            Class.class, Constructor.class,Method.class,Field.class,
            Type.class, BigDecimal.class, BigInteger.class, AtomicLong.class, AtomicInteger.class);

    private final Map<Object,List<EMFieldInfo>> holdingObject=new EMObjectMap<>();
    private boolean hasRead(Object o){
        return hasRead.contains(o);
    }
    private void addRead(Object o){
        hasRead.add(o);
    }

    private EMObjectUtil() { }

    public static Map<Object,List<EMFieldInfo>> match(Object src, Object target) {
        if(target!=currentTarget){
            System.out.println("em-matcher: handle object : "+target);
            hasRead.clear();
            currentTarget=target;
        }
        EMObjectUtil result = new EMObjectUtil();
        result.getAllDeclaredFieldsHierarchy(src, result.holdingObject, target,new ArrayList<String>(){{add(src.getClass().getName());}});
        return result.holdingObject;
    }

    private void getAllDeclaredFieldsHierarchy(Object src, Map<Object, List<EMFieldInfo>> holdingObject, Object target,List<String> trace) {
        if (src == null || !isIncludeClazz(src.getClass()) ||  hasRead(src)) {
            return;
        }
        try {
            addRead(src);
            List<Field> fields = EMClassUtil.getAllDeclaredFields(src.getClass(),this::isIncludeField);
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(src);
                if (value == null) {
                    continue;
                }
                List<String> newTrace=createTrace(trace,field,value,-1);
                if (field.getType().isArray() && isIncludeField(field)) {
                    findInArray((Object[]) value, holdingObject, target,newTrace);
                    continue;
                }
                if (value == target) {
                    if (holdingObject.get(src) == null)
                        holdingObject.computeIfAbsent(src, k -> new ArrayList<>());
                    holdingObject.get(src).add(new EMFieldInfo(field,newTrace));
                }
                getAllDeclaredFieldsHierarchy(value, holdingObject, target,newTrace);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void findInArray(Object[] src, Map<Object, List<EMFieldInfo>> holdingObject, Object target,List<String> trace) {
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
            if (value.getClass().isArray() && isIncludeClazz(value.getClass())) {
                findInArray((Object[]) value, holdingObject, target,newTrace);
                continue;
            }
            if (value == target) {
                if (holdingObject.get(src) == null)
                    holdingObject.computeIfAbsent(src, k -> new ArrayList<>());
                holdingObject.get(src).add(new EMFieldInfo(i,newTrace));
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

    private boolean isIncludeField(Field srcField){
        Class<?> type=EMClassUtil.getRawType(srcField.getType());
        return EMClassUtil.isReferenceField(type) && !excludeClz.contains(type);
    }
    private boolean isIncludeClazz(Class<?> srcType){
        Class<?> type=EMClassUtil.getRawType(srcType);
        return EMClassUtil.isReferenceField(type) && !excludeClz.contains(type);
    }

}
