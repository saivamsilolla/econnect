package com.intuit.sbg.econnect.rest;

import com.intuit.sbg.econnect.model.TweetMessage;
import com.intuit.sbg.econnect.service.FollowService;
import com.intuit.sbg.econnect.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Set;

/**
 * The type Employee connect service - rest service which has methods to follow, tweet .
 */
@RestController
@RequestMapping(value = "econnect")
public class EmployeeConnectService {

    @Autowired
    TweetService tweetService;
    @Autowired
    FollowService followService;

    /**
     * Follow int.
     *
     * @param id        the id
     * @param principal the principal
     * @return the int
     */
    @RequestMapping(method = RequestMethod.POST, value = "follow", produces = {
            "application/json"})
    public String follow(@RequestBody String id, Principal principal) {
        UsernamePasswordAuthenticationToken userInfo = (UsernamePasswordAuthenticationToken) principal;
        LdapUserDetailsImpl ldapDetails = (LdapUserDetailsImpl) userInfo.getPrincipal();
        String userId = ldapDetails.getUsername();
        long following = followService.follow(userId, id);
        followService
                .getRedisTemplate()
                .setValueSerializer(followService
                        .getRedisTemplate()
                        .getDefaultSerializer());
        return "You are following " + following + " Employees";
    }

    /**
     * Tweet message.
     *
     * @param message   the message
     * @param principal the principal
     * @return the message
     */
    @RequestMapping(method = RequestMethod.POST, value = "tweet", produces = {
            "application/json"})
    public TweetMessage tweet(@RequestBody TweetMessage message, Principal principal) {
        UsernamePasswordAuthenticationToken userInfo = (UsernamePasswordAuthenticationToken) principal;
        LdapUserDetailsImpl ldapDetails = (LdapUserDetailsImpl) userInfo.getPrincipal();
        String userId = ldapDetails.getUsername();

        return tweetService.tweet(message, userId);

    }


    /**
     * Load list.
     *
     * @return the list
     */
    @RequestMapping(method = RequestMethod.GET, value = "load", produces = {
            "application/json"})
    public List<TweetMessage> load(Principal principal) {
        UsernamePasswordAuthenticationToken userInfo = (UsernamePasswordAuthenticationToken) principal;
        LdapUserDetailsImpl ldapDetails = (LdapUserDetailsImpl) userInfo.getPrincipal();
        String userId = ldapDetails.getUsername();

        return tweetService.load(userId);

    }
}
