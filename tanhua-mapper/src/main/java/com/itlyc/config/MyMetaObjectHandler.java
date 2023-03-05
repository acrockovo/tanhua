package com.itlyc.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 插入或更新时日期字段自动填充
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Object created = getFieldValByName("created", metaObject);
        if (null == created) {
            //字段为空，可以进行填充
            setFieldValByName("created", new Date(), metaObject);
        }

        Object updated = getFieldValByName("updated", metaObject);
        if (null == updated) {
            //字段为空，可以进行填充
            setFieldValByName("updated", new Date(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //更新数据时，直接更新字段
        setFieldValByName("updated", new Date(), metaObject);
    }
}