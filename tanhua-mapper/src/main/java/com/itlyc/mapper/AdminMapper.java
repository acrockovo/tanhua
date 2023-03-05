package com.itlyc.mapper;

import com.itlyc.domain.db.Admin;

public interface AdminMapper {

    // 根据用户名查询用户
    Admin findUserByName(String username);

}
