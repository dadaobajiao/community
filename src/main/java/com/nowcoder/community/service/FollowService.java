package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service//这是用来编写关注对象和统计粉丝的业务逻辑
public class FollowService  implements CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    //关注
    public void follow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //这是获取key  followeeKey关注了那些人的key  followerKey 哪些粉丝key
                String followeeKey= RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey=RedisKeyUtil.getFollowerKey(entityType, entityId);
                //创建事务
                operations.multi();
                //添加数据到redis中
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                //提交事务
                return operations.exec();
            }
        });
    }
    //取消关注
    public void unFollow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //这是获取key  followeeKey关注了那些人的key  followerKey 哪些粉丝key
                String followeeKey= RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey=RedisKeyUtil.getFollowerKey(entityType, entityId);
                //创建事务
                operations.multi();
                //从redis中删除
                operations.opsForZSet().remove(followeeKey,entityId );
                operations.opsForZSet().remove(followerKey,userId );
                //提交事务
                return operations.exec();
            }
        });}
        //查询关注的实体数量
    public long findFolloweeCount(int userId,int entityType){
        String followeeKey =RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }
    //查询粉丝的数量
    public long findFollowerCount(int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }
    //查询当前用户是否关注当前实体
 public boolean  hasFollowed(int userId,int entityType,int entityId){
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;
 }
 //查询某用户关注的人
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit){
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> targetIds=redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset+limit-1);

        if(targetIds==null){
            return null;
        }
        List<Map<String,Object>> list=new ArrayList<>();
        for(Integer targetId:targetIds){
            Map<String ,Object> map=new HashMap<>();
            User user= userService.findUserById(targetId);
            map.put("user",user);
            Double score=redisTemplate.opsForZSet().score(followeeKey,targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return  list;
    }
    // 查询某用户的粉丝
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if (targetIds == null) {
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user", user);
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }

        return list;
    }



}
