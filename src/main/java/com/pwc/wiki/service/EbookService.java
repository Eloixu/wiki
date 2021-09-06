package com.pwc.wiki.service;

import com.pwc.wiki.domain.Ebook;
import com.pwc.wiki.domain.EbookExample;
import com.pwc.wiki.mapper.EbookMapper;
import com.pwc.wiki.req.EbookReq;
import com.pwc.wiki.resp.CommonResp;
import com.pwc.wiki.resp.EbookResp;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EbookService {

    @Autowired
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        //模糊查询
        criteria.andNameLike("%" + req.getName() + "%");
        List<Ebook>  ebookList = ebookMapper.selectByExample(ebookExample);

        List<EbookResp> ebookRespList = new ArrayList<>();
        for(Ebook ebook:ebookList){
            EbookResp ebookResp = new EbookResp();
            BeanUtils.copyProperties(ebook,ebookResp);
            ebookRespList.add(ebookResp);
        }
        return ebookRespList;
    }
}
