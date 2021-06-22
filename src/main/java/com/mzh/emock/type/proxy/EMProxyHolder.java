package com.mzh.emock.type.proxy;

import com.mzh.emock.util.EMObjectMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EMProxyHolder {
    private int proxyHash;
    private Object proxy;
    private List<EMObjectMatcher.FieldInfo> injectField;

    public EMProxyHolder(Object proxy) {
        this.proxy = proxy;
        this.proxyHash=999000000+new Random().nextInt(1000000);
    }

    public int getProxyHash() {
        return proxyHash;
    }

    public Object getProxy() {
        return proxy;
    }

    public void setProxy(Object proxy) {
        this.proxy = proxy;
    }


    public List<EMObjectMatcher.FieldInfo> getInjectField() {
        return injectField;
    }

    public void setInjectField(List<EMObjectMatcher.FieldInfo> injectField) {
        this.injectField = injectField;
    }

    public void addInjectField(EMObjectMatcher.FieldInfo fieldInfo){
        if(this.injectField==null){
            injectField=new ArrayList<>();
        }
        injectField.add(fieldInfo);
    }

}
