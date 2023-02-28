package com.itlyc.service.mongo.impl;

import com.itlyc.domain.mongo.UserLocation;
import com.itlyc.service.mongo.UserLocationService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 搜索附近的人
     * @param id 当前登录用户id
     * @param distance 距离
     * @return
     */
    @Override
    public List<Long> searchNear(Long id, int distance) {

        UserLocation userLocation = mongoTemplate.findOne(new Query(Criteria.where("userId").is(id)), UserLocation.class);
        Distance dis = new Distance(distance / 1000, Metrics.KILOMETERS); // 半径
        Circle circle = new Circle(userLocation.getLocation(), dis); // 根据当前用户坐标花圈
        Query query = new Query(
                Criteria.where("location").withinSphere(circle) // 设置范围条件
                        .and("userId").ne(id) // 排除自己
        );
        List<UserLocation> userLocations = mongoTemplate.find(query, UserLocation.class);
        List<Long> userIds = new ArrayList<>();

        if(!CollectionUtils.isEmpty(userLocations)){
            for (UserLocation location : userLocations) {
                userIds.add(location.getUserId()); // 遍历获取用户id
            }
        }
        return userIds;
    }
}
