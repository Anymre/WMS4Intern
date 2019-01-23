package com.connext.wms.service.impl;

import com.connext.wms.dao.GoodsMapper;
import com.connext.wms.dao.OutRepertoryMapper;
import com.connext.wms.entity.*;
import com.connext.wms.service.ExceptionService;
import com.connext.wms.util.Page;
import com.connext.wms.util.PageSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Chao.Sun
 * @Date: 2019/1/7  17:00
 * @Version 1.0
 */
@Service
public class ExceptionServiceImpl implements ExceptionService {
    @Autowired
    private OutRepertoryMapper outRepertoryMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    //分页查询所有异常订单信息返回列表
    @Override
    public Page selectByPage(Integer currPage){
        List<OutRepertory> list = outRepertoryMapper.selectByPage((currPage-1)*Page.PAGE_SIZE, Page.PAGE_SIZE);
        OutRepertoryExample example = new OutRepertoryExample();
        String status = "%shipException%";
        example.or().andOutRepoStatusLike(status);
        return PageSet.setPage(list,currPage,outRepertoryMapper.countByExample(example));
    }

    //查找关键字查找异常订单
    @Override
    public Page selectByExampleToKey(Integer currPage,String key){
        String newKey = "%" + key + "%";
        List<OutRepertory> list = outRepertoryMapper.selectByKey1(newKey,(currPage-1)*Page.PAGE_SIZE, Page.PAGE_SIZE);
        OutRepertoryExample example = new OutRepertoryExample();
        example.or().andOutRepoIdLike(newKey);
        return PageSet.setPage(list,currPage,outRepertoryMapper.countByExample(example));
    }

    //点击查看异常订单详情
    @Override
    public OutRepertory selectByPrimaryKey(Integer id){
        return outRepertoryMapper.selectByPrimaryKey(id);
    }


}
