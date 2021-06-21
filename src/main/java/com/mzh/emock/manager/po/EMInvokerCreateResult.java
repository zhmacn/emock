package com.mzh.emock.manager.po;

import com.mzh.emock.type.bean.method.EMMethodInvoker;

public class EMInvokerCreateResult {
    private boolean success;
    private String message;
    private EMMethodInvoker<Object,Object[]> invoker;

    public EMInvokerCreateResult(boolean success, String message, EMMethodInvoker<Object, Object[]> invoker) {
        this.success = success;
        this.message = message;
        this.invoker = invoker;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EMMethodInvoker<Object, Object[]> getInvoker() {
        return invoker;
    }

    public void setInvoker(EMMethodInvoker<Object, Object[]> invoker) {
        this.invoker = invoker;
    }
}
