package com.zkml.domainmanage.support.metadata.swagger;

import com.zkml.domainmanage.support.metadata.AnnotationDescription;
import org.apache.commons.lang3.StringUtils;

public enum  SwaggerEnum implements AnnotationDescription<SwaggerMetadata>{

    API_MODEL_PROPERTY("ApiModelProperty",ApiModelPropertyMetadata.class),
    API_MODEL("ApiModel",ApiModelMetadata.class);

    private String annotationName;

    private Class<? extends SwaggerMetadata> metadataClass;

    @Override
    public Class<? extends SwaggerMetadata> getMetadataClass() {
        return metadataClass;
    }

    @Override
    public String getAnnotationName() {
        return annotationName;
    }

    SwaggerEnum(String annotationName, Class<? extends SwaggerMetadata> metadataClass){
        this.annotationName = annotationName;
        this.metadataClass = metadataClass;
    }

    /**
     * 根据annotationName获取具体的SwaggerEnum
     * @param annotationName
     * @return
     */
    public static SwaggerEnum getByAnnotationName(String annotationName){
        if(StringUtils.isBlank(annotationName))
            return null;
        for(SwaggerEnum swaggerEnum:SwaggerEnum.values()){
            if(swaggerEnum.getAnnotationName().equals(annotationName))
                return swaggerEnum;
        }
        return null;
    }

}
