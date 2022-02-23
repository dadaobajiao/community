package com.nowcoder.community.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
    //日志信息配置
    private  static  final Logger logger= (Logger) LoggerFactory.getLogger(MailClient.class);

    //注入JavaMileSender对象，这个里面只有两种类型，MimeMessage和send
    @Autowired
    private JavaMailSender mailSender;

    //注入邮箱的发送者 。是固定的
    @Value("he2jiao@sina.com")
    private String from;
    //邮箱的执行体，发给谁，标题是什么，内容是什么
    public void sendMail(String to,String subject,String content){
        try {
            //主要就是构建MimeMessage，构建MimeMessage需要用MimeMessageHelper来帮助。然后调用send方法
            MimeMessage message= mailSender.createMimeMessage();
            MimeMessageHelper helper=new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败:" + e.getMessage());
        }

    }

}
