package com.pwc.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pwc.wiki.domain.Doc;
import com.pwc.wiki.domain.DocExample;
import com.pwc.wiki.mapper.DocMapper;
import com.pwc.wiki.req.DocQueryReq;
import com.pwc.wiki.req.DocSaveReq;
import com.pwc.wiki.resp.DocQueryResp;
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
public class DocService {
    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Autowired
    private DocMapper docMapper;

    @Autowired
    private SnowFlake snowFlake;

    public List<DocQueryResp> all() {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort ASC");

        List<Doc> docList = docMapper.selectByExample(docExample);

        List<DocQueryResp> docQueryRespList = CopyUtil.copyList(docList,DocQueryResp.class);

        return docQueryRespList ;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();

        PageHelper.startPage(req.getPage(),req.getSize());
        List<Doc>  docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}",pageInfo.getTotal());
//        LOG.info("总页数：{}",pageInfo.getPages());

//        List<DocQueryResp> docQueryRespList = new ArrayList<>();
//        for(Doc doc:docList){
//            DocQueryResp docResp = new DocQueryResp();
//            BeanUtils.copyProperties(doc,docResp);
//            docQueryRespList.add(docResp);
//        }

        List<DocQueryResp> docQueryRespList = CopyUtil.copyList(docList,DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setList(docQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());

        return pageResp ;
    }

    //保存：修改+新增
    public void save(DocSaveReq req){
        Doc doc = CopyUtil.copy(req,Doc.class);
        if(ObjectUtils.isEmpty(req.getId())){
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        }
        else{
            docMapper.updateByPrimaryKey(doc);
        }
    }

    //删除
    public void delete(Long id){
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids){
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }
}
