package com.zkml.domainmanage.support;

import com.zkml.domainmanage.support.metadata.imports.CacheImportSource;
import com.zkml.domainmanage.support.metadata.imports.DomainImportSupport;
import com.zkml.domainmanage.support.metadata.imports.ImportSource;
import com.zkml.domainmanage.support.metadata.imports.PropertiesImportSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@AutoConfigureAfter(DomainManageAutoConfig.class)
public class ImportSupportAutoConfig {

    private DomainManageProperties domainManageProperties;

    public ImportSupportAutoConfig(DomainManageProperties domainManageProperties){
        this.domainManageProperties = domainManageProperties;
    }

    private List<ImportSource> importSourceList = new ArrayList<>();

    /**
     * 可以自己实现ImportSource接口
     * 在根据classType获取import的时候会依次调用getImport进行解析获取所需要的import
     * see {@link DomainImportSupport#getImport(List)}
     * @param importSourceList
     */
    @Autowired(required = false)
    public void setImportSourceList(List<ImportSource> importSourceList){
        if (!CollectionUtils.isEmpty(importSourceList)) {
            this.importSourceList.addAll(importSourceList);
        }
    }

    /**
     * 提供对import的支持，根据classType获取相应需要的import
     */
    @Bean
    DomainImportSupport domainImportSupport(DomainContext domainContext) throws IOException {
        return new DomainImportSupport(importSourceList);
    }

    /**
     * 从已有的domain中获取import
     * @param domainContext
     * @return
     */
    @Bean
    CacheImportSource  cacheImportSource(DomainContext domainContext){
        return new CacheImportSource(domainContext);
    }

    /**
     * 从properties中获取import
     * @return
     * @throws IOException
     */
    @Bean
    PropertiesImportSource propertiesImportSource() throws IOException {
        String propertiesPath = domainManageProperties.getImportPropertiesPath();
        if(StringUtils.isBlank(propertiesPath)){
            propertiesPath = System.getProperty("user.dir")+"/import.properties";
            File file = new File(propertiesPath);
            if(!file.exists())
                file.createNewFile();
            propertiesPath = "file:"+propertiesPath;
        }
        return new PropertiesImportSource(propertiesPath);
    }

}
