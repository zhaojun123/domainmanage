package com.zkml.domainmanage.support.metadata.validation;
import com.zkml.domainmanage.support.metadata.AnnotationDescription;
import org.apache.commons.lang3.StringUtils;

public enum ValidationEnum implements AnnotationDescription<ValidationMetadata>{

    NOT_EMPTY("NotEmpty",NotEmptyMetadata.class),
    NOT_BLANK("NotBlank",NotBlankMetadata.class),
    NOT_NULL("NotNull",NotNullMetadata.class);

    private String annotationName;

    private Class<? extends ValidationMetadata> metadataClass;

    @Override
    public Class<? extends ValidationMetadata> getMetadataClass() {
        return metadataClass;
    }

    @Override
    public String getAnnotationName() {
        return annotationName;
    }

    ValidationEnum(String annotationName, Class<? extends ValidationMetadata> metadataClass){
        this.annotationName = annotationName;
        this.metadataClass = metadataClass;
    }

    /**
     * 根据annotationName获取具体的ValidationEnum
     * @param annotationName
     * @return
     */
    public static ValidationEnum getByAnnotationName(String annotationName){
        if(StringUtils.isBlank(annotationName))
            return null;
        for(ValidationEnum validationEnum:ValidationEnum.values()){
            if(validationEnum.getAnnotationName().equals(annotationName))
                return validationEnum;
        }
        return null;
    }
}
