package com.simple.game.server.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.simple.game.core.exception.BizException;
import com.simple.game.server.cmd.rtn.RtnResult;

import lombok.extern.slf4j.Slf4j;
 
/***
 * 全局异常
 * 
 * @author Administrator
 *
 */
@Slf4j
@ControllerAdvice
public class MyControllerAdvice {
 
    /**
     * 全局异常捕捉处理
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public RtnResult<?> errorHandler(Exception ex) {
    	log.warn(ex.getMessage(), ex);
        return RtnResult.failure(ex.getMessage());
    }
    
    /**
     * 拦截捕捉自定义异常 BizException.class
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = BizException.class)
    public RtnResult<?> myErrorHandler(BizException ex) {
    	log.warn(ex.getMessage(), ex);
        return RtnResult.failure(ex.getMessage());
    }
 
}