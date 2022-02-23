package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    //这是处理私信的接口
     //查询当前的用户列表，针对每个会话只显示一条最新的私信
    List<Message> selectConversations(int userId,int offset,int limit);
    //查询当前用户的会话数量
    int selectConversationCount(int userId);
    //查询某个会话所包含的私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);
    //查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读的私信的数量
    int selectLetterUnreadCount(int userId,String conversationId);
    //新增信息
    int insertMessage(Message message);
    //修改消息的状态
    int updateStatus(List<Integer> ids,int status);

}
