package com.itlyc.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 通用设置Vo
public class SettingVo implements Serializable {
    private Long id;
    private String strangerQuestion = "你是喜欢渣男，还是暗恋靓女？？"; // 陌生人问题
    private String phone; // 手机号
    private Boolean likeNotification = true;  // 允许推送喜欢通知
    private Boolean pinglunNotification = true; // 允许推送评论通知
    private Boolean gonggaoNotification = true; // 允许推送公告通知
}
