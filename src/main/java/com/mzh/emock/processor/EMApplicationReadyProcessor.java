package com.mzh.emock.processor;

import com.mzh.emock.EMConfigurationProperties;
import com.mzh.emock.core.EMSupport;
import com.mzh.emock.log.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.ResourceLoader;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class EMApplicationReadyProcessor implements ApplicationListener<ApplicationReadyEvent>, Ordered {
    private static final Logger logger= Logger.get(EMApplicationReadyProcessor.class);

    private final AbstractApplicationContext context;
    private final ResourceLoader resourceLoader;

    public EMApplicationReadyProcessor(AbstractApplicationContext context, ResourceLoader resourceLoader){
        logger.info("EMApplicationReadyProcessor loaded");
        this.context=context;
        this.resourceLoader=resourceLoader;
        logger.info("init PMockProcessor: bean:defaultProcessor,context:"+context.toString()+",resourceLoader:"+resourceLoader.toString());
    }


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Thread main=Thread.currentThread();
        new Thread(()->{
            LocalDateTime startTime= LocalDateTime.now();
            long maxWaiting= EMConfigurationProperties.WAIT_FOR_APPLICATION_READY;
            try{
                main.join(maxWaiting);
            }catch (Exception ex){
                logger.error("emock : wait for main thread complete error",ex);
            }
            long waitTimes=startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS);
            if(waitTimes>=maxWaiting){
                logger.info("emock : wait for main thread complete until "+maxWaiting+" stop mock initial");
                return;
            }
            initialMockBeans();
        },"EMThread").start();
    }
    private void initialMockBeans() {
        try {
            LocalDateTime initStart= LocalDateTime.now();
            logger.info("emock : init processor start , time:"+initStart);
            EMSupport.loadEMDefinitionSource(context, resourceLoader);
            EMSupport.createEMDefinition(context);
            EMSupport.createEMBeanIfNecessary(context);
            EMSupport.proxyAndInject(context);
            LocalDateTime initEnd=LocalDateTime.now();
            logger.info("emock : init processor complete, time: "+ initEnd
                    +", cost : "+initStart.until(initEnd,ChronoUnit.MILLIS)/1000.00+"s");
        }catch (Exception ex){
           logger.error("emock : init error",ex);
        }finally {

        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
