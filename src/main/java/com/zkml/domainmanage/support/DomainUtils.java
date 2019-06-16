package com.zkml.domainmanage.support;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * domain工具类
 */
public class DomainUtils {

    /**
     * 基础类型
     */
    public static String[] baseType = {"int","long","double","float","char","byte","short","boolean"
            ,"Integer","Long","Double","Float","Character","Byte","Short","Boolean","String"};

    /**
     * 可以被导出field字典的类型
     */
    public static String[] fieldDictionaryType ={"int","long","double","float","char","byte","short","boolean"
            ,"Integer","Long","Double","Float","Character","Byte","Short","Boolean","String","Date","BigDecimal"};

    //查询field的正则表达式
    private static Pattern PATTERN_FILED = Pattern.compile("^(private|public)((?!\\().)*(;)(.)*");

    //查询方法的正则表达式
    private static Pattern PATTERN_METHOD = Pattern.compile("^(private|public|protected)(.)*(\\()(.)*(\\))(\\s)*(\\{)(.)*");

    public static Boolean isFieldDictionaryType(String classType){
        return Arrays.asList(fieldDictionaryType).contains(classType);
    }

    /**
     * 查询是否是基础类型
     * @return
     */
    public static Boolean isBaseType(String classType){
        return Arrays.asList(baseType).contains(classType);
    }

    /**
     * 清除import的 import前缀，；符号
     * @param importName
     * @return
     */
    public static String cleanImport(String importName){
        if(importName == null)
            return null;
        return importName.replace("import","").replace(";","").trim();
    }

    /**
     * 清除package的 package前缀, ;符号
     * @param packageName
     * @return
     */
    public static String cleanPackage(String packageName){
        if(packageName == null)
            return null;
        return packageName.replace("package","").replace(";","").trim();
    }

    /**
     * 将list循环合并成多行的文本
     * @param contentList
     * @return
     */
    public static String joinStringList(List<String> contentList){
        StringBuilder contentBuilder = new StringBuilder();
        for(String contentLine:contentList){
            contentBuilder.append("\n").append(contentLine);
        }
        return contentBuilder.substring(1);
    }

    /**
     * 将不可变的list copy成普通可变list
     * @param sourceList
     * @param <T>
     * @return
     */
    public static <T> List<T> copyModifiableList(List<T> sourceList){
        if(sourceList == null)
            return null;
        List<T> resultList = new ArrayList<>(sourceList.size());
        for(T object:sourceList){
            resultList.add(object);
        }
        return resultList;
    }

    /**
     * 根据import解析出classType
     * @param importName
     */
    public static String getClassTypeByImport(String importName){
        if(importName == null)
            return null;
        importName = cleanImport(importName);
        String[] s = importName.split("\\.");
        return s[s.length-1];
    }

    /**
     * 根据import获取属于哪个包
     * @param importName
     * @return
     */
    public static String getPackageByImport(String importName){
        if(importName == null)
            return null;
        importName = cleanImport(importName);
        int index = importName.lastIndexOf(".");
        return importName.substring(0,index);
    }

    /**
     * 判断是否是field
     * @param content
     * @return
     */
    public static boolean isMatcherField(String content){
        if(StringUtils.isBlank(content))
            return false;
        return PATTERN_FILED.matcher(content.trim()).matches();
    }

    /**
     * 判断是否是方法
     * @param content
     * @return
     */
    public static boolean isMatcherMethod(String content){
        if(StringUtils.isBlank(content))
            return false;
        return PATTERN_METHOD.matcher(content.trim()).matches();
    }

    /**
     * 在多行文本中获取fieldName
     * @param content
     * @return
     */
    public static String getFieldNameByContent(String content){
        if(StringUtils.isBlank(content))
            return null;
        for(String line:content.split("\n")){
            if(isMatcherField(line)){
                String[] chars;
                if(line.indexOf("=")>=0){
                    chars = line.split("=");
                }else{
                    chars = line.split(";");
                }
                List<String> fieldChars = ContentUtils.getStringSplit(chars[0],"\\s");
                return fieldChars.get(fieldChars.size()-1);
            }
        }
        return null;
    }

    /**
     * 在多行文本中获取fieldType
     * @param content
     * @return
     */
    public static String getFieldTypeByContent(String content){
        if(StringUtils.isBlank(content))
            return null;
        for(String line:content.split("\n")){
            if(isMatcherField(line)){
                String[] chars;
                if(line.indexOf("=")>=0){
                    chars = line.split("=");
                }else{
                    chars = line.split(";");
                }
                List<String> fieldChars = ContentUtils.getStringSplit(chars[0],"\\s");
                return fieldChars.get(fieldChars.size()-2);
            }
        }
        return null;
    }

    /**
     * 在多行文本中获取methodName
     * @param content
     * @return
     */
    public static String getMethodNameByContent(String content){
        if(StringUtils.isBlank(content))
            return null;
        for(String line:content.split("\n")){
            if(isMatcherMethod(line)){
                String[] chars = line.split("\\(");
                List<String> fieldChars = ContentUtils.getStringSplit(chars[0],"\\s");
                return fieldChars.get(fieldChars.size()-1);
            }
        }
        return null;
    }

    /**
     * 在多行文本中获取methodType
     * @param content
     * @return
     */
    public static String getMethodTypeByContent(String content){
        if(StringUtils.isBlank(content))
            return null;
        for(String line:content.split("\n")){
            if(isMatcherMethod(line)){
                String[] chars = line.split("\\(");
                List<String> fieldChars = ContentUtils.getStringSplit(chars[0],"\\s");
                return fieldChars.get(fieldChars.size()-2);
            }
        }
        return null;
    }

    /**
     * 在多行文本中获取到第一个注解的名称
     * @param content
     * @return
     */
    public static String getAnnotationNameByContent(String content){
        if(StringUtils.isBlank(content))
            return null;
        String annotationName;
        int index = content.indexOf("(");
        if(index>0){
            annotationName = content.substring(1,index);
        }else{
            annotationName = content.substring(1);
        }
        return annotationName;
    }

    /**
     * 去除注释的 /* // @ 等格式，获取比较干净的注释文本
     * @return
     */
    public static String processedNoteContent(String content){
        if(content == null)
            return null;
        String[] contentList = content.replace("/*","")
                .replace("*/","")
                .replace("*","")
                .replace("//","").trim().split("\n");
        StringBuffer result = new StringBuffer();
        for(String contentLine:contentList){
            contentLine =  contentLine.trim();
            if(StringUtils.isNotBlank(contentLine) && !contentLine.startsWith("@"))
                result.append(",").append(contentLine);
        }
        return result.length()>0?result.substring(1):"";
    }
}
