package com.mzh.emock.type.bean.method;

import com.mzh.emock.util.entity.EMMethodSignature;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class EMMethodInfo {
    private String name;
    private boolean isMock;
    private EMMethodSignature methodSignature;
    private Map<String, EMMethodInvoker<Object,Object[]>> dynamicInvokers=new HashMap<>();
    private String dynamicInvokerName;

    public EMMethodInfo(Method method){
        this.name=method.getName();
        this.methodSignature=new EMMethodSignature(method);
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

    public EMMethodSignature getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(EMMethodSignature methodSignature) {
        this.methodSignature = methodSignature;
    }
}
