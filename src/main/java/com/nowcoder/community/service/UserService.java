package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import jdk.nashorn.internal.ir.Block;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UserService  implements CommunityConstant {
//处理外键用的
    @Autowired
    private UserMapper userMapper;
    //注入发送邮件的配置类
    @Autowired
    private MailClient mailClient;
    //注入模板引擎
    @Autowired
    private TemplateEngine templateEngine;
    //注入LoinTicketMapper
     // @Autowired
    //private LoginTicketMapper loginTicketMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    //引入 域名，和项目路径
    @Value("${community.path.domain}")
    private String domain;



    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        //        return userMapper.selectById(id);
        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;}

    //处理账号注册的业务逻辑
    public Map<String,Object> register(User user){
        Map<String,Object> map=new HashMap<>();

        //首先是判断输入的格式是否正确
        //对空值进行处理
        if(user==null) throw  new IllegalArgumentException("参数不能为空");
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空");
            return  map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空");
            return  map;
        }
        //验证邮箱是否已经在数据库中存在
       User u=userMapper.selectByEmail(user.getEmail());
        if(u!=null){
            map.put("emailMsg","邮箱已经被注册");
            return map;
        }
        //检查用户是否在数据库中
        u=userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该用户已经被注册");
            return map;
        }

        //注册用户成功就是吧用户存入数据库里，里面有密码，账号，激活码
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));//获取随机生成的字符串五个
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));//把密码用MD5加密
        user.setType(0);//普通用户
        user.setStatus(0);//未激活
        //设置随机生成码
        user.setActivationCode(CommunityUtil.generateUUID());
       user.setHeaderUrl(String.format("http://image/s.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
       user.setCreateTime(new Date());
       userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        //把context.注入模板引擎
        String content = templateEngine.process("/mail/activation", context);
        //把邮件发给注册的用户
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    //处理对激活状态得逻辑判断，激活成功，激活不成功，激活重复
    public int activation(int userId,String code){
        //先更具传经来的id查询到用户
        User user=userMapper.selectById(userId);
        if(1==user.getStatus()){
            //重复
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            //注册成功
            userMapper.updateStatus(userId,1);//更改这个用户在数据库中的状态
            clearCache(userId);//清空之前的缓存数据
            return ACTIVATION_SUCCESS;
        }else {
            //激活失败
            return ACTIVATION_FAILURE;
        }

    }

    //处理对登录账号密码的验证。不同的情况对应不同的访问逻辑
      //账号密码，生存时间
     public Map<String,Object> login(String username, String password, long expiredSeconds){
        Map<String ,Object> map=new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return  map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return  map;
        }
         // 验证账号
         User user = userMapper.selectByName(username);
         if (user == null) {
             map.put("usernameMsg", "该账号不存在!");
             return map;
         }

         // 验证状态
         if (user.getStatus() == 0) {
             map.put("usernameMsg", "该账号未激活!");
             return map;
         }

         // 验证密码
         //这里的密码是在申请的时候就会存入数据库中的，不过数据库中存储的是经过加密的，
         password = CommunityUtil.md5(password + user.getSalt());
         if (!user.getPassword().equals(password)) {
             map.put("passwordMsg", "密码不正确!");
             return map;
         }
         // 生成登录凭证
         LoginTicket loginTicket = new LoginTicket();
         loginTicket.setUserId(user.getId());
         loginTicket.setTicket(CommunityUtil.generateUUID());
         loginTicket.setStatus(0);
         loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);
          //把登录凭证放入了redis中
         String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
         redisTemplate.opsForValue().set(redisKey, loginTicket);

         map.put("ticket", loginTicket.getTicket());
         return map;
     }

    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl) {
//        return userMapper.updateHeader(userId, headerUrl);
        int rows = userMapper.updateHeader(userId, headerUrl);
        clearCache(userId);
        return rows;
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }

    // 1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    // 2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 3.数据变更时清除缓存数据
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }



}
