package com.pwc.wiki.controller;

import com.pwc.wiki.domain.Demo;
import com.pwc.wiki.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by xuhaocheng on 05/09/2021.
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Demo> list(){
        return demoService.list();
    }
}
