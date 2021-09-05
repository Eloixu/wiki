package com.pwc.wiki.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xuhaocheng on 05/09/2021.
 */
@RestController
public class TestController {

    /**
    * GET, POST, PUT, DELETE
    *
    *
    **/

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String Hello(){
        return "Hello World";
    }
}
