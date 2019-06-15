package com.zkml.domainmanage.support.metadata;

import com.zkml.domainmanage.support.metadata.validation.ValidationMetadata;

/**
 * annotation描述
 * @param <T>
 */
public interface AnnotationDescription<T>{

    /**
     * 获取注解名称
     * @return
     */
    String getAnnotationName();

    /**
     * 获取对应的metadataclass
     * @return
     */
    Class getMetadataClass();

    /**
     * 创建相应的Metadata
     * @return
     */
    default T createMetadata(){
        try {
            return (T) getMetadataClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
