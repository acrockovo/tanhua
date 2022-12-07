package com.itlyc.mapper;

import com.itlyc.domain.db.UserInfo;
import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper {
    // 保存用户信息
    void save(UserInfo userInfo);
    // 更新用户信息
    void update(UserInfo userInfo);
    // 根据id查找用户
    UserInfo findById(@Param("id") Long id);

}
