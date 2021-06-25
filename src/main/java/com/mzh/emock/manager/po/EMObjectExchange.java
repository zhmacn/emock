package com.mzh.emock.manager.po;

import java.security.PrivateKey;
import java.util.List;
import java.util.Map;

public class EMObjectExchange {
    private long id;
    private String oldObject;
    private String oldBeanName;
    private String oldBeanClazz;
    private EMRTExchange emInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOldObject() {
        return oldObject;
    }

    public void setOldObject(String oldObject) {
        this.oldObject = oldObject;
    }

    public String getOldBeanName() {
        return oldBeanName;
    }

    public void setOldBeanName(String oldBeanName) {
        this.oldBeanName = oldBeanName;
    }

    public String getOldBeanClazz() {
        return oldBeanClazz;
    }

    public void setOldBeanClazz(String oldBeanClazz) {
        this.oldBeanClazz = oldBeanClazz;
    }

    public EMRTExchange getEmInfo() {
        return emInfo;
    }

    public void setEmInfo(EMRTExchange emInfo) {
        this.emInfo = emInfo;
    }

    public static class EMRTExchange{
        private Map<String,List<EMBeanExchange>> beanInfo;
        private Map<String,List<EMProxyExchange>> proxyInfo;

        public Map<String, List<EMBeanExchange>> getBeanInfo() {
            return beanInfo;
        }

        public void setBeanInfo(Map<String, List<EMBeanExchange>> beanInfo) {
            this.beanInfo = beanInfo;
        }

        public Map<String, List<EMProxyExchange>> getProxyInfo() {
            return proxyInfo;
        }

        public void setProxyInfo(Map<String, List<EMProxyExchange>> proxyInfo) {
            this.proxyInfo = proxyInfo;
        }
    }
    public static class EMBeanExchange{
        private long id;
        private int order;
        private boolean mock;
        private String name;
        private String defClz;
        private String defMethod;
        private List<EMMethodExchange> methodInfo;

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public boolean isMock() {
            return mock;
        }

        public void setMock(boolean mock) {
            this.mock = mock;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDefClz() {
            return defClz;
        }

        public void setDefClz(String defClz) {
            this.defClz = defClz;
        }

        public String getDefMethod() {
            return defMethod;
        }

        public void setDefMethod(String defMethod) {
            this.defMethod = defMethod;
        }

        public List<EMMethodExchange> getMethodInfo() {
            return methodInfo;
        }

        public void setMethodInfo(List<EMMethodExchange> methodInfo) {
            this.methodInfo = methodInfo;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    public static class EMMethodSignatureExchange{
        private long beanId;
        private String methodName;
        private String returnType;
        private String parameterList;
        private String simpleSignature;
        private String importCode;

        public long getBeanId() {
            return beanId;
        }

        public void setBeanId(long beanId) {
            this.beanId = beanId;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        public String getParameterList() {
            return parameterList;
        }

        public void setParameterList(String parameterList) {
            this.parameterList = parameterList;
        }

        public String getSimpleSignature() {
            return simpleSignature;
        }

        public void setSimpleSignature(String simpleSignature) {
            this.simpleSignature = simpleSignature;
        }

        public String getImportCode() {
            return importCode;
        }

        public void setImportCode(String importCode) {
            this.importCode = importCode;
        }
    }

    public static class EMMethodExchange{
        private String methodName;
        private boolean mock;
        private String dynamicInvokeName;
        private Map<String,EMDynamicInvokeExchange> dynamicInvokes;

        public boolean isMock() {
            return mock;
        }

        public void setMock(boolean mock) {
            this.mock = mock;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getDynamicInvokeName() {
            return dynamicInvokeName;
        }

        public void setDynamicInvokeName(String dynamicInvokeName) {
            this.dynamicInvokeName = dynamicInvokeName;
        }

        public Map<String, EMDynamicInvokeExchange> getDynamicInvokes() {
            return dynamicInvokes;
        }

        public void setDynamicInvokes(Map<String, EMDynamicInvokeExchange> dynamicInvokes) {
            this.dynamicInvokes = dynamicInvokes;
        }
    }
    public static class EMDynamicInvokeExchange{
        private String srcCode;
        private String addition;

        public String getSrcCode() {
            return srcCode;
        }

        public void setSrcCode(String srcCode) {
            this.srcCode = srcCode;
        }

        public String getAddition() {
            return addition;
        }

        public void setAddition(String addition) {
            this.addition = addition;
        }
    }

    public static class EMProxyExchange{
        private String proxyClz;
        private List<String> injectField;

        public String getProxyClz() {
            return proxyClz;
        }

        public void setProxyClz(String proxyClz) {
            this.proxyClz = proxyClz;
        }

        public List<String> getInjectField() {
            return injectField;
        }

        public void setInjectField(List<String> injectField) {
            this.injectField = injectField;
        }
    }
}
