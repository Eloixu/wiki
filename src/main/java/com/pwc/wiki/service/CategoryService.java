package com.pwc.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pwc.wiki.domain.Category;
import com.pwc.wiki.domain.CategoryExample;
import com.pwc.wiki.mapper.CategoryMapper;
import com.pwc.wiki.req.CategoryQueryReq;
import com.pwc.wiki.req.CategorySaveReq;
import com.pwc.wiki.resp.CategoryQueryResp;
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
public class CategoryService {
    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SnowFlake snowFlake;

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req) {
        CategoryExample categoryExample = new CategoryExample();

        PageHelper.startPage(req.getPage(),req.getSize());
        List<Category>  categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("总行数：{}",pageInfo.getTotal());
//        LOG.info("总页数：{}",pageInfo.getPages());

//        List<CategoryQueryResp> categoryQueryRespList = new ArrayList<>();
//        for(Category category:categoryList){
//            CategoryQueryResp categoryResp = new CategoryQueryResp();
//            BeanUtils.copyProperties(category,categoryResp);
//            categoryQueryRespList.add(categoryResp);
//        }

        List<CategoryQueryResp> categoryQueryRespList = CopyUtil.copyList(categoryList,CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp();
        pageResp.setList(categoryQueryRespList);
        pageResp.setTotal(pageInfo.getTotal());

        return pageResp ;
    }

    //保存：修改+新增
    public void save(CategorySaveReq req){
        Category category = CopyUtil.copy(req,Category.class);
        if(ObjectUtils.isEmpty(req.getId())){
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }
        else{
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    //删除
    public void delete(Long id){
        categoryMapper.deleteByPrimaryKey(id);
    }
}
