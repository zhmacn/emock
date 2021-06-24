package com.mzh.emock.manager.controller;

import com.google.gson.GsonBuilder;
import com.mzh.emock.core.EMCache;
import com.mzh.emock.manager.po.EMObjectExchange;
import com.mzh.emock.manager.service.EMManagerService;
import com.mzh.emock.manager.tools.ResourceTool;
import com.mzh.emock.type.bean.method.EMMethodInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mzh/em/manager")
public class EMManagerController {
    private static final String pageResPrefix="/static/page";
    private static final String indexPage="/index.html";

    private final Map<String,String> resourceCache=new HashMap<>();
    private final GsonBuilder builder=new GsonBuilder().serializeNulls();

    @RequestMapping(indexPage)
    public String index(){
        if(resourceCache.get(indexPage)==null){
            resourceCache.put(indexPage, ResourceTool.loadResourceAsString(pageResPrefix+indexPage));
        }
        return resourceCache.get(indexPage);
    }

    @RequestMapping(value = "/query",method = RequestMethod.POST)
    public String query(String oldBeanName,boolean includeBean,boolean includeProxy){
        return builder.create().toJson(EMManagerService.query(oldBeanName,includeBean,includeProxy));
    }


    @RequestMapping(value = "/queryMethodSignature")
    public String queryMethodSignature(int id,String method){
        List<EMMethodInfo> targetMethod=new ArrayList<>();
        EMCache.EM_OBJECT_MAP.values().forEach(
                h->h.getEmInfo().values().forEach(
                        bl->bl.stream().filter(b->b.getId()==id).collect(Collectors.toList()).forEach(
                                bf->targetMethod.addAll(bf.getInvokeMethods().values().stream().filter(m->m.getName().equals(method)).collect(Collectors.toList()))
                        )));
        if(targetMethod.size()==0){
            return "";
        }
        Method rMethod=targetMethod.get(0).getNativeMethod();
        Class<?> returnType=rMethod.getReturnType();
        //Type type=rMethod.getGenericReturnType();
        return returnType.getCanonicalName();
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public String update(@RequestBody List<EMObjectExchange> exchange){
        return EMManagerService.update(exchange);
    }
}
