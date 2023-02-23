package com.itlyc.app.controller;

import com.itlyc.app.manager.MovementManager;
import com.itlyc.domain.mongo.Movement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 动态详情控制器
 * @author lyc
 * @date 2023-02-18
 */
@RestController
public class MovementController {

    @Autowired
    private MovementManager movementManager;

    /**
     * 上传动态
     * @param movement 动态详情实体
     * @param imageContent 图片列表
     * @return
     */
    @PostMapping("/movements")
    public ResponseEntity save(Movement movement, MultipartFile[] imageContent) throws IOException {

        return movementManager.save(movement,imageContent);
    }

    /**
     * 查找个人动态
     * @param page 页码
     * @param pageSize 每页条数
     * @param userId 用户id
     * @return
     */
    @GetMapping("/movements/all")
    public ResponseEntity findMyMovementByPage(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "8") int pageSize,
            Long userId){
        return movementManager.findMyMovementByPage(page,pageSize,userId);
    }

    /**
     * 查询好友动态列表
     * @param page 页码
     * @param pageSize 每页条数
     * @return
     */
    @GetMapping("/movements")
    public ResponseEntity getFriendMovements(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "8") int pageSize){
      return movementManager.getFriendMovements(page, pageSize);
    }

    /**
     * 推荐动态查询
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return
     */
    @GetMapping("/movements/recommend")
    public ResponseEntity findRecommendMovementByPage(
            @RequestParam(value = "page", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "8") int pageSize){
        return movementManager.findRecommendMovementByPage(pageNum,pageSize);
    }

    /**
     * 动态点赞
     * @param movementId 动态id
     * @return
     */
    @GetMapping("/movements/{movementId}/like")
    public ResponseEntity saveMovementTypeLike(@PathVariable String movementId){
        return movementManager.saveMovementType(movementId,1);
    }

    /**
     * 取消点赞
     * @param movementId 动态id
     * @return
     */
    @GetMapping("/movements/{movementId}/dislike")
    public ResponseEntity deleteMovementTypeLike(@PathVariable String movementId){
        return movementManager.deleteMovementType(movementId,1);
    }

    /**
     * 动态喜欢
     * @param movementId 动态id
     * @return
     */
    @GetMapping("/movements/{movementId}/love")
    public ResponseEntity saveMovementTypeMove(@PathVariable String movementId){
        return movementManager.saveMovementType(movementId,3);
    }

    /**
     * 取消动态喜欢
     * @param movementId 动态id
     * @return
     */
    @GetMapping("/movements/{movementId}/unlove")
    public ResponseEntity deleteMovementTypeMove(@PathVariable String movementId){
        return movementManager.deleteMovementType(movementId,3);
    }

    /**
     * 查找单条动态
     * @param movementId 动态id
     * @return
     */
    @GetMapping("/movements/{movementId}")
    public ResponseEntity findMovementByMovementId(@PathVariable String movementId){
        return movementManager.findMovementByMovementId(movementId);
    }

    /**
     * 查询动态评论列表
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param movementId 动态id
     * @return
     */
    @GetMapping("/comments")
    public ResponseEntity findMovementComment(
            @RequestParam(value = "page",defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize",defaultValue = "8") Integer pageSize,
            @RequestParam String movementId){
        return movementManager.findMovementComment(pageNum,pageSize,movementId);
    }

    /**
     * 保存动态评论内容
     * @param map 评论对象
     * @return
     */
    @PostMapping("/comments")
    public ResponseEntity saveMovementComment(@RequestBody Map<String,String> map){

        //1.获取动态id和评论内容
        String movementId = map.get("movementId");
        String content = map.get("comment");

        //2.调用manager保存评论
        return movementManager.saveMovementComment(movementId,content);
    }
}
