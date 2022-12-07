package com.itlyc.mapper;

import com.itlyc.domain.db.User;
import org.apache.ibatis.annotations.Param;

/**
 * 用户模块mapper
 * @author lyc
 * @date 2022-11-18
 */
public interface UserMapper {
    // 保存用户
    Long save(User user);

    // 根据手机号查询用户
    User findUserByPhone(@Param("phone") String phone);
}
