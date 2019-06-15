package com.zkml.domainmanage.support.metadata.process;

import com.zkml.domainmanage.support.metadata.DomainMetadata;
import com.zkml.domainmanage.support.metadata.DomainMetadataHandle;

/**
 * 在DomainMetadata初始化后触发，可以在这里自定义对domainMetadata的分析
 * see {@link DomainMetadataHandle#init(DomainMetadata)}
 *
 */
public interface AfterInitMetadataProcess {

    void afterInitProcess(DomainMetadata domainMetadata);

}
