package com.zkml.domainmanage.support.metadata.swagger;

import com.zkml.domainmanage.support.metadata.BaseAnnotationMetadata;
import com.zkml.domainmanage.support.metadata.ClassMetadata;
import com.zkml.domainmanage.support.metadata.DomainMetadata;
import com.zkml.domainmanage.support.metadata.FieldMetadata;
import com.zkml.domainmanage.support.metadata.process.AfterInitMetadataProcess;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析swagger类型注解
 */
public class SwaggerProcess implements AfterInitMetadataProcess{

    @Override
    public void afterInitProcess(DomainMetadata domainMetadata) {
        //过滤class中的annotation
        ClassMetadata classMetadata = domainMetadata.getClassMetadata();
        if(classMetadata!=null)
            classMetadata.setAnnotationList(switchSwagger(classMetadata.getAnnotationList()));
        //过滤field中的annotation
        for(FieldMetadata fieldMetadata:domainMetadata.getFieldMetadataList()){
            fieldMetadata.setAnnotationList(switchSwagger(fieldMetadata.getAnnotationList()));
        }
    }

    /**
     * 过滤annotation，找出SwaggerAnnotation进行转换
     * @param list
     * @return
     */
    private List<BaseAnnotationMetadata> switchSwagger(List<BaseAnnotationMetadata> list){
        return list.stream().map(annotation->{
            SwaggerMetadata swaggerMetadata = SwaggerMetadata.createOf(annotation);
            if(swaggerMetadata == null){
                return annotation;
            }else{
                return swaggerMetadata;
            }
        }).collect(Collectors.toList());
    }

}
