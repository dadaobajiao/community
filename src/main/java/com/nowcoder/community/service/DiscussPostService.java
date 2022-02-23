package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
@Service
public class DiscussPostService {

    //注入dao层的对象
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;


    //查询用户的信息
    /*这里要注意，DiscussPost中的useid是use中的外键，但是我们查询的时候是想要显示名字的
    * 有两种操作    第一种直接在编写sql语句的时候关联编写  第二种单独查询use然后整合  后面会争对redis有好处*/
    public List<DiscussPost> findDiscussPosts(int useId,int offset,int limit){
        return discussPostMapper.selectDiscussPosts(useId,offset,limit);
    }

    //查询行数
    public int findDiscussPostRows(int useId){
        return discussPostMapper.selectDiscussPostRows(useId);
    }

    //发布帖子
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 转义HTML标记 Spring中自带的
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    //显示帖子里面的内容
    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    //更新评论数量
    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }


}
