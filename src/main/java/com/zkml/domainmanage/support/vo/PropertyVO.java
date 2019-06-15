package com.zkml.domainmanage.support.vo;

/**
 * Property模型
 */
public class PropertyVO {

    private String name;

    private String value;

    private String comment;

    public PropertyVO(String name, String value,String comment) {
        this.name = name;
        this.value = value;
        this.comment = comment;
    }

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
