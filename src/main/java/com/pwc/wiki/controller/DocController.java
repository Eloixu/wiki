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
import java.util.List;

/**
 * Created by xuhaocheng on 05/09/2021.
 */
@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    private DocService docService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public CommonResp all(){
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        resp.setContent(docService.all());
        return resp;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResp list(@Valid DocQueryReq req){
        CommonResp<PageResp> resp = new CommonResp<>();
        PageResp<DocQueryResp> pageResp = docService.list(req);
        resp.setContent(pageResp);
        return resp;
    }

    //只有当POST请求,并且Content-Type是application/json方式时要用到@RequestBody
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResp save(@Valid @RequestBody DocSaveReq req){
        CommonResp resp = new CommonResp<>();
        docService.save(req);
        return resp;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        docService.delete(id);
        return resp;
    }
}
