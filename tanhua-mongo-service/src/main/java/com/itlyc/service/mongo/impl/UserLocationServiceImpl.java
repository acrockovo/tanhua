package com.itlyc.service.mongo.impl;

import com.itlyc.domain.db.User;
import com.itlyc.domain.mongo.UserLocation;
import com.itlyc.service.mongo.UserLocationService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Service
public class UserLocationServiceImpl implements UserLocationService {

    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 上报地理位置信息
     * @param longitude
     * @param latitude
     * @param addStr
     * @param userId
     */
    @Override
    public void saveOrUpdate(double longitude, double latitude, String addStr, Long userId) {
        Query query = new Query(
                Criteria.where("userId").is(userId)
        );

        UserLocation userLocation = mongoTemplate.findOne(query, UserLocation.class);

        if(userLocation == null){
            userLocation = new UserLocation();

            userLocation.setUserId(userId);//用户id
            userLocation.setLocation(new GeoJsonPoint(longitude,latitude));//经纬度
            userLocation.setAddress(addStr);//地址
            userLocation.setCreated(System.currentTimeMillis());//创建时间
            userLocation.setUpdated(System.currentTimeMillis());//修改时间
            userLocation.setLastUpdated(System.currentTimeMillis());//上次更新时间

            mongoTemplate.save(userLocation);

        }else{
            userLocation.setLocation(new GeoJsonPoint(longitude,latitude));//经纬度
            userLocation.setAddress(addStr);//地址
            userLocation.setUpdated(System.currentTimeMillis());//修改时间
            userLocation.setLastUpdated(userLocation.getUpdated());//上次更新时间

            mongoTemplate.save(userLocation);
        }
    }
}
