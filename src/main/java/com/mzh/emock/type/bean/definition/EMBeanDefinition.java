package com.mzh.emock.type.bean.definition;

import com.mzh.emock.type.bean.EMBeanWrapper;
import org.springframework.lang.NonNull;
import org.springframework.util.PatternMatchUtils;

public class EMBeanDefinition<T> {
    private Class<T> classMatcher;
    private String nameMatcher;
    private EMBeanWrapper<T> wrapper;

    public boolean isMatch(String beanName,Object oldBean){
        return (nameMatcher == null || PatternMatchUtils.simpleMatch(nameMatcher, beanName))
                && classMatcher.isAssignableFrom(oldBean.getClass());
    }

    public EMBeanDefinition(Class<T> mockedClass, EMBeanWrapper<T> wrapper){
        initial(mockedClass,null,wrapper);
    }
    public EMBeanDefinition(Class<T> classMatcher, @NonNull String nameMatcher, EMBeanWrapper<T> beanWrapper){
        initial(classMatcher,nameMatcher,beanWrapper);
    }
    private void initial(Class<T> mockedClass, String nameMatcher, EMBeanWrapper<T> beanWrapper){
        this.nameMatcher=nameMatcher;
        this.classMatcher=mockedClass;
        this.wrapper=beanWrapper;
    }

    public Class<T> getClassMatcher() {
        return classMatcher;
    }

    public void setClassMatcher(Class<T> classMatcher) {
        this.classMatcher = classMatcher;
    }

    public String getNameMatcher() {
        return nameMatcher;
    }

    public void setNameMatcher(String nameMatcher) {
        this.nameMatcher = nameMatcher;
    }


    public EMBeanWrapper<T> getWrapper() {
        return wrapper;
    }

    public void setWrapper(EMBeanWrapper<T> wrapper) {
        this.wrapper = wrapper;
    }
}
