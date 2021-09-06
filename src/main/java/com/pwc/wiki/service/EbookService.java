package com.pwc.wiki.service;

import com.pwc.wiki.domain.Ebook;
import com.pwc.wiki.mapper.EbookMapper;
import com.pwc.wiki.resp.CommonResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EbookService {

    @Autowired
    private EbookMapper ebookMapper;

    public CommonResp list() {
        List<Ebook> list = ebookMapper.selectByExample(null);
        CommonResp<List<Ebook>> commonResp = new CommonResp();
        commonResp.setContent(list);
        return commonResp;
    }
}
