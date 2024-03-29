package com.itlyc.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("video_score")
public class VideoScore implements Serializable {

    private ObjectId id;
    private Long userId;// 用户id
    private Long videoId; //大数据推荐视频id，需要转化为Long类型
    private Double score; //得分
    private Long date; //时间戳
}
