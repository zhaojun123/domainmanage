package com.zkml.domainmanage.support;

import com.zkml.domainmanage.support.metadata.DomainMetadata;
import com.zkml.domainmanage.support.metadata.DomainMetadataHandle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * domain上下文环境，记录了domain的缓存
 */
public class DomainContext {

    private  List<DomainMetadata> domainTreeCache; //domain树缓存

    public   List<DomainMetadata> getDomainTreeCache() {
        return domainTreeCache;
    }

    /**
     * 获取所有domain的集合
     * @return
     */
    public List<DomainMetadata> getDomainList(){
        List<DomainMetadata> result = new ArrayList<>();
        if(!CollectionUtils.isEmpty(domainTreeCache)){
            for(DomainMetadata domainMetadata:domainTreeCache){
                addDomains(domainMetadata,result);
            }
        }
        return result;
    }

    private void addDomains(DomainMetadata domainMetadata,List<DomainMetadata> list){
        list.add(domainMetadata);
        for(DomainMetadata childDomainMetadata:domainMetadata.getDomainMetadataList()){
            addDomains(childDomainMetadata,list);
        }
    }

    /**
     * 根据fileKey从缓存查询DomainMetadata
     * @param fileKey
     * @return
     */
    public DomainMetadata getDomainByKey(String fileKey){
        List<DomainMetadata> domainList = getDomainList();
        for(DomainMetadata domainMetadata:domainList){
            if(domainMetadata.getFileKey().equals(fileKey))
                return domainMetadata;
        }
        return null;
    }

    /**
     * 初始化DomainMetadataTree
     * @param localPathList
     * @return
     */
    public void initDomainMetadataTree(List<String> localPathList,DomainMetadataHandle domainMetadataHandle) throws IOException {
        List<DomainMetadata> resultList = new ArrayList<>();
        for(String localPath:localPathList){
            if(StringUtils.isNotBlank(localPath)){
                DomainFileUtils.Directory directory = DomainFileUtils.scanDirectory(localPath);
                DomainMetadata domainMetadata = DomainMetadata.create(domainMetadataHandle,directory);
                resultList.add(domainMetadata);
            }
        }
        domainTreeCache = resultList;
    }
}
