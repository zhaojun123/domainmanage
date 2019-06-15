package com.zkml.domainmanage.support.metadata.validation;

import com.zkml.domainmanage.support.BeanCopyUtils;
import com.zkml.domainmanage.support.metadata.BaseAnnotationMetadata;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * javax.validation验证注解通用模型
 */
public abstract class ValidationMetadata extends BaseAnnotationMetadata {

    private String message; //验证错误后的文本提示

    private ValidationEnum validationType; //类型

    public ValidationMetadata(ValidationEnum validationEnum){
        this.setAnnotationName(validationEnum.getAnnotationName());
        this.validationType = validationEnum;
    }

    public ValidationEnum getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationEnum validationType) {
        this.validationType = validationType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void analysisContent(){
        this.message = getFirstAttrByName("message",String.class);
    }

    /**
     * 检查传入的baseAnnotationMetadata是否是同一类型
     * @param baseAnnotationMetadata
     * @return
     */
    public static Boolean match(BaseAnnotationMetadata baseAnnotationMetadata){
        ValidationEnum validationEnum = ValidationEnum.getByAnnotationName(baseAnnotationMetadata.getAnnotationName());
        return validationEnum != null;
    }

    /**
     * 解析普通的annotationMetadata，生成ValidationMetadata
     * @param annotationMetadata
     * @return
     */
    public static ValidationMetadata createOf(BaseAnnotationMetadata annotationMetadata){
        if(annotationMetadata == null)
            return null;
        String annotationName = annotationMetadata.getAnnotationName();
        if(StringUtils.isBlank(annotationName))
            return null;
        ValidationEnum validationEnum = ValidationEnum.getByAnnotationName(annotationName);
        if(validationEnum == null)
            return null;
        ValidationMetadata validationMetadata =validationEnum.createMetadata();
        //从annotation copy 数据到 validationMetadata
        BeanCopyUtils.copyBean(annotationMetadata,validationMetadata);
        validationMetadata.analysisContent();
        return validationMetadata;
    }
}
