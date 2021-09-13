package com.pwc.wiki.controller;

import com.pwc.wiki.req.EbookQueryReq;
import com.pwc.wiki.req.EbookSaveReq;
import com.pwc.wiki.resp.CommonResp;
import com.pwc.wiki.resp.EbookQueryResp;
import com.pwc.wiki.resp.PageResp;
import com.pwc.wiki.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by xuhaocheng on 05/09/2021.
 */
@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Autowired
    private EbookService ebookService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResp list(@Valid EbookQueryReq req){
        CommonResp<PageResp> resp = new CommonResp<>();
        PageResp<EbookQueryResp> pageResp = ebookService.list(req);
        resp.setContent(pageResp);
        return resp;
    }

    //只有当POST请求,并且Content-Type是application/json方式时要用到@RequestBody
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResp save(@Valid @RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public CommonResp delete(@PathVariable Long id){
        CommonResp resp = new CommonResp<>();
        ebookService.delete(id);
        return resp;
    }
}
