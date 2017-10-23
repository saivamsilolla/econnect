package com.intuit.sbg.econnect.service;

import com.intuit.sbg.econnect.model.TweetMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The type Tweet service.
 */
@Component
public class TweetService {
    private static Logger logger = LoggerFactory.getLogger(TweetService.class);
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * Tweet  a message .
     *
     * @param message the message to be tweeted
     * @param userId  the user who is tweeting
     * @return the message which comes user
     */
    public TweetMessage tweet(TweetMessage message, String userId) {
        logger.info("User : " + userId + " tweeting message " + message.getText());
        message.setUserId(userId);
        message.setCreated_at(new Date(Calendar
                .getInstance()
                .get(Calendar.MILLISECOND)));
        //push the message for the user
        redisTemplate
                .opsForList()
                .leftPush("eposts:" + userId, message);
        //also asynchronous save to the persistent data store.
        //maintain only latest 100 messages in the cache.
        if (redisTemplate
                .opsForList()
                .size("eposts:" + userId) > 100) {
            redisTemplate
                    .opsForList()
                    .rightPop("eposts:" + userId);
        }
        //publish the message to update the actual tweet list
        logger.info("User : " + userId + " tweeting message " + message.getText() + "publishing message to followers");
        redisTemplate.convertAndSend("newtweet", message);
        return message;
    }

    public List<TweetMessage> load(String id) {
        return redisTemplate
                .opsForList()
                .range("tweethome:" + id, 0, 100);
    }
}
