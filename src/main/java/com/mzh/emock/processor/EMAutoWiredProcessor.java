package com.mzh.emock.processor;

import com.mzh.emock.core.EMSupport;
import com.mzh.emock.log.Logger;
import com.mzh.emock.util.EMObjectMatcher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ResourceLoader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class EMAutoWiredProcessor extends EMAbstractProcessor implements BeanDefinitionRegistryPostProcessor {
    private final Logger logger=Logger.get(EMAutoWiredProcessor.class);

    public EMAutoWiredProcessor(AbstractApplicationContext context, ResourceLoader resourceLoader){
        super(context,resourceLoader);
        logger.info("Effective Processor: EMAutoWiredProcessor,context:"+context.toString()+",resourceLoader:"+resourceLoader.toString());
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
        AutowiredAnnotationBeanPostProcessor processor=(AutowiredAnnotationBeanPostProcessor)factory.getBean(AutowiredAnnotationBeanPostProcessor.class);
        Map<Object, List<EMObjectMatcher.FieldInfo>> holders= EMObjectMatcher.match(factory,processor);
        try {
            for (Object holder : holders.keySet()) {
                List<EMObjectMatcher.FieldInfo> fieldInfoList = holders.get(holder);
                for (EMObjectMatcher.FieldInfo fieldInfo : fieldInfoList) {
                    EMSupport.doInject(fieldInfo, holder, createProxy(processor));
                }
            }
        }catch (Exception ex){
            throw new BeanCreationException("aax");
        }

    }

    private AutowiredAnnotationBeanPostProcessor createProxy(AutowiredAnnotationBeanPostProcessor processor){
        Enhancer enhancer=new Enhancer();
        enhancer.setSuperclass(AutowiredAnnotationBeanPostProcessor.class);
        enhancer.setCallback(new MyMethodInterceptor(processor));
        return (AutowiredAnnotationBeanPostProcessor) enhancer.create();
    }

    private static class MyMethodInterceptor implements MethodInterceptor{
        private final AutowiredAnnotationBeanPostProcessor nativeProcessor;

        public MyMethodInterceptor(AutowiredAnnotationBeanPostProcessor processor) {
            this.nativeProcessor=processor;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            System.out.println("processor invoker-->method:"+method.getName()+",args:"+objects);
            return method.invoke(nativeProcessor,objects);
        }
    }


}
