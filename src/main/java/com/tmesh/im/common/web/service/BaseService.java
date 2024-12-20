package com.tmesh.im.common.web.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 基础 service 接口层基类
 */

public interface BaseService<T> extends IService<T> {

    /**
     * 新增（并返回id）
     */
    boolean add(T entity);

    /**
     * 根据 id 删除
     */
    Integer deleteById(Long id);

    /**
     * 根据ids 批量删除
     */
    Integer deleteByIds(Collection<? extends Serializable> ids);

    /**
     * 根据ids 批量删除
     */
    Integer deleteByIds(Long[] ids);

    /**
     * 根据ids 批量删除
     */
    Integer deleteByIds(List<Long> ids);


    /**
     * 根据 id 查询
     */
    T getById(Long id);

    /**
     * 根据 id 查询
     */
    T findById(Long id);

    /**
     * 根据 ids 查询
     */
    List<T> getByIds(Collection<? extends Serializable> idList);

    /**
     * 查询总记录数
     */
    Long queryCount(T t);

    /**
     * 查询总记录数
     */
    Long queryCount(Wrapper<T> wrapper);

    /**
     * 查询全部记录
     */
    List<T> queryList(T t);

    /**
     * 查询全部记录
     */
    List<T> queryList(Wrapper<T> wrapper);

    /**
     * 查询一个
     */
    T queryOne(T t);

    /**
     * 查询一个
     */
    T queryOne(Wrapper<T> wrapper);

    /**
     * 批量新增
     */
    Integer batchAdd(List<T> list);

    /**
     * 批量新增（仅mysql可以使用）
     */
    Integer batchAdd(List<T> list, Integer batchCount);

    /**
     * 修改状态
     */
    void changeStatus(T t, String... param);
}
