package com.mzh.emock.type;

import com.mzh.emock.core.EMCache;
import com.mzh.emock.type.bean.EMBeanInfo;
import com.mzh.emock.type.proxy.EMProxyHolder;
import com.mzh.emock.util.EMObjectMap;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.List;
import java.util.Map;

public class EMRelatedObject {
    private final long id;
    private final Object oldBean;
    private final String oldBeanName;
    private BeanDefinition oldDefinition;
    private Map<Class<?>,List<EMBeanInfo<?>>> emInfo=new EMObjectMap<>();
    private Map<Class<?>,EMProxyHolder> proxyHolder=new EMObjectMap<>();

    public EMRelatedObject(String oldBeanName,Object oldBean){
        this.id= EMCache.idSequence.getAndIncrement();
        this.oldBean=oldBean;
        this.oldBeanName=oldBeanName;
    }

    public BeanDefinition getOldDefinition() {
        return oldDefinition;
    }

    public void setOldDefinition(BeanDefinition oldDefinition) {
        this.oldDefinition = oldDefinition;
    }

    public Map<Class<?>, List<EMBeanInfo<?>>> getEmInfo() {
        return emInfo;
    }

    public void setEmInfo(Map<Class<?>, List<EMBeanInfo<?>>> emInfo) {
        this.emInfo = emInfo;
    }

    public Map<Class<?>, EMProxyHolder> getProxyHolder() {
        return proxyHolder;
    }

    public void setProxyHolder(Map<Class<?>, EMProxyHolder> proxyHolder) {
        this.proxyHolder = proxyHolder;
    }

    public Object getOldBean() {
        return oldBean;
    }

    public long getId(){
        return id;
    }

    public String getOldBeanName() {
        return oldBeanName;
    }
}
