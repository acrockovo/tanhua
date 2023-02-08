package com.itlyc.service.db.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itlyc.domain.db.UserInfo;
import com.itlyc.domain.vo.PageBeanVo;
import com.itlyc.mapper.BlackListMapper;
import com.itlyc.service.db.BlackListService;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Log
public class BlackListServiceImpl implements BlackListService {

    @Autowired
    private BlackListMapper blackListMapper;

    /**
     * 查询用户黑名单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param userId 用户id
     * @return
     */
    @Override
    public PageBeanVo findBlackListByPage(Long userId, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        Page<UserInfo> page = blackListMapper.findBlackListByPage(userId);

        PageBeanVo pageBeanVo = new PageBeanVo();
        pageBeanVo.setCounts(page.getTotal()); // 从记录数
        pageBeanVo.setItems(page.getResult()); // 数据集合

        return pageBeanVo;

    }

    /**
     * 删除用户黑名单
     * @param userId 用户id
     * @param blackId 黑名单用户id
     * @return
     */
    @Override
    public void deleteBlackList(Long userId, Long blackId) {
        blackListMapper.deleteBlackList(userId,blackId);
    }
}
