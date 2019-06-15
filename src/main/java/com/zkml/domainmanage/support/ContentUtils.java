package com.zkml.domainmanage.support;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文本工具类
 */
public class ContentUtils {

    /**
     * 获取字符串前面的空格数
     * @param content
     * @return
     */
    public static int getFrontBlank(String content){
        if(content == null)
            return 0;
        int blank = 0;
        for(int i=0;i<content.length();i++){
            char s = content.charAt(i);
            //如果是tab则默认补4个空格
            if(s == '\t'){
                blank = blank + 4;
            }else if(s<= ' '){
                blank++;
            }else{
                break;
            }
        }
        return blank;
    }


    /**
     * 在单行文本之前添加空格，如果是负数则减空格
     * @param blankCount
     * @param content
     * @return
     */
    public static String addBlankSingleLine(String content,int blankCount){
        if(blankCount ==0)
            return content;
        StringBuilder contentBuilder = new StringBuilder(content);
        if(blankCount>0){
            for(int i=0;i<blankCount;i++){
                contentBuilder.insert(0," ");
            }
            return contentBuilder.toString();
        }else{
            return contentBuilder.substring(Math.abs(blankCount));
        }
    }

    /**
     * 在保证整个文本内部格式不变的情况下 调整整个文本的前置空格数
     * @param frontBlankCount
     * @param content
     * @return
     */
    public static String trimFrontBlankContent(String content,int frontBlankCount){
        if(content ==null)
            return content;
        //找出最靠近左端的那一行文本，作为调整的标准
        String[] lines = content.split("\n");
        int addBlankCount = Integer.MAX_VALUE;
        for(String contentLine:lines){
            //如果是空行则略过
            if(StringUtils.isBlank(contentLine))
                continue;
            int frontBlank = getFrontBlank(contentLine);
            if(frontBlank<addBlankCount)
                addBlankCount = frontBlank;
        }
        //每一行文本都需要添加的前置空格数
        addBlankCount = frontBlankCount - addBlankCount;

        StringBuilder contentBuilder = new StringBuilder();
        for(String contentLine:lines){
            contentBuilder.append("\n").append(addBlankSingleLine(contentLine,addBlankCount));
        }
        return contentBuilder.substring(1);
    }

    /**
     * 去除前后的空行
     * @param content
     * @return
     */
    public static String trimBlankLines(String content){
        if(content == null)
            return content;
        List<String> lineList = Arrays.stream(content.split("\n",-1)).collect(Collectors.toList());
        StringBuilder result = new StringBuilder();
        int index =0;
        int size = lineList.size();
        while(index<size && StringUtils.isBlank(lineList.get(0))){
            lineList.remove(0);
            index++;
        }
        index = lineList.size();
        while(index >0 && StringUtils.isBlank(lineList.get(lineList.size()-1))){
            lineList.remove(lineList.size()-1);
            index--;
        }
        for(String line:lineList){
            result.append("\n").append(line);
        }
        return result.length()>0?result.substring(1):"";
    }

    /**
     * 根据符号分割，并且过滤掉空的字符串
     * @param content
     * @return
     */
    public static List<String> getStringSplit(String content, String charSplit){
        String[] chars = content.split(charSplit);
        List<String> result = Arrays.asList(chars).stream().filter(
                string->StringUtils.isNotBlank(string)
        ).collect(Collectors.toList());
        return result;
    }

    /**
     * 在文本前面设置空行，如果有多余的空行也会删除
     * @param content
     * @param count
     * @return
     */
    public static String setFrontBlankLine(String content,int count){
        if(content == null)
            return content;
        content = trimBlankLines(content);
        StringBuilder s = new StringBuilder(content);
        for(int i=0;i<count;i++){
            s.insert(0,"\n");
        }
        return s.toString();
    }

    /**
     * 将几个文本整合一起，保持一致的前置空格数
     * @param frontBlankCount
     * @param contents
     * @return
     */
    public static String joinContents(int frontBlankCount,List<String> contents){
        StringBuffer result = new StringBuffer();
        for(String s:contents){
            if(StringUtils.isNotBlank(s)){
                result.append("\n").append(trimFrontBlankContent(s,frontBlankCount));
            }
        }
        return result.length()>0?result.substring(1):"";
    }

    /**
     * 根据匹配条件匹配内容，返回符合匹配的行数集合（从1开始计数）
     * @param query
     * @param content
     * @return
     */
    public static List<Integer> queryContentLine(String query,String content){
        List<Integer> resultList = new ArrayList<>();
        if(StringUtils.isBlank(query) || StringUtils.isBlank(content))
            return resultList;
        query = query.trim();
        String[] contentList = content.toLowerCase().split("\n");
        for(int i=0;i<contentList.length;i++){
            if(contentList[i].indexOf(query.toLowerCase())>=0)
                resultList.add(i+1);
        }
        return resultList;
    }
}
