package com.pwc.wiki.controller;

import com.pwc.wiki.domain.Test;
import com.pwc.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by xuhaocheng on 05/09/2021.
 */
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    //在启动的时候Spring会扫描所有的类，找到test.hello这个值注入到testValue
    //TEST为默认的配置值
    @Value("${test.hello:Test}")
    private String testValue;


    /**
    * GET, POST, PUT, DELETE
    *
    *
    **/

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String Hello(){
        return "Hello World!"+testValue;
    }

    @RequestMapping(value = "/test/list", method = RequestMethod.GET)
    public List<Test> list(){
        return testService.list();
    }
}
