package com.itlyc.app.exception;

import com.itlyc.domain.vo.ErrorResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 统一异常处理
 */
@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity exceptionHandler(Exception e){

        e.printStackTrace();

        return ResponseEntity.status(500).body(ErrorResult.error());
    }
}
