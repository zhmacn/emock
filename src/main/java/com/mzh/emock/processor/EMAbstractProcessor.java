package com.mzh.emock.processor;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ResourceLoader;

public abstract class EMAbstractProcessor {
    protected final AbstractApplicationContext context;
    protected final ResourceLoader resourceLoader;
    protected EMAbstractProcessor(AbstractApplicationContext context,ResourceLoader resourceLoader){
        this.context=context;
        this.resourceLoader=resourceLoader;
    }
}
