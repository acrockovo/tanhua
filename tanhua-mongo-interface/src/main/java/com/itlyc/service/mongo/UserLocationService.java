package com.itlyc.service.mongo;

import java.util.List;

public interface UserLocationService {
    // 上报地理位置信息
    void saveOrUpdate(double longitude, double latitude, String addStr, Long id);
    // 搜索附近的人
    List<Long> searchNear(Long id, int distance);
}
