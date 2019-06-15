package com.zkml.domainmanage.support.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * domain页面模型
 */
public class DomainVO {

    private String fileKey; //唯一标示

    private String className; //class名称

    private boolean directory = false; //是否目录

    private String parentFileKey; //父

    private int level = 0; //级别

    private String note; //注释

    private List<FieldVO> fieldList; //field列表

    private List<QueryContentVO> queryContentList = new ArrayList<>(); //匹配搜索的内容

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<QueryContentVO> getQueryContentList() {
        return queryContentList;
    }

    public void setQueryContentList(List<QueryContentVO> queryContentList) {
        this.queryContentList = queryContentList;
    }

    public List<FieldVO> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldVO> fieldList) {
        this.fieldList = fieldList;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public String getParentFileKey() {
        return parentFileKey;
    }

    public void setParentFileKey(String parentFileKey) {
        this.parentFileKey = parentFileKey;
    }
}
