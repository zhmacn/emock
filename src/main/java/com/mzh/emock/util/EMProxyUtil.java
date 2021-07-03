package com.mzh.emock.util;

import com.mzh.emock.core.EMCache;
import com.mzh.emock.core.EMHandler;
import com.mzh.emock.type.EMRelatedObject;
import com.mzh.emock.type.proxy.EMProxyHolder;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class EMProxyUtil {

    public static EMProxyHolder createProxy(Class<?> targetClz, Object oldBean) {
        EMProxyHolder cached = findCreatedProxy(targetClz, oldBean);
        if (cached != null) {
            return cached;
        }
        ClassLoader loader = EMProxyUtil.class.getClassLoader();
        Object proxy = (targetClz.isInterface() ? createInterfaceProxy(new Class<?>[]{targetClz}, oldBean, loader)
                : createClassProxy(oldBean, loader, targetClz));
        EMCache.EM_OBJECT_MAP.get(oldBean).getProxyHolder().put(targetClz, new EMProxyHolder(proxy));
        return EMCache.EM_OBJECT_MAP.get(oldBean).getProxyHolder().get(targetClz);
    }

    private static Object createInterfaceProxy(Class<?>[] interfaces, Object oldBean, ClassLoader loader) {
        if (oldBean instanceof Proxy) {
            InvocationHandler oldHandler = Proxy.getInvocationHandler(oldBean);
            return Proxy.newProxyInstance(loader, interfaces,
                    createEnhance(oldHandler, new EMHandler.EProxyHandlerEnhanceInterceptor(oldHandler, oldBean, interfaces[0]), loader));
        }
        return Proxy.newProxyInstance(loader, interfaces, new EMHandler.EInterfaceProxyInvocationHandler(oldBean, interfaces[0]));
    }


    private static Object createClassProxy(Object oldBean, ClassLoader loader, Class<?> injectClz) {
        return createEnhance(oldBean, new EMHandler.EObjectEnhanceInterceptor(oldBean, injectClz), loader);
    }

    private static EMProxyHolder findCreatedProxy(Class<?> targetClz, Object oldBean) {
        EMRelatedObject relation = EMCache.EM_OBJECT_MAP.get(oldBean);
        if (relation == null) {
            return null;
        }
        return relation.getProxyHolder().get(targetClz);
    }

    @SuppressWarnings("unchecked")
    private static <T> T createEnhance(T old, MethodInterceptor methodInterceptor, ClassLoader loader) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(old.getClass());
        enhancer.setClassLoader(loader);
        enhancer.setUseCache(false);
        enhancer.setCallback(methodInterceptor);
        Constructor<?>[] cons = old.getClass().getDeclaredConstructors();
        Constructor<?> usedCon = null;
        for (Constructor<?> con : cons) {
            if (usedCon == null) {
                usedCon = con;
                continue;
            }
            if (con.getParameterCount() < usedCon.getParameterCount()) {
                usedCon = con;
            }
        }
        Object proxy;
        assert usedCon != null;
        if (usedCon.getParameterCount() == 0) {
            proxy = enhancer.create();
        } else {
            Object[] args = new Object[usedCon.getParameterCount()];
            proxy = enhancer.create(usedCon.getParameterTypes(), args);
        }
        return (T) proxy;
    }
}
