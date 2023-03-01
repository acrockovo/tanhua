package com.itlyc.service.mongo;

import com.itlyc.domain.mongo.UserLike;

public interface UserLikeService {
    // 右滑喜欢
    void save(UserLike userLike);
}
