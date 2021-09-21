package com.pwc.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.pwc.wiki.req.UserLoginReq;
import com.pwc.wiki.req.UserQueryReq;
import com.pwc.wiki.req.UserResetPasswordReq;
import com.pwc.wiki.req.UserSaveReq;
import com.pwc.wiki.resp.CommonResp;
import com.pwc.wiki.resp.UserLoginResp;
import com.pwc.wiki.resp.UserQueryResp;
import com.pwc.wiki.resp.PageResp;
import com.pwc.wiki.service.UserService;
import com.pwc.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * Created by xuhaocheng on 05/09/2021.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SnowFlake snowFlake;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public CommonResp save(@Valid @RequestBody UserLoginReq req){
        //后端对前端传过来的密码密文进行第二次md5加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);
        //token就是一个唯一的字符串,所以可以用雪花算法来生成
        Long token = snowFlake.nextId();
        userLoginResp.setToken(token.toString());
        LOG.info("生成单点登录token：{}，并放入redis中", token);
        //以token作为key，userLoginResp作为value，时效为24小时
        redisTemplate.opsForValue().set(token, JSONObject.toJSONString(userLoginResp), 3600*24, TimeUnit.SECONDS);
//        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        resp.setContent(userLoginResp);
        return resp;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResp list(@Valid UserQueryReq req){
        CommonResp<PageResp> resp = new CommonResp<>();
        PageResp<UserQueryResp> pageResp = userService.list(req);
        resp.setContent(pageResp);
        return resp;
    }

    //只有当POST请求,并且Content-Type是application/json方式时要用到@RequestBody
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
        //后端对前端传过来的密码密文进行第二次md5加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public CommonResp save(@Valid @RequestBody UserResetPasswordReq req){
        //后端对前端传过来的密码密文进行第二次md5加密
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }
}
