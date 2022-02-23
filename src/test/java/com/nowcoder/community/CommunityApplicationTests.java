package com.nowcoder.community;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    //这是把容器环境引入测试类中，以方便测试类可以测试响应代码
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @Test
    void contextLoads() {
        //org.springframework.web.context.support.GenericWebApplicationContext@1c33c17b
        System.out.println(applicationContext);
        //通过容器获取dao的对象，并调用方法
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        System.out.println(alphaDao.select());
    }



     @Test//测试bean默认只能加载一次
     public void testBeanManagement() {
         AlphaService alphaService = applicationContext.getBean(AlphaService.class);
         System.out.println(alphaService);

         alphaService = applicationContext.getBean(AlphaService.class);
         System.out.println(alphaService);
     }
}
