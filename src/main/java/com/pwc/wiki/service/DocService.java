package com.pwc.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pwc.wiki.domain.Content;
import com.pwc.wiki.domain.Doc;
import com.pwc.wiki.domain.DocExample;
import com.pwc.wiki.exception.BusinessException;
import com.pwc.wiki.exception.BusinessExceptionCode;
import com.pwc.wiki.mapper.ContentMapper;
import com.pwc.wiki.mapper.DocMapper;
import com.pwc.wiki.mapper.DocMapperCust;
import com.pwc.wiki.req.DocQueryReq;
import com.pwc.wiki.req.DocSaveReq;
import com.pwc.wiki.resp.DocQueryResp;
import com.pwc.wiki.resp.PageResp;
import com.pwc.wiki.util.CopyUtil;
import com.pwc.wiki.util.RedisUtil;
import com.pwc.wiki.util.RequestContext;
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
    private DocMapperCust docMapperCust;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private SnowFlake snowFlake;

    @Autowired
    public RedisUtil redisUtil;

    public List<DocQueryResp> all(Long ebookId) {
        DocExample docExample = new DocExample();
        docExample.createCriteria().andEbookIdEqualTo(ebookId);
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

    //查找content
    public String getContent(Long id){
        Content content = contentMapper.selectByPrimaryKey(id);
        //文档阅读数+1
        docMapperCust.increaseViewCount(id);
        return content==null? null:content.getContent();
    }

    //点赞
    public void vote(Long id){
        String ip = RequestContext.getRemoteAddr();
        // 远程IP+doc.id作为key，24小时内不能重复点赞
        if(redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip,3600*24)){
            //文档阅读数+1
            docMapperCust.increaseVoteCount(id);
        }else{
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

    }

    //保存：修改+新增
    public void save(DocSaveReq req){
        //把docSaveReq里的id等（除content）复制到doc
        Doc doc = CopyUtil.copy(req,Doc.class);
        //把docSaveReq里的id和content复制到content
        Content content = CopyUtil.copy(req,Content.class);
        if(ObjectUtils.isEmpty(req.getId())){
            doc.setId(snowFlake.nextId());
            //若ViewCount不设为0，则数据库中其值会为null，阅读过后自增功能将不起作用
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);
            content.setId(doc.getId());
            contentMapper.insert(content);
        }
        else{
            docMapper.updateByPrimaryKey(doc);
            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            //原来的doc里没有存content
            if(count==0){
                contentMapper.insert(content);
            }
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

    public void updateEbookInfo(){
        docMapperCust.updateEbookInfo();
    }
}
