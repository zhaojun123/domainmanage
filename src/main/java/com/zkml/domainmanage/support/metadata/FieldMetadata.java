package com.zkml.domainmanage.support.metadata;

import com.zkml.domainmanage.support.ContentUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * field模型，开始行数、结束行数、内容 包括了注解、注释
 */
public class FieldMetadata extends BaseTypeMetadata{

    public FieldMetadata(){
        super();
    }

    private String fieldName; //field名

    private String fieldType; //field名类型

    private MethodMetadata gMethod; // getMethod;

    private MethodMetadata sMethod; // setMethod;

    private Status status; //get/set  添加/删除的时候会根据这个选择性添加/删除

    private int frontBlankLineCount = 1; //前置空行数，和前面同类型元素保持的间距数

    private int frontBlankCount = 4; //前置空格数

    @Override
    public int getFrontBlankCount() {
        return frontBlankCount;
    }

    @Override
    public void setFrontBlankCount(int frontBlankCount) {
        this.frontBlankCount = frontBlankCount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public MethodMetadata getgMethod() {
        return gMethod;
    }

    public void setgMethod(MethodMetadata gMethod) {
        this.gMethod = gMethod;
    }

    public MethodMetadata getsMethod() {
        return sMethod;
    }

    public void setsMethod(MethodMetadata sMethod) {
        this.sMethod = sMethod;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    public int getFrontBlankLineCount() {
        return frontBlankLineCount;
    }

    @Override
    public void setFrontBlankLineCount(int frontBlankLineCount) {
        this.frontBlankLineCount = frontBlankLineCount;
    }

    public FieldMetadata(Builder builder){
        this.fieldName = builder.fieldName;
        this.fieldType = builder.fieldType;
        if(StringUtils.isNotBlank(builder.gMethodContent))
            this.gMethod = new MethodMetadata(builder.gMethodContent);
        if(StringUtils.isNotBlank(builder.sMethodContent))
            this.sMethod = new MethodMetadata(builder.sMethodContent);
        this.setNoteList(builder.noteList);
        this.setAnnotationList(builder.annotationList);
        this.status = builder.status;
        this.setStartLine(builder.startLine);
        this.setEndLine(builder.endLine);
        this.setContent(builder.content);
        this.frontBlankCount = builder.frontBlankCount;
    }

    //设置GET/SET
    public enum Status{

        ALL,GET,SET,NONE

    }

    public static class Builder{

        private String fieldName; //field名

        private String fieldType; //field名类型

        private String gMethodContent; // get方法内容

        private String sMethodContent; // set方法内容

        private List<OtherMetadata> noteList = new ArrayList<>(); //注释内容

        private List<BaseAnnotationMetadata> annotationList = new ArrayList<>();//注解内容

        private Status status; //get/set

        private int startLine = 0;

        private int endLine = 0;

        private String content;

        private int frontBlankCount = 4; //前置空格数

        public Builder fieldName(String fieldName){
            this.fieldName = fieldName;
            return this;
        }

        public Builder fieldType(String fieldType){
            this.fieldType = fieldType;
            return this;
        }

        public Builder gMethodContent(String gMethodContent){
            this.gMethodContent = gMethodContent;
            return this;
        }

        public Builder sMethodContent(String sMethodContent){
            this.sMethodContent = sMethodContent;
            return this;
        }

        public Builder note(String note){
            this.noteList.add(new OtherMetadata(note, OtherMetadata.MetadataType.NOTE));
            return this;
        }

        public Builder note(OtherMetadata note){
            this.noteList.add(note);
            return this;
        }

        public Builder notes(Collection<OtherMetadata> notes){
            this.noteList.addAll(notes);
            return this;
        }

        public Builder annotation(AnnotationMetadata annotation){
            annotationList.add(annotation);
            return this;
        }

        public Builder annotations(Collection<AnnotationMetadata> annotations){
            annotationList.addAll(annotations);
            return this;
        }

        public Builder status(Status status){
            this.status = status;
            return this;
        }

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

        public FieldMetadata build(){
            return new FieldMetadata(this);
        }

    }
}
