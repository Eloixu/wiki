package com.pwc.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pwc.wiki.domain.Ebook;
import com.pwc.wiki.domain.EbookExample;
import com.pwc.wiki.mapper.EbookMapper;
import com.pwc.wiki.req.EbookQueryReq;
import com.pwc.wiki.req.EbookSaveReq;
import com.pwc.wiki.resp.EbookQueryResp;
import com.pwc.wiki.resp.PageResp;
import com.pwc.wiki.util.CopyUtil;
import com.pwc.wiki.util.SnowFlake;
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

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<EbookQueryResp> list(EbookQueryReq req) {
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

//        List<EbookQueryResp> ebookQueryRespList = new ArrayList<>();
//        for(Ebook ebook:ebookList){
//            EbookQueryResp ebookResp = new EbookQueryResp();
//            BeanUtils.copyProperties(ebook,ebookResp);
//            ebookQueryRespList.add(ebookResp);
//        }

        List<EbookQueryResp> ebookQueryRespList = CopyUtil.copyList(ebookList,EbookQueryResp.class);

        PageResp<EbookQueryResp> pageResp = new PageResp();
        pageResp.setList(ebookQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());

        return pageResp ;
    }

    //保存：修改+新增
    public void save(EbookSaveReq req){
        Ebook ebook = CopyUtil.copy(req,Ebook.class);
        if(ObjectUtils.isEmpty(req.getId())){
            ebook.setId(snowFlake.nextId());
            ebookMapper.insert(ebook);
        }
        else{
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    //删除
    public void delete(Long id){
        ebookMapper.deleteByPrimaryKey(id);
    }
}
