package com.zkml.domainmanage.support.input;

import javax.validation.constraints.NotBlank;

public class PropertiesInput {

    @NotBlank(message = "name不能为空")
    private String name;

    @NotBlank(message = "value不能为空")
    private String value;

    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
