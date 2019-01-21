package com.connext.wms.service.impl;

import com.connext.wms.dao.ExpressCompanyMapper;
import com.connext.wms.entity.ExpressCompany;
import com.connext.wms.entity.ExpressCompanyExample;
import com.connext.wms.service.ExpressCompanyService;
import com.connext.wms.util.Page;
import com.connext.wms.util.PageSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.connext.wms.util.Validate.checkName;
import static com.connext.wms.util.Validate.checkPhone;

/**
 * @Author: Chao.Sun
 * @Date: 2019/1/7 9:37
 * @Version 1.0
 */

@Service
public class ExpressCompanyServiceImpl implements ExpressCompanyService {
    @Autowired
    private ExpressCompanyMapper expressCompanyMapper;

    //判断的状态
    private Integer flag;


    //分页查询所有快递公司信息
    @Override
    public Page selectByPage(Integer currPage){
        List<ExpressCompany> list = expressCompanyMapper.selectByPage((currPage-1)*Page.PAGE_SIZE, Page.PAGE_SIZE);
        ExpressCompanyExample example = new ExpressCompanyExample();
        /*return byPage(currPage,list,example);*/
        return PageSet.setPage(list,currPage,expressCompanyMapper.countByExample(example));
    }

    //根据关键字查询
    @Override
    public Page selectByKey(Integer currPage,String key){
        String newKey = "%" + key + "%";
        List<ExpressCompany> list = expressCompanyMapper.selectByKey((currPage-1)*Page.PAGE_SIZE, Page.PAGE_SIZE,newKey);
        ExpressCompanyExample example = new ExpressCompanyExample();
        example.or().andExpressCompanyNameLike(newKey);
        /*return byPage(currPage,list,example);*/
        return PageSet.setPage(list,currPage,expressCompanyMapper.countByExample(example));
    }

    //添加快递公司信息
    @Override
    public Integer insert(String expressCompanyName,String contactWay){
        //1:该公司已存在；2：该公司不存在，可以添加；3：手机号格式错误;4:公司名称超出范围;5:信息不完整
        if(expressCompanyName.isEmpty()||contactWay.isEmpty()){
            flag = 5;
        }else {
            //按公司名查找是否存在记录
            ExpressCompanyExample example = new ExpressCompanyExample();
            example.or().andExpressCompanyNameEqualTo(expressCompanyName);
            List<ExpressCompany> list = expressCompanyMapper.selectByExample(example);
            if(list.size()==1){
                flag = 1;
            }else{
                if(checkName(expressCompanyName)){
                    if(checkPhone(contactWay)){
                        ExpressCompany expressCompany = new ExpressCompany();
                        expressCompany.setExpressCompanyName(expressCompanyName);
                        expressCompany.setContactWay(contactWay);
                        expressCompanyMapper.insert(expressCompany);
                        flag = 2;
                    }else{
                        flag = 3;
                    }
                }else{
                    flag = 4;
                }
            }
        }
        return flag;
    }

    //删除快递公司信息
    @Override
    public void deleteByExample(String expressCompanyName){
        ExpressCompanyExample example = new ExpressCompanyExample();
        example.createCriteria().andExpressCompanyNameEqualTo(expressCompanyName);
        expressCompanyMapper.deleteByExample(example);
    }

    //修改快递公司信息
    @Override
    public Integer updateByExample(String newName,String expressCompanyName,String contactWay){
         //1:新公司已存在;2：新公司不存在，可以添加；3：手机号格式错误;4:公司名称超出范围;
         //5：信息不完整;6：原公司不存在
        if(expressCompanyName.isEmpty()||newName.isEmpty()||contactWay.isEmpty()){
            flag = 5;
        }else{
            //通过新公司名称查找是否已经存在该条记录
            ExpressCompanyExample example = new ExpressCompanyExample();
            example.or().andExpressCompanyNameEqualTo(newName);
            List<ExpressCompany> list1 = expressCompanyMapper.selectByExample(example);
            example.or().andExpressCompanyNameEqualTo(expressCompanyName);
            List<ExpressCompany> list2 = expressCompanyMapper.selectByExample(example);
            if(list2.size()==1){
                if(list1.size()==1){
                    flag = 1;
                }else{
                    if(checkName(newName)){
                        if(checkPhone(contactWay)){
                            ExpressCompany record = new ExpressCompany();
                            record.setExpressCompanyName(newName);
                            record.setContactWay(contactWay);
                            example.or().andExpressCompanyNameEqualTo(expressCompanyName);
                            expressCompanyMapper.updateByExample(record,example);
                            flag = 2;
                        }else{
                            flag = 3;
                        }
                    }else{
                        flag = 4;
                    }
                }
            }else{
                flag = 6;
            }
        }
        return flag;
    }

}
