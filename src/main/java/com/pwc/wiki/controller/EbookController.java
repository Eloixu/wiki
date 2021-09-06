package com.pwc.wiki.controller;

import com.pwc.wiki.domain.Ebook;
import com.pwc.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by xuhaocheng on 05/09/2021.
 */
@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Autowired
    private EbookService ebookService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Ebook> list(){
        return ebookService.list();
    }
}
