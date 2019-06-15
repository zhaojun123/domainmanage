package com.zkml.domainmanage.support.input;

import com.zkml.domainmanage.support.metadata.FieldMetadata;
import com.zkml.domainmanage.support.metadata.validation.ValidationEnum;
import com.zkml.domainmanage.support.metadata.validation.ValidationMetadata;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 添加field
 */
public class AddFieldInput {

    //文件key字符串，用英文逗号隔开
    @NotBlank(message = "fileKeys不能为空")
    private String fileKeys;

    //field名称
    @NotBlank(message = "field名不能为空")
    private String fieldName;

    //field类型
    @NotBlank(message = "field类型不能为空")
    private String fieldType;

    //注释
    @NotBlank(message = "注释不能为空")
    private String note;
    //javax.validation验证注解
    private List<ValidationEnum> validations = new ArrayList<>();

    //验证失败提示信息
    private String validationMessage;

    //设置GET SET方法
    @NotNull
    private FieldMetadata.Status status;

    public List<ValidationEnum> getValidations() {
        return validations;
    }

    public void setValidations(List<ValidationEnum> validations) {
        this.validations = validations;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    public FieldMetadata.Status getStatus() {
        return status;
    }

    public void setStatus(FieldMetadata.Status status) {
        this.status = status;
    }

    public String getFileKeys() {
        return fileKeys;
    }

    public void setFileKeys(String fileKeys) {
        this.fileKeys = fileKeys;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
