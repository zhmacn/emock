package com.mzh.emock.processor;

import com.mzh.emock.log.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ResourceLoader;

public class EMAutoWiredProcessor extends EMAbstractProcessor implements BeanDefinitionRegistryPostProcessor {
    private final Logger logger=Logger.get(EMAutoWiredProcessor.class);

    public EMAutoWiredProcessor(AbstractApplicationContext context, ResourceLoader resourceLoader){
        super(context,resourceLoader);
        logger.info("Effective Processor: EMAutoWiredProcessor,context:"+context.toString()+",resourceLoader:"+resourceLoader.toString());
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String[] names=registry.getBeanDefinitionNames();
        for(String name:names){
            BeanDefinition definition=registry.getBeanDefinition(name);
            if(AutowiredAnnotationBeanPostProcessor.class.getName().equals(definition.getBeanClassName())){
                registry.removeBeanDefinition(name);
                RootBeanDefinition newDef=new RootBeanDefinition(MyAutoWired.class);
                registry.registerBeanDefinition(name,newDef);
            }

        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    public static class MyAutoWired extends AutowiredAnnotationBeanPostProcessor{
        @Override
        public void processInjection(Object bean) throws BeanCreationException {
            System.out.println("clz:"+bean.getClass());
            super.processInjection(bean);
        }
    }


}
