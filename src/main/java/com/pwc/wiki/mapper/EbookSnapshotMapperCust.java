package com.pwc.wiki.mapper;

import com.pwc.wiki.resp.StatisticResp;

import java.util.List;

/**
 * Created by xuhaocheng on 14/10/2021.
 */
public interface EbookSnapshotMapperCust {
    public void genSnapshot();

    List<StatisticResp> getStatistic();
}