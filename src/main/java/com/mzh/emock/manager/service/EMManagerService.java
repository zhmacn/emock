package com.mzh.emock.manager.service;

import com.mzh.emock.core.EMCache;
import com.mzh.emock.manager.code.EMCodeTemplate;
import com.mzh.emock.manager.code.EMRTCompiler;
import com.mzh.emock.manager.po.EMInvokerCreateResult;
import com.mzh.emock.manager.po.EMObjectExchange;
import com.mzh.emock.manager.tools.ResourceTool;
import com.mzh.emock.type.EMRelatedObject;
import com.mzh.emock.type.bean.EMBeanInfo;
import com.mzh.emock.type.bean.method.EMMethodInfo;
import com.mzh.emock.type.bean.method.EMMethodInvoker;
import com.mzh.emock.type.proxy.EMProxyHolder;
import com.mzh.emock.util.EMStringUtil;
import com.mzh.emock.util.entity.EMFieldInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class EMManagerService {
    public static List<EMObjectExchange> query(String name,boolean includeBean,boolean includeProxy){
        List<EMObjectExchange> result=new ArrayList<>();
        for(EMRelatedObject rt:EMCache.EM_OBJECT_MAP.values()){
            if(EMStringUtil.isEmpty(name)
                    || rt.getOldBeanName().toUpperCase().contains(name.toUpperCase())){
                EMObjectExchange exchange=new EMObjectExchange();
                exchange.setId(rt.getId());
                exchange.setOldBeanName(rt.getOldBeanName());
                exchange.setOldBeanClazz(rt.getOldDefinition().getBeanClassName());
                exchange.setOldObject(rt.getOldBean().toString());
                exchange.setEmInfo(toRTExchange(rt,includeBean,includeProxy));
                result.add(exchange);
            }
        }
        return result;
    }
    public static String update(List<EMObjectExchange> exchanges){
        if(exchanges==null){
            return "update failed exchange is null";
        }
        StringBuilder sb=new StringBuilder();
        for(EMObjectExchange exchange:exchanges){
            EMRelatedObject relation=findRelationById(exchange.getId());
            if(relation!=null){
                for(String clzName:exchange.getEmInfo().getBeanInfo().keySet()){
                    for(Class<?> clz:relation.getEmInfo().keySet()){
                        if(clz.getName().equals(clzName)){
                            List<EMBeanInfo<?>> beanInfoList= relation.getEmInfo().get(clz);
                            List<EMObjectExchange.EMBeanExchange> exchangeList=exchange.getEmInfo().getBeanInfo().get(clzName);
                            sb.append(updateList(exchangeList,beanInfoList));
                        }
                    }
                }
            }
        }
        return sb.toString().equals("")?"success":sb.toString();
    }
    private static String updateList(List<EMObjectExchange.EMBeanExchange> exchangeList,List<EMBeanInfo<?>> emBeanInfoList){
        StringBuilder sb=new StringBuilder(256);
        for(EMObjectExchange.EMBeanExchange exchange:exchangeList){
            for(EMBeanInfo<?> emBeanInfo:emBeanInfoList){
                if(exchange.getId()==emBeanInfo.getId()){
                    sb.append(updateSingle(exchange,emBeanInfo));
                }
            }
        }
        emBeanInfoList.sort(Comparator.comparingInt(e->e.getDefinitionSource().getOrder()));
        return sb.toString();
    }

    private static String updateSingle(EMObjectExchange.EMBeanExchange exchange, EMBeanInfo<?> emBeanInfo){
        StringBuilder sb=new StringBuilder(256);
        emBeanInfo.setMocked(exchange.isMock());
        emBeanInfo.getDefinitionSource().setOrder(exchange.getOrder());
        for(EMObjectExchange.EMMethodExchange methodExchange:exchange.getMethodInfo()){
            EMMethodInfo methodInfo=emBeanInfo.getInvokeMethods().get(methodExchange.getMethodName());
            methodInfo.setMock(methodExchange.isMock());
            String updateInfo=updateDynamicInvoker(methodInfo,methodExchange);
            if(!EMStringUtil.isEmpty(updateInfo)){
                sb.append(updateInfo).append("\r\n");
            }
        }
        return sb.toString();
    }

    private static String updateDynamicInvoker(EMMethodInfo methodInfo, EMObjectExchange.EMMethodExchange methodExchange){
        String resultStr="";
        for(String dynamicMethodName:methodExchange.getDynamicInvokes().keySet()){
            EMObjectExchange.EMDynamicInvokeExchange dynamicInvokeExchange=methodExchange.getDynamicInvokes().get(dynamicMethodName);
            String sourceCode=dynamicInvokeExchange.getSrcCode();
            if(!EMStringUtil.isEmpty(sourceCode)){
                EMMethodInvoker<Object,Object[]> currInvoker=methodInfo.getDynamicInvokers().get(dynamicMethodName);
                if(currInvoker==null || !sourceCode.equals(currInvoker.getCode())) {
                    EMInvokerCreateResult result=createInvoker(sourceCode);
                    if(result.isSuccess()){
                        methodInfo.getDynamicInvokers().put(dynamicMethodName, result.getInvoker());
                    }else{
                        resultStr="update failed,name:"+dynamicMethodName+",msg:"+result.getMessage();
                    }
                }
            }
        }
        //remove
        List<String> removeKeys=new ArrayList<>();
        for(String key:methodInfo.getDynamicInvokers().keySet()){
            boolean flag=false;
            for(String eKey:methodExchange.getDynamicInvokes().keySet()){
                if(key.equals(eKey)){
                    flag=true;
                    break;
                }
            }
            if(!flag){
                removeKeys.add(key);
            }
        }
        for(String rKey:removeKeys){
            methodInfo.getDynamicInvokers().remove(rKey);
        }
        if(methodExchange.getDynamicInvokeName()!=null && methodInfo.getDynamicInvokers().size()>0
                && methodInfo.getDynamicInvokers().get(methodExchange.getDynamicInvokeName())!=null){
            methodInfo.setDynamicInvokerName(methodExchange.getDynamicInvokeName());
        }else{
            methodInfo.setDynamicInvokerName(null);
        }
        return resultStr;
    }




    private static EMInvokerCreateResult createInvoker(String srcCode){
        String[] codes=srcCode.split("_");
        if(codes.length!=2 || EMStringUtil.isEmpty(codes[1])){
            return new EMInvokerCreateResult(false,"code format error",null);
        }
        String importPart= EMStringUtil.isEmpty(codes[0])?"":new String(Base64.getDecoder().decode(codes[0]), StandardCharsets.UTF_8);
        String codePart=new String(Base64.getDecoder().decode(codes[1]),StandardCharsets.UTF_8);
        String clzName="EMDynamicInvoker_i_"+EMCache.ID_SEQUENCE.getAndIncrement();
        Map<String,String> codePlaceHolder=new HashMap<>();
        codePlaceHolder.put(EMCodeTemplate.NAME_HOLDER,clzName);
        codePlaceHolder.put(EMCodeTemplate.IMPORT_HOLDER,importPart);
        codePlaceHolder.put(EMCodeTemplate.CODE_HOLDER,codePart);
        String fullCodeStr=generateCode(EMCodeTemplate.simpleInvokeTemplatePath,codePlaceHolder);
        try {
            Map<String,byte[]> byteCode= EMRTCompiler.compile(clzName+".java",fullCodeStr);
            if(byteCode==null){
                return new EMInvokerCreateResult(false,"byteCode is null",null);
            }
            String clzFullName=byteCode.keySet().iterator().next();
            Class<?> clz=EMRTCompiler.loadClass(clzFullName,byteCode);
            if(clz==null){
                return new EMInvokerCreateResult(false,"can not load clz:"+clzFullName,null);
            }
            Constructor<?> constructor= clz.getConstructor(String.class);
            Object o=constructor.newInstance(srcCode);
            return new EMInvokerCreateResult(true,"",EMMethodInvoker.class.cast(o));
        }catch (Exception ex){
            ex.printStackTrace();
            return new EMInvokerCreateResult(false,ex.getMessage(),null);
        }
    }

    private static String generateCode(String template,Map<String,String> codePlaceHolder){
        String templateCode= ResourceTool.loadResourceAsString(template);
        if(templateCode==null){
            return null;
        }
        for(String key:codePlaceHolder.keySet()){
            templateCode=templateCode.replace(key,codePlaceHolder.get(key));
        }
        return templateCode;
    }


    private static EMRelatedObject findRelationById(long id){
        for(EMRelatedObject relation:EMCache.EM_OBJECT_MAP.values()){
            if(relation.getId()==id){
                return relation;
            }
        }
        return null;
    }

    private static EMObjectExchange.EMRTExchange toRTExchange(EMRelatedObject rt,boolean includeBean,boolean includeProxy){
        EMObjectExchange.EMRTExchange rtExchanges=new EMObjectExchange.EMRTExchange();
        if(includeBean){
            rtExchanges.setBeanInfo(toBeanExchange(rt));
        }
        if(includeProxy) {
            rtExchanges.setProxyInfo(toProxyExchange(rt));
        }
        return rtExchanges;

    }
    private static Map<String, List<EMObjectExchange.EMBeanExchange>> toBeanExchange(EMRelatedObject rt){
        Map<String, List<EMObjectExchange.EMBeanExchange>> beanExchangeMap=new HashMap<>();
        for(Class<?> clz:rt.getEmInfo().keySet()) {
            List<EMBeanInfo<?>> emBeanInfoList= rt.getEmInfo().get(clz);
            beanExchangeMap.computeIfAbsent(clz.getName(),k->new ArrayList<>());
            for(EMBeanInfo<?> emBeanInfo:emBeanInfoList) {
                EMObjectExchange.EMBeanExchange beanExchange = new EMObjectExchange.EMBeanExchange();
                beanExchange.setId(emBeanInfo.getId());
                beanExchange.setName(emBeanInfo.getDefinitionSource().getName());
                beanExchange.setOrder(emBeanInfo.getDefinitionSource().getOrder());
                beanExchange.setDefClz(emBeanInfo.getDefinitionSource().getSrcClz().getName());
                beanExchange.setDefMethod(emBeanInfo.getDefinitionSource().getSrcMethod().getName());
                beanExchange.setMock(emBeanInfo.isMocked());
                beanExchange.setMethodInfo(toMethodInfo(emBeanInfo));
                beanExchangeMap.get(clz.getName()).add(beanExchange);
            }
        }
        return beanExchangeMap;
    }

    private static Map<String,List<EMObjectExchange.EMProxyExchange>> toProxyExchange(EMRelatedObject rt){
        Map<String,List<EMObjectExchange.EMProxyExchange>> proxyExchangeMap=new HashMap<>();
        for(Class<?> clz:rt.getProxyHolder().keySet()){
            EMProxyHolder holder=rt.getProxyHolder().get(clz);
            EMObjectExchange.EMProxyExchange proxyExchange=new EMObjectExchange.EMProxyExchange();
            proxyExchange.setProxyClz(holder.getProxy().getClass().getName());
            proxyExchange.setInjectField(toStringChain(holder.getInjectField()));
            proxyExchangeMap.put(clz.getName(), Collections.singletonList(proxyExchange));
        }
        return proxyExchangeMap;
    }

    private static List<String> toStringChain(List<EMFieldInfo> fieldInfos){
        List<String> ls=new ArrayList<>();
        for(EMFieldInfo fi:fieldInfos){
            StringBuilder sb=new StringBuilder(256);
            fi.getFieldTrace().forEach(s->sb.append(" -> ").append(s));
            ls.add(sb.toString());
        }
        return ls;
    }

    private static List<EMObjectExchange.EMMethodExchange> toMethodInfo(EMBeanInfo<?> em){
        List<EMObjectExchange.EMMethodExchange> methodExchanges=new ArrayList<>();
        for(String name:em.getInvokeMethods().keySet()){
            EMMethodInfo methodInfo=em.getInvokeMethods().get(name);
            EMObjectExchange.EMMethodExchange methodExchange=new EMObjectExchange.EMMethodExchange();
            methodExchange.setMethodName(name);
            methodExchange.setMock(methodInfo.isMock());
            methodExchange.setMethodSignature(methodInfo.getMethodSignature().getSimpleSignature());
            methodExchange.setDynamicInvokeName(methodInfo.getDynamicInvokerName());
            Map<String, EMObjectExchange.EMDynamicInvokeExchange> dynamicInvokeExchanges=new HashMap<>();
            for(String invokerName:methodInfo.getDynamicInvokers().keySet()){
                EMMethodInvoker<?,?> dyInvoker=methodInfo.getDynamicInvokers().get(invokerName);
                EMObjectExchange.EMDynamicInvokeExchange mdExchange=new EMObjectExchange.EMDynamicInvokeExchange();
                mdExchange.setSrcCode(dyInvoker.getCode());
                dynamicInvokeExchanges.put(invokerName,mdExchange);
            }
            methodExchange.setDynamicInvokes(dynamicInvokeExchanges);
            methodExchanges.add(methodExchange);
        }
        return methodExchanges;
    }

}
