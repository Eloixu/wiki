package com.pwc.wiki.mapper;

import org.apache.ibatis.annotations.Param;


/**
 * Created by xuhaocheng on 06/09/2021.
 */
public interface DocMapperCust {
    //@Param("id")里的id会和DocMapperCust.xml里的 #{id} 匹配
    public void increaseViewCount(@Param("id") Long id);

    public void increaseVoteCount(@Param("id") Long id);

    public void updateEbookInfo();
}
