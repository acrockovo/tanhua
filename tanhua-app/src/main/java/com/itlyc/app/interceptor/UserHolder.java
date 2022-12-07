package com.itlyc.app.interceptor;

import com.itlyc.domain.db.User;

public class UserHolder {
    private static final ThreadLocal<User> tl = new ThreadLocal<>();

    public static void set(User user){
        tl.set(user);
    }

    public static User get(){
        return tl.get();
    }

    public static void remove(){
        tl.remove();
    }
}
