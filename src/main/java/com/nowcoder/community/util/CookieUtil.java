package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

//这是一个获取Cookie内部信息的工具类
public class CookieUtil {
    //从request种获取cookie name是值的key
    public static String getValue(HttpServletRequest request,String name){
        //首先要判断传过来的request和name是不是空
        if(request==null||name==null){
            throw new IllegalArgumentException("传入的值不能为空，cookieutil说的");
        }
        Cookie[] cookies=request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                //查找是否有对相应的cookie
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return  null;
    }

}
