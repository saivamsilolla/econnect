package com.intuit.sbg.econnect.service;

import com.intuit.sbg.econnect.model.TweetMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Set;

public class FollowMessageSubscriber implements MessageListener {
    private static Logger logger = LoggerFactory.getLogger(FollowMessageSubscriber.class);

    public FollowMessageSubscriber(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    RedisTemplate redisTemplate;
    public static int maxSize = 100;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
             logger.info("Processing new message");
            redisTemplate.setValueSerializer( new StringRedisSerializer());
            ByteArrayInputStream in = new ByteArrayInputStream(message.getBody());
            ObjectInputStream is = new ObjectInputStream(in);
            TweetMessage tweet = (TweetMessage) is.readObject();
            Set<String> followers = redisTemplate
                    .opsForSet()
                    .members("efollowers:" + tweet.getUserId());
            followers
                    .parallelStream()
                    .forEach(follower -> {
                        if (redisTemplate
                                .opsForList()
                                .size("tweethome:" + follower) > maxSize) {
                            logger.info("popping as the size of list is greter than  " + maxSize);
                            redisTemplate
                                    .opsForList()
                                    .rightPop("tweethome:" + follower);
                        }
                        redisTemplate.setValueSerializer( redisTemplate.getDefaultSerializer());
                        redisTemplate
                                .opsForList()
                                .leftPush
                                        ("tweethome:" + follower, tweet);
                    });

        } catch (Exception e) {
            logger.info("Exception while processing the message");
        }
    }


}
