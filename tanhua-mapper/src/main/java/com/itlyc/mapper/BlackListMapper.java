package com.itlyc.mapper;

import com.github.pagehelper.Page;
import com.itlyc.domain.db.UserInfo;
import org.apache.ibatis.annotations.Param;

public interface BlackListMapper {

    Page<UserInfo> findBlackListByPage(Long userId);

    void deleteBlackList(@Param("userId") Long userId, @Param("blackId") Long blackId);
}
