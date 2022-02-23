package com.nowcoder.community.controller.interceptor;
//这是一个拦截器，拦截器需要实现HandeleInterceptor接口，选择性得重写里面的方法

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AlphaInterceptor implements HandlerInterceptor {
    private static final  Logger logger= LoggerFactory.getLogger(AlphaInterceptor.class);


     //这是运行在Controller得前面。这个事在映射器返回给dispachServlet后获取导适配器对象就可以执行得
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("执行前"+handler.toString());
        return true;
    }

    //这个是在Controller后执行得
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("执行后"+handler.toString());
    }

    //实在temeleteEngine后执行得
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("模板引擎执行后"+handler.toString());
    }
}
