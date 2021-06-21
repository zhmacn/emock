package com.mzh.emock.type.bean.definition;

import com.mzh.emock.type.EMBean;
import com.mzh.emock.util.StringUtil;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.mzh.emock.type.bean.method.EMMethodInvoker.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 用于定义mockBean的定义信息来源
 * 如在哪个类的哪个方法
 * @param <T>
 */
public class EMBeanDefinitionSource<T> {
    public EMBeanDefinitionSource(Method srcMethod,ClassLoader loader) throws ClassNotFoundException {
        this.srcMethod = srcMethod;
        this.srcClz=srcMethod.getDeclaringClass();
        String clzName=((ParameterizedType)srcMethod.getGenericReturnType()).getActualTypeArguments()[0].getTypeName();
        this.targetClz= (Class<T>) loader.loadClass(clzName);
        Annotation[] annotations=srcMethod.getAnnotations();
        for(Annotation annotation:annotations){
            this.annotations.put(annotation.annotationType(),annotation);
        }
        this.name=deduceName();
        this.order=deduceOrder();
    }
    private final Class<?> srcClz;
    private final String name;
    private int order;
    private final Class<T> targetClz;
    private EMBeanDefinition<T> beanDefinition;

    private final Method srcMethod;
    private SimpleInvoker<EMBeanDefinition<T>, ApplicationContext> methodInvoker=new SimpleInvoker<EMBeanDefinition<T>, ApplicationContext>() {
        @Override
        @SuppressWarnings("unchecked")
        public EMBeanDefinition<T> invoke(ApplicationContext args) throws InvocationTargetException, IllegalAccessException {
            return (EMBeanDefinition<T>) srcMethod.invoke(null,args);
        }
    };

    private String deduceName(){
        EMBean emBean=(EMBean) this.annotations.get(EMBean.class);
        if(emBean!=null && !StringUtil.isEmpty(emBean.name())){
            return emBean.name();
        }
        return srcMethod.getName();
    }

    private int deduceOrder(){
        Order order = (Order) this.annotations.get(Order.class);
        if (order != null) {
            return order.value();
        }
        EMBean orderN = (EMBean) this.annotations.get(EMBean.class);
        if (orderN != null) {
            return orderN.order();
        }
        return Ordered.LOWEST_PRECEDENCE;
    }


    public void createBeanDefinition(ApplicationContext context)throws Exception{
        this.beanDefinition=this.methodInvoker.invoke(context);
    }

    private Map<Class<?>, Object> annotations=new ConcurrentHashMap<>();

    public SimpleInvoker<EMBeanDefinition<T>, ApplicationContext> getMethodInvoker() {
        return methodInvoker;
    }

    public void setMethodInvoker(SimpleInvoker<EMBeanDefinition<T>, ApplicationContext> methodInvoker) {
        this.methodInvoker = methodInvoker;
    }

    public Map<Class<?>, Object> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<Class<?>, Object> annotations) {
        this.annotations = annotations;
    }

    public int getOrder(){
        return this.order;
    }

    public Class<?> getSrcClz() {
        return srcClz;
    }

    public Class<T> getTargetClz() {
        return targetClz;
    }

    public EMBeanDefinition<T> getBeanDefinition() {
        return beanDefinition;
    }

    public void setBeanDefinition(EMBeanDefinition<T> beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    public Method getSrcMethod() {
        return srcMethod;
    }

    public String getName() {
        return name;
    }

    public void setOrder(int order) {
        this.order = order;
    }


}
