package com.intuit.sbg.econnect.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * The type Follow service.
 */
@Component
public class FollowService {
    private static Logger logger = LoggerFactory.getLogger(FollowService.class);
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * Follow a specific user.
     *
     * @param currentUserId the current user id who wants to follow
     * @param userId        the user id of the user being followed
     * @return the set
     */
    public long follow(String currentUserId, String userId) {

        logger.info("User : " + currentUserId + " following " + userId);
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate
                .opsForSet()
                .add("efollowing:" + currentUserId, userId);
       redisTemplate
                .opsForSet()
                .add("efollowers:" + userId, currentUserId);

        return redisTemplate
                .opsForSet()
               .size("efollowing:" + currentUserId);
        }

    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }
}
