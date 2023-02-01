package com.itlyc.service.db.impl;

import com.itlyc.domain.db.Question;
import com.itlyc.mapper.QuestionMapper;
import com.itlyc.service.db.QuestionService;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;

/**
 * @author lyc
 * @date 2023-01-17
 * @description 陌生人问题实现类
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Resource
    private QuestionMapper questionMapper;

    @Override
    public Question findByUserId(Long userId) {
        return questionMapper.findByUserId(userId);
    }

    @Override
    public void save(Question question) {
        questionMapper.save(question);
    }

    @Override
    public void update(Question question) {
        questionMapper.update(question);
    }
}
