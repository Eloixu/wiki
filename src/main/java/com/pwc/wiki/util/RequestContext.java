package com.pwc.wiki.util;

import java.io.Serializable;

public class RequestContext implements Serializable {

    //线程本地变量是一个域，在当前线程有效
    private static ThreadLocal<String> remoteAddr = new ThreadLocal<>();

    public static String getRemoteAddr() {
        return remoteAddr.get();
    }

    public static void setRemoteAddr(String remoteAddr) {
        RequestContext.remoteAddr.set(remoteAddr);
    }

}