package com.mzh.emock.type.bean.definition;

import com.mzh.emock.type.EMBean;
import com.mzh.emock.util.EMStringUtil;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.mzh.emock.type.bean.method.EMMethodInvoker.*;

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
        deduceEMBeanAnnotationInfo();
    }
    private final Class<?> srcClz;
    private String name;
    private int order;
    private boolean beanEnableMock;
    private boolean methodEnableMock;
    private String[] reverseEnabledMethods;
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

    private void deduceEMBeanAnnotationInfo(){
        EMBean emBean=(EMBean) this.annotations.get(EMBean.class);
        if(emBean==null){
           return ;
        }
        this.beanEnableMock= emBean.beanEnableMock();
        this.methodEnableMock= emBean.methodEnableMock();
        String name=emBean.name();
        this.name= EMStringUtil.isEmpty(name)?this.srcMethod.getName():name;
        this.reverseEnabledMethods=emBean.reverseEnabledMethod();
        this.order=emBean.order();
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

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBeanEnableMock() {
        return beanEnableMock;
    }

    public void setBeanEnableMock(boolean beanEnableMock) {
        this.beanEnableMock = beanEnableMock;
    }

    public boolean isMethodEnableMock() {
        return methodEnableMock;
    }

    public void setMethodEnableMock(boolean methodEnableMock) {
        this.methodEnableMock = methodEnableMock;
    }

    public String[] getReverseEnabledMethods() {
        return reverseEnabledMethods;
    }

    public void setReverseEnabledMethods(String[] reverseEnabledMethods) {
        this.reverseEnabledMethods = reverseEnabledMethods;
    }
}
