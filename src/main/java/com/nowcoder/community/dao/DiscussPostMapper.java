package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface DiscussPostMapper {
    //查询数据库中数据，参数是，用户id  当前页的起始行，这个页面限制多少行
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在<if>里使用,则必须加别名.
    int selectDiscussPostRows(@Param("userId") int userId);//查询帖子的条数，来进行分页

    //插入帖子
    int insertDiscussPost(DiscussPost discussPost);

    //显示帖子里面的内容
    DiscussPost selectDiscussPostById(int id);

    //更新帖子当中的内容
    int updateCommentCount(int id ,int commentCount);
}
