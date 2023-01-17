package com.itlyc.service.db;

import com.itlyc.domain.db.User;

/**
 * 用户模块service
 * @author lyc
 * @date 2022-11-18
 */
public interface UserService {
    // 保存用户
    Long save(User user);

    // 根据手机号查询用户
    User findUserByPhone(String phone);

}
