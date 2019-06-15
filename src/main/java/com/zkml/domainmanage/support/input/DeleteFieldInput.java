package com.zkml.domainmanage.support.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 删除field
 */
public class DeleteFieldInput {

    //文件key字符串，用英文逗号隔开
    @NotBlank(message = "fileKeys不能为空")
    private String fileKeys;

    //field名称
    @NotBlank(message = "field名不能为空")
    private String fieldName;

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
}
