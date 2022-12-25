package com.itlyc.service.db.impl;

import com.itlyc.domain.db.UserInfo;
import com.itlyc.mapper.UserInfoMapper;
import com.itlyc.service.db.UserInfoService;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

/**
 * 用户详情业务处理
 * @author lyc
 * @date 2022-11-25
 */

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    /**
     * 保存用户详情信息
     * @param userInfo 用户详情对象
     */
    @Override
    public void save(UserInfo userInfo) {

        userInfoMapper.save(userInfo);
    }

    /**
     * 更新用户信息
     * @param userInfo 用户详情对象
     */
    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.update(userInfo);
    }

    /**
     * 根据id查找用户
     * @param id 用户id
     * @return
     */
    @Override
    public UserInfo findById(Long id) {
        return userInfoMapper.findById(id);
    }
}
