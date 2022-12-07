package com.itlyc.service.db;

import com.itlyc.domain.db.UserInfo;

public interface UserInfoService {
    // 保存用户信息
    void save(UserInfo userInfo);
    // 更新用户信息
    void update(UserInfo userInfo);
    // 根据用户id查找
    UserInfo findById(Long id);
}
