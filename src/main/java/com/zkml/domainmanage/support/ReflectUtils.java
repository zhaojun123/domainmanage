package com.zkml.domainmanage.support;

public class ReflectUtils {

    /**
     * 根据fieldName获取getMethodName
     * @param fieldName
     * @return
     */
    public static String getMethodName(String fieldName){
        String firstLetter = fieldName.substring(0,1).toUpperCase();
        String getMethodName = "get" + firstLetter + fieldName.substring(1);
        return getMethodName;
    }

    /**
     * 根据fieldName获取setMethodName
     * @param fieldName
     * @return
     */
    public static String setMethodName(String fieldName){
        String firstLetter = fieldName.substring(0,1).toUpperCase();
        String setMethodName = "set" + firstLetter + fieldName.substring(1);
        return setMethodName;
    }

}
