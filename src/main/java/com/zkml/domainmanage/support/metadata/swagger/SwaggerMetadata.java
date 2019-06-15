package com.zkml.domainmanage.support.metadata.swagger;

import com.zkml.domainmanage.support.BeanCopyUtils;
import com.zkml.domainmanage.support.metadata.BaseAnnotationMetadata;
import org.apache.commons.lang3.StringUtils;

/**
 * swagger 注解通用模型
 */
public abstract class SwaggerMetadata extends BaseAnnotationMetadata{

    public SwaggerMetadata(SwaggerEnum swaggerType){
        this.setAnnotationName(swaggerType.getAnnotationName());
        this.swaggerType = swaggerType;
    }

    //swagger注解类型
    private SwaggerEnum swaggerType;

    public SwaggerEnum getSwaggerType() {
        return swaggerType;
    }

    public void setSwaggerType(SwaggerEnum swaggerType) {
        this.swaggerType = swaggerType;
    }

    /**
     * 解析普通的annotationMetadata，SwaggerMetadata
     * @param annotationMetadata
     * @return
     */
    public static SwaggerMetadata createOf(BaseAnnotationMetadata annotationMetadata){
        if(annotationMetadata == null)
            return null;
        String annotationName = annotationMetadata.getAnnotationName();
        if(StringUtils.isBlank(annotationName))
            return null;
        SwaggerEnum swaggerEnum = SwaggerEnum.getByAnnotationName(annotationName);
        if(swaggerEnum == null)
            return null;
        SwaggerMetadata swaggerMetadata =swaggerEnum.createMetadata();
        //从annotation copy 数据到 swaggerMetadata
        BeanCopyUtils.copyBean(annotationMetadata,swaggerMetadata);
        swaggerMetadata.analysisContent();
        return swaggerMetadata;
    }

    /**
     * 检查传入的baseAnnotationMetadata是否是同一类型
     * @param baseAnnotationMetadata
     * @return
     */
    public static Boolean match(BaseAnnotationMetadata baseAnnotationMetadata){
        SwaggerEnum swaggerEnum = SwaggerEnum.getByAnnotationName(baseAnnotationMetadata.getAnnotationName());
        return swaggerEnum != null;
    }
}
