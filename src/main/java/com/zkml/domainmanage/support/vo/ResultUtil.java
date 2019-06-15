package com.zkml.domainmanage.support.vo;

import com.zkml.domainmanage.support.exception.DomainServiceException;

/**
 * 返回结果工具类
 */
public class ResultUtil {

    public static <T> ResultVO<T> successResult(T model){
        return successResult(model,null);
    }

    public static <T> ResultVO<T> successResult(String message){
        return successResult(null,message);
    }

    public static <T> ResultVO<T> successResult(T model,String message){
        return new ResultVO(ResultVO.SUCCESS,model,message);
    }

    public static <T> ResultVO<T> failResult(String message){
        return new ResultVO(ResultVO.FAIL,null,message);
    }

    public static <T> ResultVO<T> failResult(T model,String message){
        return new ResultVO(ResultVO.FAIL,model,message);
    }

    public static <T> ResultVO<T> failResult(DomainServiceException e){
        return new ResultVO(ResultVO.FAIL,null,e.getMessage());
    }
}
