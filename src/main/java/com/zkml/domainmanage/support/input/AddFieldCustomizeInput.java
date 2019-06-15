package com.zkml.domainmanage.support.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 自定义添加field代码，
 * 有些特殊情况需要自己写一些复杂的代码，例如加一些自定义注解，在get/set里面写一些逻辑等
 */
public class AddFieldCustomizeInput {

    //文件key字符串，用英文逗号隔开
    @NotBlank(message = "fileKeys不能为空")
    private String fileKeys;

    @NotBlank(message = "file代码不能为空")
    private String fieldContent;

    private String importName;

    public String getFileKeys() {
        return fileKeys;
    }

    public void setFileKeys(String fileKeys) {
        this.fileKeys = fileKeys;
    }

    public String getImportName() {
        return importName;
    }

    public void setImportName(String importName) {
        this.importName = importName;
    }

    public String getFieldContent() {
        return fieldContent;
    }

    public void setFieldContent(String fieldContent) {
        this.fieldContent = fieldContent;
    }
}
