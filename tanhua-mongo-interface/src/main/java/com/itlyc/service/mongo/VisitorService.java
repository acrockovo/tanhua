package com.itlyc.service.mongo;

import com.itlyc.domain.mongo.Visitor;

import java.util.List;

public interface VisitorService {
    // 查找最近访客
    List<Visitor> findVisitorsSinceLastAccessTime(Long userId, Long lastAccessTime);

    void save(Long id, Long userId);

}
