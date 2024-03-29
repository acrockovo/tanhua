package com.itlyc.service.db;

import com.itlyc.domain.db.Question;

/**
 * @author lyc
 * @date 2023-01-17
 * @description 陌生人问题接口
 */
public interface QuestionService {
    // 根据用户id查找陌生人问题
    Question findByUserId(Long userId);

    // 保存陌生人问题
    void save(Question question);

    //  修改陌生人问题
    void update(Question question);

}
