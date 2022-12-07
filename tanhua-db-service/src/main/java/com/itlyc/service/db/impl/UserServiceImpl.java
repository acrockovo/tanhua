package com.itlyc.service.db.impl;

import cn.hutool.crypto.SecureUtil;
import com.itlyc.domain.db.User;
import com.itlyc.mapper.UserMapper;
import com.itlyc.service.db.UserService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 用户模块service 实现类
 * @author lyc
 * @date 2022-11-18
 */

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 保存用户
     * @param user 用户对象
     * @return 主键id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(User user) {

        user.setPassword(SecureUtil.md5(user.getPassword())); // 对密码进行md5加密

        userMapper.save(user);

        return user.getId();
    }

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户对象
     */
    @Override
    public User findUserByPhone(String phone) {

        User user = userMapper.findUserByPhone(phone);

        return user;
    }
}
