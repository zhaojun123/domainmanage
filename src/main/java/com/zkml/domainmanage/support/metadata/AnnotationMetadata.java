package com.zkml.domainmanage.support.metadata;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用注解模型
 */
public class AnnotationMetadata extends BaseAnnotationMetadata{

    public AnnotationMetadata(){

    }

    public AnnotationMetadata(Builder builder){
        this.setStartLine(builder.startLine);
        this.setEndLine(builder.endLine);
        this.setContent(builder.content);
        this.setFrontBlankCount(builder.frontBlankCount);
        this.setSortNo(builder.sortNo);
        this.setAnnotationName(builder.annotationName);
    }


    @Override
    public String assembleContent() {
        return getContent();
    }

    @Override
    public void analysisContent() {

    }

    public static class Builder{

        private int startLine = 0; //开始行数

        private int endLine = 0; //结束行数

        private String content; //文件内容

        private int frontBlankCount = 0; //前置空格数

        private int sortNo = -1; //与同类元素的排序 从0开始 -1表示排队尾

        private String annotationName; //注解名称

        public Builder startLine(int startLine){
            this.startLine = startLine;
            return this;
        }

        public Builder endLine(int endLine){
            this.endLine = endLine;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder frontBlankCount(int frontBlankCount){
            this.frontBlankCount = frontBlankCount;
            return this;
        }

        public Builder sortNo(int sortNo){
            this.sortNo = sortNo;
            return this;
        }

        public Builder annotationName(String annotationName){
            this.annotationName = annotationName;
            return this;
        }

        public AnnotationMetadata build(){
            return new AnnotationMetadata(this);
        }
    }
}
