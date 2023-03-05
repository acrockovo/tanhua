package com.itlyc.web.interceptor;

import com.itlyc.domain.db.Admin;
// 线程工具类
public class AdminHolder {
    private static final ThreadLocal<Admin> adminTl = new ThreadLocal<>();

    public static void set(Admin admin){
        adminTl.set(admin);
    }

    public static Admin get(){
        return adminTl.get();
    }

    public static void remove(){
        adminTl.remove();
    }
}
