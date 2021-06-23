package com.mzh.emock.core;

import com.mzh.emock.type.EMRelatedObject;
import com.mzh.emock.type.bean.definition.EMBeanDefinitionSource;
import com.mzh.emock.util.entity.EMObjectMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class EMCache {

    public static AtomicLong ID_SEQUENCE=new AtomicLong(0);

    public static final List<EMBeanDefinitionSource<?>> EM_DEFINITION_SOURCES = new ArrayList<>();

    public static final Map<Object, EMRelatedObject> EM_OBJECT_MAP = new EMObjectMap<>();


}
