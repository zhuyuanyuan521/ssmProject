package com.ssm.dao;

import java.util.List;

import com.ssm.model.Add;


public interface AddMapper {
    int deleteByPrimaryKey(String id);

    int insert(Add record);

    int insertSelective(Add record);

    Add selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Add record);

    int updateByPrimaryKey(Add record);
    
    List<Add> getAll();
}