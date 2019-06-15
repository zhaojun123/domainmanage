package com.zkml.domainmanage.support.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * field页面模型
 */
public class FieldVO {

    private String fieldName; //field名

    private String fieldType; //field名类型

    private int sortNo = -1; //排序

    private String note; //注释

    private Map<String,ValidationVO> validationMap = new HashMap<>(); //支持哪些认证注解 key=ValidationEnum.name()

    private String validationMessage; //验证错误的提示文本

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    public Map<String, ValidationVO> getValidationMap() {
        return validationMap;
    }

    public void setValidationMap(Map<String, ValidationVO> validationMap) {
        this.validationMap = validationMap;
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

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
