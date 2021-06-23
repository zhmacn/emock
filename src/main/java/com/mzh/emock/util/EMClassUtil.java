package com.mzh.emock.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class EMClassUtil {

    public static void hierarchyClazz(Class<?> curr, Consumer<Class<?>> consumer) {
        while (curr != null) {
            consumer.accept(curr);
            curr = curr.getSuperclass();
        }
    }

    public static Class<?> getRawType(Class<?> srcType){
        if(srcType.isArray()){
            srcType=srcType.getComponentType();
        }
        return srcType;
    }

    public static boolean isReferenceField(Class<?> type) {
        return  !type.isEnum() && !type.isPrimitive() && type != String.class
                && type != Character.class && type != Boolean.class
                && type != Byte.class && type != Short.class && type != Integer.class && type != Long.class
                && type != Float.class && type != Double.class;
    }


    public static List<Field> getAllDeclaredFields(Class<?> clz, Function<Field,Boolean> filter) {
        List<Field> res = new ArrayList<>();
        hierarchyClazz(clz, c -> {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (filter.apply(field)) { res.add(field); }
            }
        });
        return res;
    }

    public static List<Method> getAllDeclaredMethods(Class<?> clz,Function<Method,Boolean> filter){
        List<Method> res=new ArrayList<>();
        hierarchyClazz(clz,c->{
            Method[] methods=c.getDeclaredMethods();
            for(Method method:methods){
                if(filter.apply(method)){ res.add(method);}
            }
        });
        return res;
    }

}
