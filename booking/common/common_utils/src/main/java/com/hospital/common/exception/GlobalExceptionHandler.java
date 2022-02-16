package com.hospital.common.exception;

import com.hospital.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 定义异常发生的归属异常类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

//   使用已有异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    //   使用自定义异常
    @ExceptionHandler(bookException.class)
    @ResponseBody
    public Result error(bookException e){
        e.printStackTrace();
        return Result.fail();
    }
}
