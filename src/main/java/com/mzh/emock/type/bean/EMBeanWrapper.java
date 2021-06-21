package com.mzh.emock.type.bean;
@FunctionalInterface
public interface EMBeanWrapper<T> {
    T wrap(T t);
}
