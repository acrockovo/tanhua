package com.itlyc.service.mongo;

public interface UserLocationService {
    // 上报地理位置信息
    void saveOrUpdate(double longitude, double latitude, String addStr, Long id);
}
