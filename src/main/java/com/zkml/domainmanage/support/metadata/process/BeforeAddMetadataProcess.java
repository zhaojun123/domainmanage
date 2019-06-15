package com.zkml.domainmanage.support.metadata.process;

import com.zkml.domainmanage.support.metadata.*;

import java.util.List;

/**
 * 在baseMetada添加前触发 see
 * {@link DomainMetadataHandle#add(FieldMetadata, DomainMetadata)}
 * {@link DomainMetadataHandle#update(ClassMetadata, DomainMetadata)}
 */
public interface BeforeAddMetadataProcess {

    /**
     * 在baseMetada添加前触发,可以对将要添加的baseMetada、以及所属的DomainMetadata进行操作
     * @param baseMetadaList 将要进行添加的BaseMetada集合
     * @param domainMetadata
     * @param domainMetadataHandle
     */
    void beforeAddProcess(List<? extends BaseMetada> baseMetadaList, DomainMetadata domainMetadata, DomainMetadataHandle domainMetadataHandle);

}
