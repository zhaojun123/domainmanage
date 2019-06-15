package com.zkml.domainmanage.support.metadata;

import java.util.List;

/**
 * DomainMetadata处理器
 */
public interface DomainMetadataHandle {

    /**
     * 初始化DomainMetadata,这里传入的DomainMetadata包含localPath、fileName,content、className
     * 需要解析fieldMetadataList、methodMetadataList、importList、classNote、fullClassName
     * @param domainMetadata
     */
    DomainMetadata init(DomainMetadata domainMetadata);

    /**
     * 删除field
     * @param fieldMetadata 需要删除的fieldMetadata
     * @param domainMetadata field所属的domainMetadata
     * @param status ALL,GET,SET,NONE 是否对get/set方法进行操作
     */
    void delete(FieldMetadata fieldMetadata, DomainMetadata domainMetadata,FieldMetadata.Status status);

    /**
     * 批量删除field
     * @param fieldMetadataList
     * @param domainMetadata
     * @param status
     */
    void delete(List <FieldMetadata> fieldMetadataList, DomainMetadata domainMetadata,FieldMetadata.Status status);

    /**
     * 添加field
     * @param fieldMetadata 包含fieldContent getMethodContent，setMethodContent，sortNo，status
     * @param domainMetadata field所属的domainMetadata
     * @param status ALL,GET,SET,NONE 是否对get/set方法进行操作
     */
    void add(FieldMetadata fieldMetadata,DomainMetadata domainMetadata,FieldMetadata.Status status);

    /**
     * 批量添加field
     * @param fieldMetadataList
     * @param domainMetadata
     * @param status
     */
    void add(List<FieldMetadata> fieldMetadataList,DomainMetadata domainMetadata,FieldMetadata.Status status);

    /**
     * 对class的 annotation note进行修改
     * @param classMetadata
     * @param domainMetadata
     */
    void update(ClassMetadata classMetadata,DomainMetadata domainMetadata);
    /**
     * 修改field
     * @param fieldMetadata
     * @param domainMetadata field所属的domainMetadata
     * @param status ALL,GET,SET,NONE 是否对get/set方法进行操作
     */
    void update(FieldMetadata fieldMetadata,DomainMetadata domainMetadata,FieldMetadata.Status status);

    /**
     * 批量修改field
     * @param fieldMetadataList
     * @param domainMetadata
     * @param status
     */
    void update(List<FieldMetadata> fieldMetadataList,DomainMetadata domainMetadata,FieldMetadata.Status status);
    /**
     * 批量添加import
     * @param importList
     * @param domainMetadata
     */
    void add(List<OtherMetadata> importList,DomainMetadata domainMetadata);

    /**
     * 批量删除import
     * @param importList
     * @param domainMetadata
     */
    void delete(List<OtherMetadata> importList,DomainMetadata domainMetadata);

}
