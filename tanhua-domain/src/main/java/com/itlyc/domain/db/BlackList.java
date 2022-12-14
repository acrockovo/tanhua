package com.itlyc.domain.db;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

//黑名单
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlackList implements Serializable {

    private Long id;
    private Long userId; //用户id
    private Long blackUserId; //黑名单用户id

    @TableField(fill = FieldFill.INSERT) // 字段填充标记 配合自动填充字段使用
    private Date created;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updated;
}
