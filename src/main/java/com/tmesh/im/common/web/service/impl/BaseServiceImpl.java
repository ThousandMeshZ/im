package com.tmesh.im.common.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.tmesh.im.common.exception.BaseException;
import com.tmesh.im.common.utils.BeanCopyUtils;
import com.tmesh.im.common.web.dao.BaseDao;
import com.tmesh.im.common.web.service.BaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author : TMesh
 * @version : 1.0.0
 * @description : 基础 service 实现层基类
 */
public class BaseServiceImpl<T> extends ServiceImpl<BaseDao<T>, T> implements BaseService<T> {

    protected final static String EXIST_MAG = "数据不存在";

    private BaseDao<T> baseDao;

    public void setBaseDao(BaseDao<T> baseDao) {
        this.baseDao = baseDao;
    }

    private static final Integer batchCount = 1000;

    @Override
    public boolean add(T entity) {
        return super.save(entity);
    }

    @Override
    public Integer deleteById(Long id) {
        return this.baseDao.deleteById(id);
    }

    @Override
    public Integer deleteByIds(Collection<? extends Serializable> ids) {
        return this.baseDao.deleteBatchIds(ids);
    }

    @Override
    public Integer deleteByIds(Long[] ids) {
        return this.deleteByIds(Arrays.asList(ids));
    }

    @Override
    public Integer deleteByIds(List<Long> ids) {
        return this.baseDao.deleteBatchIds(ids);
    }


    @Override
    public T getById(Long id) {
        return super.getById(id);
    }


    @Override
    public T findById(Long id) {
        T t = super.getById(id);
        if (t == null) {
            throw new BaseException(BaseServiceImpl.EXIST_MAG);
        }
        return t;
    }

    @Override
    public List<T> getByIds(Collection<? extends Serializable> idList) {
        return super.listByIds(idList);
    }

    @Override
    public Long queryCount(T t) {
        Wrapper<T> wrapper = new QueryWrapper<>(t);
        return this.queryCount(wrapper);
    }

    @Override
    public Long queryCount(Wrapper<T> wrapper) {
        return super.count(wrapper);
    }

    @Override
    public List<T> queryList(T t) {
        Wrapper<T> wrapper = new QueryWrapper<>(t);
        return this.queryList(wrapper);
    }

    @Override
    public List<T> queryList(Wrapper<T> wrapper) {
        return super.list(wrapper);
//        return this.baseDao.selectList(wrapper);
    }

    @Override
    public T queryOne(T t) {
        Wrapper<T> wrapper = new QueryWrapper<>(t);
        return this.queryOne(wrapper);
    }

    @Override
    public T queryOne(Wrapper<T> wrapper) {
        return super.getOne(wrapper);
//        return this.baseDao.selectOne(wrapper);
    }

    @Override
    public Integer batchAdd(List<T> list) {
        return this.batchAdd(list, BaseServiceImpl.batchCount);
    }

    @Transactional
    @Override
    public Integer batchAdd(List<T> list, Integer batchCount) {
        if (CollectionUtils.isEmpty(list))
            return 0;

        List<T> batchList = new ArrayList<>();
        AtomicReference<Integer> result = new AtomicReference<>(0);
        list.forEach(t -> {
            batchList.add(t);
            if (batchList.size() == batchCount) {
                result.updateAndGet(v -> v + this.baseDao.insertBatchSomeColunm(batchList));
                batchList.clear();
            }
        });

        if (!CollectionUtils.isEmpty(batchList)) {
            result.updateAndGet(v -> v + this.baseDao.insertBatchSomeColunm(batchList));
            batchList.clear();
        }

        return result.get();
    }

    /**
     * 循环工具类
     */
    public static <T> Consumer<T> forEach(BiConsumer<T, Integer> consumer) {
        class Temp {
            int i;
        }
        Temp temp = new Temp();
        return r -> {
            int index = temp.i++;
            consumer.accept(r, index);
        };
    }

    @Override
    public void changeStatus(T t, String... param) {
        if (param.length == 0)
            this.updateById(BeanCopyUtils.include(t, "id", "status"));
        else
            this.updateById(BeanCopyUtils.include(t, param));
    }

    /**
     * 响应请求分页数据
     */
    protected PageInfo<?> getPageInfo(List<?> list, List<?> oldList) {
        long total = new PageInfo<>(oldList).getTotal();
        return this.getPageInfo(list, total);
    }

    /**
     * 格式化分页
     */
    public PageInfo<?> getPageInfo(List<?> list, long total) {
        PageInfo<?> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(total);
        return pageInfo;
    }
}
