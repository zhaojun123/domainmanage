package com.zkml.domainmanage.support.metadata.validation;

import com.zkml.domainmanage.support.metadata.*;
import com.zkml.domainmanage.support.metadata.process.AfterInitMetadataProcess;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析javax.validation.constraints 类型的注解
 */
public class ValidationProcess implements AfterInitMetadataProcess{

    @Override
    public void afterInitProcess(DomainMetadata domainMetadata) {
        //过滤class中的annotation
        ClassMetadata classMetadata = domainMetadata.getClassMetadata();
        if(classMetadata!=null)
            classMetadata.setAnnotationList(switchValidation(classMetadata.getAnnotationList()));
        //过滤field中的annotation
        for(FieldMetadata fieldMetadata:domainMetadata.getFieldMetadataList()){
            fieldMetadata.setAnnotationList(switchValidation(fieldMetadata.getAnnotationList()));
            MethodMetadata gMethod = fieldMetadata.getgMethod();
            if(gMethod!=null){
                gMethod.setAnnotationList(switchValidation(gMethod.getAnnotationList()));
            }
            MethodMetadata sMethod = fieldMetadata.getsMethod();
            if(sMethod!=null){
                sMethod.setAnnotationList(switchValidation(sMethod.getAnnotationList()));
            }
        }
    }

    /**
     * 过滤annotation，找出validationAnnotation进行转换
     * @param list
     * @return
     */
    private List<BaseAnnotationMetadata> switchValidation(List<BaseAnnotationMetadata> list){
        return list.stream().map(annotation->{
                ValidationMetadata validationMetadata = ValidationMetadata.createOf(annotation);
                if(validationMetadata == null){
                    return annotation;
                }else{
                    return validationMetadata;
                }
            }).collect(Collectors.toList());
    }
}
