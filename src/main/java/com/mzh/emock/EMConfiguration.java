package com.mzh.emock;

import com.mzh.emock.manager.controller.EMManagerController;
import com.mzh.emock.processor.EMAbstractProcessor;
import com.mzh.emock.processor.EMApplicationReadyProcessor;
import com.mzh.emock.processor.EMAfterPostBeanProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import com.mzh.emock.EMConfigurationProperties.*;


@Component
@ConditionalOnProperty(prefix = EMConfigurationProperties.Constant.CONFIGURATION_PREFIX,
        name = EMConfigurationProperties.Constant.ENABLED_CONFIGURATION_NAME,
        havingValue = EMConfigurationProperties.Constant.ENABLED_CONFIGURATION_VALUE)
@EnableConfigurationProperties(EMConfigurationProperties.class)
@DependsOn(EMConfigurationProperties.Constant.PROPERTIES_FILE_NAME)
public class EMConfiguration {

    //---------------------------------processor-------------------------------//

    @Bean
    @ConditionalOnProperty(prefix = Constant.CONFIGURATION_PREFIX, name = Constant.PROCESSOR_TYPE, havingValue = Constant.TYPE_AB)
    public EMAfterPostBeanProcessor emAfterPostBeanProcessor(@Autowired AbstractApplicationContext context, @Autowired ResourceLoader resourceLoader){
        return new EMAfterPostBeanProcessor(context,resourceLoader);
    }


    @Bean
    @ConditionalOnMissingBean(value=EMAbstractProcessor.class)
    public EMApplicationReadyProcessor emApplicationReadyProcessor(@Autowired AbstractApplicationContext context, @Autowired ResourceLoader resourceLoader){
        return new EMApplicationReadyProcessor(context,resourceLoader);
    }






    //----------------------------------manager-----------------------------------//

    @Bean
    @ConditionalOnProperty(prefix=Constant.CONFIGURATION_PREFIX, name = Constant.ENABLED_MANAGER_NAME, havingValue = Constant.ENABLED_CONFIGURATION_VALUE)
    public EMManagerController managerController(){
        return new EMManagerController();
    }
}
