package com.itlyc.web.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.itlyc.domain.db.UserInfo;
import com.itlyc.domain.mongo.Comment;
import com.itlyc.domain.mongo.Movement;
import com.itlyc.domain.mongo.Video;
import com.itlyc.domain.vo.CommentVo;
import com.itlyc.domain.vo.MovementVo;
import com.itlyc.domain.vo.PageBeanVo;
import com.itlyc.domain.vo.VideoVo;
import com.itlyc.service.db.UserInfoService;
import com.itlyc.service.mongo.CommentService;
import com.itlyc.service.mongo.MovementService;
import com.itlyc.service.mongo.VideoService;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class UserManager {

    @Reference
    private UserInfoService userInfoService;
    @Reference
    private MovementService movementService;
    @Reference
    private CommentService commentService;
    @Reference
    private VideoService videoService;

    /**
     * 查找用户列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    public ResponseEntity findByPage(Integer pageNum, Integer pageSize) {
        PageBeanVo pageBeanVo = userInfoService.findByPage(pageNum, pageSize);
        return ResponseEntity.ok(pageBeanVo);
    }

    /**
     * 根据用户id查找用户详细信息
     * @param userId 用户id
     * @return
     */
    public ResponseEntity findUserById(Long userId) {
        return ResponseEntity.ok(userInfoService.findById(userId));
    }

    /**
     * 查看用户发表动态列表
     * @param uid 用户id
     * @param state 状态
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    public ResponseEntity findMovementByCondition4Page(Long uid, Integer state, int pageNum, int pageSize) {

        PageBeanVo pageBeanVo = movementService.findMovementByCondition4Page(uid,state,pageNum,pageSize);

        List<MovementVo> movementVoList = new ArrayList<>();

        UserInfo userInfo = null;
        if (uid != null) {
            userInfo = userInfoService.findById(uid);
        }

        //2.2 遍历movement集合
        List<Movement> items = (List<Movement>)pageBeanVo.getItems();
        if (CollUtil.isNotEmpty(items)) {
            for (Movement movement : items) {
                //2.3 若uid为空 就获取动态所属的用户id,查询动态所属的用户信息
                if (uid == null) {
                    userInfo = userInfoService.findById(movement.getUserId());
                }

                //2.2 将每个动态封装为movementVo
                MovementVo movementVo = new MovementVo();

                //先用户,后动态
                movementVo.setUserInfo(userInfo);
                movementVo.setMovement(movement);

                //单独处理发布时间
                movementVo.setCreateDate(DateUtil.formatDateTime(new Date(movement.getCreated())));

                movementVoList.add(movementVo);
            }
        }

        //4.将封装后的新集合设置给分页对象
        pageBeanVo.setItems(movementVoList);

        //5.返回分页对象
        return ResponseEntity.ok(pageBeanVo);
    }

    /**
     * 动态详情
     * @param id 动态id
     * @return
     */
    public ResponseEntity findMovementById(String id) {
        Movement movement = movementService.findMovementByMovementId(id);

        UserInfo userInfo = userInfoService.findById(movement.getUserId());

        MovementVo movementVo = new MovementVo();
        movementVo.setUserInfo(userInfo);
        movementVo.setMovement(movement);
        movementVo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(movement.getCreated())));
        return ResponseEntity.ok(movementVo);
    }

    /**
     * 动态评论列表
     * @param messageID 评论id
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    public ResponseEntity findMovementCommentByPage(int pageNum, int pageSize, String messageID) {
        PageBeanVo pageBeanVo = commentService.findMovementComment(pageNum, pageSize, messageID);

        List<Comment> items = (List<Comment>) pageBeanVo.getItems();
        List<CommentVo> commentVoList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(items)){
            for (Comment comment : items) {
                UserInfo userInfo = userInfoService.findById(comment.getUserId());

                CommentVo commentVo = new CommentVo();
                commentVo.setAvatar(userInfo.getAvatar());
                commentVo.setNickname(userInfo.getNickname());

                commentVo.setId(comment.getId().toString());
                commentVo.setContent(comment.getContent());
                commentVo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(comment.getCreated())));

                commentVoList.add(commentVo);
            }
        }

        pageBeanVo.setItems(commentVoList);
        return ResponseEntity.ok(pageBeanVo);
    }

    /**
     * 查找视频列表
     * @param uid 用户id
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    public ResponseEntity findUserVideoByPage(int pageNum, int pageSize, Long uid) {
        PageBeanVo pageBeanVo = videoService.findUserVideoByPage(pageNum,pageSize,uid);

        List<Video> items = (List<Video>) pageBeanVo.getItems();

        List<VideoVo> videoVoList = new ArrayList<>();
        UserInfo userInfo = userInfoService.findById(uid);

        if(!CollectionUtils.isEmpty(items)){
            for (Video video : items) {
                VideoVo videoVo = new VideoVo();
                BeanUtils.copyProperties(userInfo,videoVo);
                BeanUtils.copyProperties(video,videoVo);
                videoVo.setCover(video.getPicUrl());//视频封面
                videoVo.setSignature(video.getText());//视频文字
                videoVoList.add(videoVo);
            }
        }

        pageBeanVo.setItems(videoVoList);
        return ResponseEntity.ok(pageBeanVo);
    }
}
