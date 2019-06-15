package com.zkml.domainmanage.support.input;

import javax.validation.constraints.NotBlank;

/**
 * 调整field的位置
 */
public class SwitchFieldInput {

    //文件key字符串，用英文逗号隔开
    @NotBlank(message = "fileKey不能为空")
    private String fileKey;

    //field名称
    @NotBlank(message = "field名不能为空")
    private String fieldName;

    //排序值 越小越靠前
    private int sortNo = 0;

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }
}
