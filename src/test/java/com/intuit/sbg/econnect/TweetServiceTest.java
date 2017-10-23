package com.intuit.sbg.econnect;

import com.intuit.sbg.econnect.model.TweetMessage;
import com.intuit.sbg.econnect.service.FollowService;
import com.intuit.sbg.econnect.service.TweetService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * The type Tweet service test.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TweetServiceTest {
    private static Logger logger = LoggerFactory.getLogger(TweetServiceTest.class);
    /**
     * The Tweet service.
     */
    @Autowired
    TweetService tweetService;
    /**
     * The Redis template.
     */
    @Autowired
    RedisTemplate redisTemplate;
    /**
     * The Follow service.
     */
    @Autowired
    FollowService followService;

    /**
     * Test tweet message.
     */
    @Test
    public void testTweetMessage() {
        while (redisTemplate
                .opsForList()
                .size("eposts:ss12") > 0) {
            redisTemplate
                    .opsForList()
                    .rightPop("eposts:ss12");
        }
        TweetMessage msg = new TweetMessage();
        msg.setText("Feeling very lucky");
        msg.setUserId("ss12");
        tweetService.tweet(msg, "ss12");
        int size = redisTemplate
                .opsForList()
                .size("eposts:ss12")
                .intValue();
        assertEquals(size, 1);
        while (redisTemplate
                .opsForList()
                .size("eposts:ss12") > 0) {
            redisTemplate
                    .opsForList()
                    .rightPop("eposts:ss12");
        }
    }

    /**
     * Test followers.
     */
    @Test
    //sloll is the current logged in user
    public void testFollowers() {

        while (redisTemplate
                .opsForSet()
                .size("efollowing:sloll") > 0) {
            redisTemplate
                    .opsForSet()
                    .pop("efollowing:sloll");
        }
        while (redisTemplate
                .opsForSet()
                .size("efollowers:ss12") > 0) {
            redisTemplate
                    .opsForSet()
                    .pop("efollowers:ss12");
        }
        //sloll follows ss12
        followService.follow("sloll", "ss12");
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        int size = redisTemplate
                .opsForSet()
                .size("efollowing:sloll")
                .intValue();
        assertEquals(size, 1);
        size = redisTemplate
                .opsForSet()
                .size("efollowers:ss12")
                .intValue();
        assertEquals(size, 1);
        while (redisTemplate
                .opsForSet()
                .size("efollowing:sloll") > 0) {
            redisTemplate
                    .opsForSet()
                    .pop("efollowing:sloll");
        }
        while (redisTemplate
                .opsForSet()
                .size("efollowers:ss12") > 0) {
            redisTemplate
                    .opsForSet()
                    .pop("efollowers:ss12");
        }
        redisTemplate.setValueSerializer(redisTemplate.getDefaultSerializer());
    }

    /**
     * Test tweet message with followers.
     */
    @Test
    public void testTweetMessageWithFollowers() {
        try {
            while (redisTemplate
                    .opsForList()
                    .size("tweethome:sloll") > 0) {
                redisTemplate
                        .opsForList()
                        .rightPop("tweethome:sloll");
            }
            while (redisTemplate
                    .opsForSet()
                    .size("efollowers:ss12") > 0) {
                redisTemplate
                        .opsForSet()
                        .pop("efollowers:ss12");
            }
            //sloll is logged in
            //he follows ss12
            followService.follow("sloll", "ss12");
            //ss12 logs in and and tweets
            while (redisTemplate
                    .opsForList()
                    .size("eposts:ss12") > 0) {
                redisTemplate
                        .opsForList()
                        .rightPop("eposts:ss12");
            }

            TweetMessage msg = new TweetMessage();
            msg.setText("Feeling very lucky");
            msg.setUserId("ss12");
            tweetService.tweet(msg, "ss12");
            Thread.sleep(5000);
            int size = redisTemplate
                    .opsForList()
                    .size("eposts:ss12")
                    .intValue();
            assertEquals(size, 1);
            size = redisTemplate
                    .opsForList()
                    .size("tweethome:sloll")
                    .intValue();
            assertEquals(size, 1);


            TweetMessage tmsg = (TweetMessage) redisTemplate
                    .opsForList()
                    .index("tweethome:sloll", 0);
            assertEquals(tmsg.getText(), msg.getText());
            //ss12 tweets again
            msg = new TweetMessage();
            msg.setText("Happy Diwali !!");
            msg.setUserId("ss12");
            tweetService.tweet(msg, "ss12");
            Thread.sleep(5000);
            tmsg = (TweetMessage) redisTemplate
                    .opsForList()
                    .index("tweethome:sloll", 1);
            System.out.print(tmsg.getText() + " " + msg.getText());
            logger.info(tmsg.getText() + " " + msg.getText());
            assertEquals(tmsg.getText(), msg.getText());
        } catch (Exception e) {

        }
    }



}
