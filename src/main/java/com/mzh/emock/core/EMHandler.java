package com.mzh.emock.core;

import com.mzh.emock.type.bean.EMBeanInfo;
import com.mzh.emock.type.bean.method.EMMethodInfo;
import com.mzh.emock.type.bean.method.EMMethodInvoker;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EMHandler {

    @FunctionalInterface
    private interface DoElse<T>{
        T get() throws Throwable;
    }

    private static class ESimpleInvoker implements EMMethodInvoker.SimpleInvoker<Object, Object[]> {
        private final Object bean;
        private final Method method;

        ESimpleInvoker(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }

        @Override
        public Object invoke(Object[] args) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(bean, args);
        }
    }

    public static abstract class EInvocationHandler{
        protected final Object oldBean;
        protected final Class<?> injectClz;
        public EInvocationHandler(Object oldBean,Class<?> injectClz){
            this.oldBean=oldBean;
            this.injectClz=injectClz;
        }
        private static class MockResult{
            private boolean mock;
            private Object result;

            public boolean isMock() {
                return mock;
            }

            public MockResult setMock(boolean mock) {
                this.mock = mock;
                return this;
            }

            public Object getResult() {
                return result;
            }

            public MockResult setResult(Object result) {
                this.result = result;
                return this;
            }
        }

        protected Object tryNoProxyMethod(Object proxy, Method method, Object[] args){
            String name=method.getName();
            switch (name){
                case "hashCode":
                    return EMCache.EM_OBJECT_MAP.get(oldBean).getProxyHolder().get(injectClz).getProxyHash();
                case "toString":
                    return proxy.getClass().getName()+"@EM:+"+proxy.hashCode();
                case "equals":
                    return proxy==args[0];
            }
            return null;
        }
        protected Object doMockElse(Object proxy,Method method,Object[] args, DoElse<Object> doElse)throws Throwable{
            MockResult result = doMock(proxy, method, args);
            return result.isMock() ?result.getResult(): doElse.get();
        }

        protected MockResult doMock(Object o, Method method, Object[] args) throws Exception {
            MockResult result=new MockResult();
            List<EMBeanInfo<?>> mockBeanInfoList=null;
            if (EMCache.EM_OBJECT_MAP.get(oldBean) != null &&  EMCache.EM_OBJECT_MAP.get(oldBean).getEmInfo()!=null
                    && EMCache.EM_OBJECT_MAP.get(oldBean).getEmInfo().get(injectClz)!=null) {
                mockBeanInfoList=EMCache.EM_OBJECT_MAP.get(oldBean).getEmInfo().get(injectClz);
            }
            if(mockBeanInfoList==null || mockBeanInfoList.size()==0){
                return result.setMock(false);
            }
            for(EMBeanInfo<?> mockBeanInfo:mockBeanInfoList){
                if(mockBeanInfo.isMocked()){
                    Map<String, EMMethodInfo> invokeMethods = mockBeanInfo.getInvokeMethods();
                    EMMethodInfo methodInfo = invokeMethods.get(method.getName());
                    if(methodInfo.isMock()) {
                        if (methodInfo.getDynamicInvokerName() != null) {
                            EMMethodInvoker<Object, Object[]> dynamicInvoker = methodInfo.getDynamicInvokers().get(methodInfo.getDynamicInvokerName());
                            Object mocked=checkReturnType(dynamicInvoker.invoke(new ESimpleInvoker(oldBean, method), new ESimpleInvoker(mockBeanInfo.getMockedBean(), method), args),method);
                            return result.setResult(mocked).setMock(true);
                        }
                        Object mocked=checkReturnType(method.invoke(mockBeanInfo.getMockedBean(), args),method);
                        return result.setResult(mocked).setMock(true);
                    }
                }
            }
            return result.setMock(false);
        }
        private Object checkReturnType(Object result,Method method)throws Exception{
            if(result==null){
                return null;
            }
            Class<?> returnClz=method.getReturnType();

            if(returnClz.isAssignableFrom(result.getClass())){
                return result;
            }
            throw new Exception("em:result cast error: "+result.getClass().getSimpleName()+" to "+returnClz.getSimpleName());
        }
    }

    public static class EInterfaceProxyInvocationHandler extends EInvocationHandler implements InvocationHandler {
        public EInterfaceProxyInvocationHandler(Object oldBean,Class<?> injectClz) {
            super(oldBean,injectClz);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object n=tryNoProxyMethod(proxy,method,args);
            return n==null?doMockElse(proxy,method,args,()->method.invoke(oldBean,args)):n;
        }
    }

    public static class EObjectEnhanceInterceptor extends EInvocationHandler implements MethodInterceptor {
        public EObjectEnhanceInterceptor(Object oldBean,Class<?> injectClz) {
            super(oldBean,injectClz);
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object n=tryNoProxyMethod(proxy,method,args);
            return n==null?doMockElse(proxy,method,args,()->method.invoke(oldBean,args)):n;
        }
    }

    public static class EProxyHandlerEnhanceInterceptor extends EInvocationHandler implements MethodInterceptor {
        private final InvocationHandler oldHandler;

        public EProxyHandlerEnhanceInterceptor(InvocationHandler oldHandler, Object oldBean,Class<?> injectClz) {
            super(oldBean, injectClz);
            this.oldHandler = oldHandler;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object noProxyResult=tryNoProxyMethod(proxy,method,args);
            if(noProxyResult==null){
                if(method.getName().equals("invoke")){
                    Method rMethod=(Method)args[0];
                    Object[] rArgs= Arrays.copyOfRange(args,1,args.length-1);
                    return doMockElse(proxy,rMethod,rArgs,()->oldHandler.invoke(oldBean,rMethod,rArgs));
                }
                return method.invoke(oldHandler,args);
            }
            return noProxyResult;
        }
    }

}
