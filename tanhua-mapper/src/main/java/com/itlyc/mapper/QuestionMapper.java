package com.itlyc.mapper;

import com.itlyc.domain.db.Question;
import org.apache.ibatis.annotations.Param;

/**
 * @author lyc
 * @date 2023-01-17
 * @description 陌生人问题mapper
 */
public interface QuestionMapper {
    // 根据id查找陌生人问题
    Question findByUserId(@Param("id") Long userId);
}
