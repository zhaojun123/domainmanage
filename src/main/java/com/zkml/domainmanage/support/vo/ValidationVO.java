package com.zkml.domainmanage.support.vo;

import com.zkml.domainmanage.support.metadata.validation.ValidationEnum;

/**
 * javax.validation 注解页面模型
 */
public class ValidationVO {

    private String annotationName; //注解名

    private String message; //验证错误后的文本提示

    private ValidationEnum validationType; //类型

    public ValidationEnum getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationEnum validationType) {
        this.validationType = validationType;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
