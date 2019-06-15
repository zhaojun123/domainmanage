package com.zkml.domainmanage.support;

import org.springframework.beans.BeanUtils;

/**
 * bean copy工具类
 */
public class BeanCopyUtils {

    /**
     * copy
     * @param source
     * @param target
     */
    public static void copyBean(Object source,Object target){

        BeanUtils.copyProperties(source,target);
    }

}
