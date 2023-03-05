package com.itlyc.service.db.impl;

import com.itlyc.domain.db.Admin;
import com.itlyc.mapper.AdminMapper;
import com.itlyc.service.db.AdminService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Override
    public Admin findUserByName(String username) {
        return adminMapper.findUserByName(username);
    }
}
