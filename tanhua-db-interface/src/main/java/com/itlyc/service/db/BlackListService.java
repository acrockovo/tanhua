package com.itlyc.service.db;

import com.itlyc.domain.vo.PageBeanVo;

/**
 * 用户黑名单
 * @author lyc
 * @date 2023-02-08
 */
public interface BlackListService {

    PageBeanVo findBlackListByPage(Long userId, Integer pageNum, Integer pageSize);

    void deleteBlackList(Long userId, Long blackId);
}
