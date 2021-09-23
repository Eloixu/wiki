package com.pwc.wiki.controller;

import com.pwc.wiki.req.DocQueryReq;
import com.pwc.wiki.req.DocSaveReq;
import com.pwc.wiki.resp.DocQueryResp;
import com.pwc.wiki.resp.CommonResp;
import com.pwc.wiki.resp.PageResp;
import com.pwc.wiki.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xuhaocheng on 05/09/2021.
 */
@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    private DocService docService;

    @RequestMapping(value = "/all/{ebookId}", method = RequestMethod.GET)
    public CommonResp all(@PathVariable Long ebookId){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        resp.setContent(docService.all(ebookId));
        return resp;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResp list(@Valid DocQueryReq req){
        CommonResp<PageResp> resp = new CommonResp<>();
        PageResp<DocQueryResp> pageResp = docService.list(req);
        resp.setContent(pageResp);
        return resp;
    }

    @RequestMapping(value = "/get-content", method = RequestMethod.GET)
    public CommonResp list(Long id){
        CommonResp<String> resp = new CommonResp<>();
        String content = docService.getContent(id);
        resp.setContent(content);
        return resp;
    }

    //只有当POST请求,并且Content-Type是application/json方式时要用到@RequestBody
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    //点赞
    @RequestMapping(value = "/vote/{id}", method = RequestMethod.GET)
    public CommonResp vote(@PathVariable Long id){
        CommonResp<String> resp = new CommonResp<>();
        docService.vote(id);
        return resp;
    }

    @RequestMapping(value = "/delete/{idsStr}", method = RequestMethod.DELETE)
    public CommonResp delete(@PathVariable String idsStr){
        CommonResp resp = new CommonResp<>();

        List<String> list = Arrays.asList(idsStr.split(","));
        docService.delete(list);
        return resp;
    }
}
