package com.pwc.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pwc.wiki.domain.Ebook;
import com.pwc.wiki.domain.EbookExample;
import com.pwc.wiki.mapper.EbookMapper;
import com.pwc.wiki.req.EbookReq;
import com.pwc.wiki.resp.EbookResp;
import com.pwc.wiki.resp.PageResp;
import com.pwc.wiki.util.CopyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class EbookService {
    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Autowired
    private EbookMapper ebookMapper;

    public PageResp<EbookResp> list(EbookReq req) {
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())){
            //模糊查询
            criteria.andNameLike("%" + req.getName() + "%");
        }

        PageHelper.startPage(req.getPage(),req.getSize());
        List<Ebook>  ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数：{}",pageInfo.getTotal());
//        LOG.info("总页数：{}",pageInfo.getPages());

//        List<EbookResp> ebookRespList = new ArrayList<>();
//        for(Ebook ebook:ebookList){
//            EbookResp ebookResp = new EbookResp();
//            BeanUtils.copyProperties(ebook,ebookResp);
//            ebookRespList.add(ebookResp);
//        }

        List<EbookResp> ebookRespList = CopyUtil.copyList(ebookList,EbookResp.class);

        PageResp<EbookResp> pageResp = new PageResp();
        pageResp.setList(ebookRespList);
        pageResp.setTotal(pageInfo.getTotal());

        return pageResp ;
    }
}
