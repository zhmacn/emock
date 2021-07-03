package com.mzh.emock.processor;

import com.mzh.emock.core.EMCache;
import com.mzh.emock.core.EMSupport;
import com.mzh.emock.log.Logger;
import com.mzh.emock.type.bean.EMBeanInfo;
import com.mzh.emock.util.EMClassUtil;
import com.mzh.emock.util.EMProxyUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ResourceLoader;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

public class EMAfterPostBeanProcessor extends EMAbstractProcessor implements BeanPostProcessor {
    private final Logger logger=Logger.get(EMAfterPostBeanProcessor.class);

    public EMAfterPostBeanProcessor(AbstractApplicationContext context, ResourceLoader resourceLoader){
        super(context,resourceLoader);
        logger.info("Effective Processor: EMAfterPostBeanProcessor,context:"+context.toString()+",resourceLoader:"+resourceLoader.toString());
        initMockResources(context,resourceLoader);
    }

    private void initMockResources(AbstractApplicationContext context,ResourceLoader loader){
        try{
            EMSupport.loadEMDefinitionSource(context,loader);
            EMSupport.createEMDefinition(context);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        BeanDefinition def=this.context.getBeanFactory().getBeanDefinition(beanName);
        if(EMSupport.createEMBeanIfNecessary(beanName,bean,def)){
            Set<Class<?>> tClz= EMCache.EM_OBJECT_MAP.get(bean).getEmInfo().keySet();
            Class<?> t1=null;
            for(Class<?> tt: tClz){
                if(t1==null)
                    t1=tt;
                if(t1.isAssignableFrom(tt)){
                    t1=tt;
                }
            }
            return EMProxyUtil.createProxy(t1,bean).getProxy();
        }
        return bean;
    }


}
