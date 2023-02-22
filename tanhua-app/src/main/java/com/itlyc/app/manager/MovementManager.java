package com.itlyc.app.manager;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.itlyc.app.interceptor.UserHolder;
import com.itlyc.autoconfig.oss.OssTemplate;
import com.itlyc.domain.db.UserInfo;
import com.itlyc.domain.mongo.Comment;
import com.itlyc.domain.mongo.Movement;
import com.itlyc.domain.vo.MovementVo;
import com.itlyc.domain.vo.PageBeanVo;
import com.itlyc.service.db.UserInfoService;
import com.itlyc.service.mongo.CommentService;
import com.itlyc.service.mongo.MovementService;
import com.itlyc.util.ConstantUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

@Component
public class MovementManager {

    @Reference
    private MovementService movementService;
    @Autowired
    private OssTemplate ossTemplate;
    @Reference
    private UserInfoService userInfoService;
    @Reference
    private CommentService commentService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 上传动态
     * @param movement 动态详情实体
     * @param imageContent 图片列表
     * @return
     */
    public ResponseEntity save(Movement movement, MultipartFile[] imageContent) throws IOException {

        // 声明存储图片地址集合
        List<String> medias = new ArrayList<>();

        if(ArrayUtil.isNotEmpty(imageContent)){
            for (MultipartFile multipartFile : imageContent) {
                String url = ossTemplate.upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
                medias.add(url);
            }
        }

        Long userId = UserHolder.get().getId();
        movement.setUserId(userId);
        movement.setMedias(medias);
        movement.setState(1); // 默认审核通过
        movement.setCreated(System.currentTimeMillis());
        movement.setSeeType(1); // 默认公开

        movementService.save(movement);
        return ResponseEntity.ok(null);
    }

    /**
     * 查找个人动态
     * @param page 页码
     * @param pageSize 每页条数
     * @param userId 用户id
     * @return
     */
    public ResponseEntity findMyMovementByPage(int page, int pageSize, Long userId) {

        PageBeanVo pageBeanVo = movementService.findMyMovementByPage(page,pageSize,userId);

        // 查询用户个人信息
        UserInfo userInfo = userInfoService.findById(userId);
        List<MovementVo> movementVoList = new ArrayList<>();
        List<Movement> items = (List<Movement>) pageBeanVo.getItems();

        if(!CollectionUtils.isEmpty(items)){
            for (Movement movement : items) {
                MovementVo movementVo = new MovementVo();
                if(redisTemplate.hasKey(StrUtil.format(ConstantUtil.MOVEMENT_LIKE,userId,movement.getId()))){
                    movementVo.setHasLiked(1);
                }
                if(redisTemplate.hasKey(StrUtil.format(ConstantUtil.MOVEMENT_LOVE,userId,movement.getId()))){
                    movementVo.setHasLoved(1);
                }
                movementVo.setUserInfo(userInfo);
                movementVo.setMovement(movement);

                movementVoList.add(movementVo);
            }
        }
        pageBeanVo.setItems(movementVoList);
        return ResponseEntity.ok(pageBeanVo);
    }

    /**
     * 查询好友动态列表
     * @param page 页码
     * @param pageSize 每页条数
     * @return
     */
    public ResponseEntity getFriendMovements(int page, int pageSize) {

        Long userId = UserHolder.get().getId();

        PageBeanVo pageBeanVo = movementService.getFriendMovements(page,pageSize,userId);

        List<MovementVo> movementVoList = new ArrayList<>();
        List<Movement> items = (List<Movement>) pageBeanVo.getItems();

        if(!CollectionUtils.isEmpty(items)){
            for (Movement movement : items) {
                // 从动态详情表中得到发布这条动态的用户id
                Long friendId = movement.getUserId();
                // 查询用户个人信息
                UserInfo userInfo = userInfoService.findById(friendId);

                MovementVo movementVo = new MovementVo();
                if(redisTemplate.hasKey(StrUtil.format(ConstantUtil.MOVEMENT_LIKE,userId,movement.getId()))){
                    movementVo.setHasLiked(1);
                }
                if(redisTemplate.hasKey(StrUtil.format(ConstantUtil.MOVEMENT_LOVE,userId,movement.getId()))){
                    movementVo.setHasLoved(1);
                }
                movementVo.setUserInfo(userInfo);
                movementVo.setMovement(movement);

                movementVoList.add(movementVo);
            }
        }
        pageBeanVo.setItems(movementVoList);
        return ResponseEntity.ok(pageBeanVo);
    }


    /**
     * 推荐动态查询
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    public ResponseEntity findRecommendMovementByPage(int pageNum, int pageSize) {

        Long userId = UserHolder.get().getId();

        PageBeanVo pageBeanVo = movementService.findRecommendMovementByPage(pageNum,pageSize,userId);

        List<Movement> items = (List<Movement>) pageBeanVo.getItems();

        List<MovementVo> movementVoList = new ArrayList<>();

        if(!CollectionUtils.isEmpty(items)){
            for (Movement movement : items) {
                Long friendId = movement.getUserId();
                UserInfo userInfo = userInfoService.findById(friendId);


                MovementVo movementVo = new MovementVo();
                if(redisTemplate.hasKey(StrUtil.format(ConstantUtil.MOVEMENT_LIKE,userId,movement.getId()))){
                    movementVo.setHasLiked(1);
                }
                if(redisTemplate.hasKey(StrUtil.format(ConstantUtil.MOVEMENT_LOVE,userId,movement.getId()))){
                    movementVo.setHasLoved(1);
                }
                movementVo.setUserInfo(userInfo);
                movementVo.setMovement(movement);

                movementVoList.add(movementVo);
            }
        }
        pageBeanVo.setItems(movementVoList);
        return ResponseEntity.ok(pageBeanVo);
    }

    /**
     * 动态点赞
     * @param movementId 动态id
     * @return
     */
    public ResponseEntity saveMovementType(String movementId,Integer commentType) {

        Long userId = UserHolder.get().getId();

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(commentType); // 1为点赞
        comment.setCreated(System.currentTimeMillis());

        int count = commentService.saveMovementType(comment);

        // 往redis中存入点赞标识
        if(commentType == 1){
            redisTemplate.opsForValue().set(StrUtil.format(ConstantUtil.MOVEMENT_LIKE,userId,movementId),"1");
        }else if(commentType == 3){
            redisTemplate.opsForValue().set(StrUtil.format(ConstantUtil.MOVEMENT_LOVE,userId,movementId),"1");
        }

        return ResponseEntity.ok(count);
    }

    /**
     * 取消动态点赞
     * @param movementId 动态id
     * @return
     */
    public ResponseEntity deleteMovementType(String movementId,Integer commentType) {

        Long userId = UserHolder.get().getId();

        int count = commentService.deleteMovementType(movementId, commentType, userId);

        if(commentType == 1){
            redisTemplate.delete(StrUtil.format(ConstantUtil.MOVEMENT_LIKE,userId,movementId));
        }else if(commentType == 3){
            redisTemplate.delete(StrUtil.format(ConstantUtil.MOVEMENT_LOVE,userId,movementId));
        }

        return ResponseEntity.ok(count);
    }
}
