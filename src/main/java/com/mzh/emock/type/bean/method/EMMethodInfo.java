package com.mzh.emock.type.bean.method;

import com.mzh.emock.EMConfigurationProperties;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EMMethodInfo {
    private String name;
    private boolean isMock;
    private Map<String, EMMethodInvoker<Object,Object[]>> dynamicInvokers=new HashMap<>();
    private Method nativeMethod;
    private String dynamicInvokerName;

    public EMMethodInfo(Method method){
        this.nativeMethod=method;
        this.name=method.getName();
        this.isMock= EMConfigurationProperties.MOCK_METHOD_ON_INIT;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, EMMethodInvoker<Object, Object[]>> getDynamicInvokers() {
        return dynamicInvokers;
    }

    public void setDynamicInvokers(Map<String, EMMethodInvoker<Object, Object[]>> dynamicInvokers) {
        this.dynamicInvokers = dynamicInvokers;
    }

    public Method getNativeMethod() {
        return nativeMethod;
    }

    public void setNativeMethod(Method nativeMethod) {
        this.nativeMethod = nativeMethod;
    }

    public String getDynamicInvokerName() {
        return dynamicInvokerName;
    }

    public void setDynamicInvokerName(String dynamicInvokerName) {
        this.dynamicInvokerName = dynamicInvokerName;
    }

    public boolean isMock() {
        return isMock;
    }

    public void setMock(boolean mock) {
        isMock = mock;
    }
}
