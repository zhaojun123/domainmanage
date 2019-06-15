package com.zkml.domainmanage.support.metadata.imports;

import com.zkml.domainmanage.support.DomainContext;
import com.zkml.domainmanage.support.DomainUtils;
import com.zkml.domainmanage.support.metadata.DomainMetadata;
import com.zkml.domainmanage.support.metadata.OtherMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 从domain缓存中获取import信息
 */
public class CacheImportSource implements ImportSource{

    private DomainContext domainContext;

    public CacheImportSource(DomainContext domainContext){

        this.domainContext = domainContext;
    }

    @Override
    public String getImport(String classType) {
        List<DomainMetadata> domainMetadataList = domainContext.getDomainTreeCache();
        if(domainMetadataList == null)
            return null;
        Map<String,String> importMap = null;
        for(DomainMetadata domainMetadata:domainMetadataList){
            importMap = getImportMap(domainMetadata,importMap);
        }
        return importMap.get(classType);
    }

    /**
     * 分析domainMetadataCache 获取import Map集合key=fileType，value=import
     * import获取来源，1、通过类全名称，2、通过DomainMetadata的importList
     * @param importMap
     * @return
     */
    private Map<String,String> getImportMap(DomainMetadata domainMetadata,Map<String,String> importMap){
        if(importMap == null)
            importMap = new HashMap<>();
        if(!domainMetadata.isDirectory()){
            String fullClassName =  domainMetadata.getFullClassName();
            String classType = DomainUtils.getClassTypeByImport(fullClassName);
            importMap.put(classType,fullClassName);
            List<OtherMetadata> importList = domainMetadata.getImportList();
            for(OtherMetadata importMetadata:importList){
                importMap.put(DomainUtils.getClassTypeByImport(importMetadata.getContent())
                        ,importMetadata.getContent());
            }
        }
        for(DomainMetadata childDomain:domainMetadata.getDomainMetadataList()){
            getImportMap(childDomain,importMap);
        }
        return importMap;
    }
}
