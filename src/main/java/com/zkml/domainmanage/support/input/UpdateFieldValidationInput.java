package com.zkml.domainmanage.support.input;

import com.zkml.domainmanage.support.metadata.validation.ValidationEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改field对应的validation注解模型
 */
public class UpdateFieldValidationInput {

    //field名称
    private String fieldName;

    //validation错误提示
    private String validationMessage;

    //validation注解
    private List<ValidationEnum> validationList = new ArrayList<>();

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    public List<ValidationEnum> getValidationList() {
        return validationList;
    }

    public void setValidationList(List<ValidationEnum> validationList) {
        this.validationList = validationList;
    }
}
