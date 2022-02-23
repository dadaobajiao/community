package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //这个工具类是产生随机数的，和进行MD5加密的
   //产生随机字符串
    public static String generateUUID(){

        return UUID.randomUUID().toString().replace("-","");
    }
    //使用MD5加密
    public static String md5(String key){
        if(StringUtils.isBlank(key)){  //if语句里面是判断空 null 空格的不允许
            return  null;
        }
               //产生一个16进制的字母 传入参数是bytes数组
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //code 代码   msg 提示信息  map处理业务逻辑
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", 25);
        System.out.println(getJSONString(0, "ok", map));
        //{"msg":"ok","code":0,"name":"zhangsan","age":25}
    }
}
