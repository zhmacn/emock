package com.mzh.emock.type.bean;

import com.mzh.emock.core.EMCache;
import com.mzh.emock.type.bean.definition.EMBeanDefinitionSource;
import com.mzh.emock.type.bean.method.EMMethodInfo;
import com.mzh.emock.util.EMClassUtil;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EMBeanInfo<T>{

    private long id;
    private boolean isMocked;
    private T mockedBean;
    private EMBeanDefinitionSource<T> definitionSource;
    private Map<String, EMMethodInfo> invokeMethods=new ConcurrentHashMap<>();

    public EMBeanInfo(@NonNull T mb,
                      @NonNull EMBeanDefinitionSource<T> ds){
        this.id= EMCache.ID_SEQUENCE.getAndIncrement();
        this.isMocked= ds.isBeanEnableMock();
        this.mockedBean=mb;
        this.definitionSource=ds;
        EMClassUtil.getAllMethods(ds.getTargetClz(),m->m.getDeclaringClass()!=Object.class)
        .forEach(method->{
            EMMethodInfo methodInfo=new EMMethodInfo(method);
            methodInfo.setMock(ds.isMethodEnableMock());
            if(Arrays.stream(ds.getReverseEnabledMethods()).anyMatch(s->s.equals(method.getName()))){
                methodInfo.setMock(!methodInfo.isMock());
            }
            this.invokeMethods.put(method.getName(),methodInfo);
        });
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isMocked() {
        return isMocked;
    }

    public void setMocked(boolean mocked) {
        isMocked = mocked;
    }

    public T getMockedBean() {
        return mockedBean;
    }

    public void setMockedBean(T mockedBean) {
        this.mockedBean = mockedBean;
    }

    public EMBeanDefinitionSource<T> getDefinitionSource() {
        return definitionSource;
    }

    public void setDefinitionSource(EMBeanDefinitionSource<T> definitionSource) {
        this.definitionSource = definitionSource;
    }

    public Map<String, EMMethodInfo> getInvokeMethods() {
        return invokeMethods;
    }

    public void setInvokeMethods(Map<String, EMMethodInfo> invokeMethods) {
        this.invokeMethods = invokeMethods;
    }


}
