package com.zkml.domainmanage.support.metadata.imports;

import com.zkml.domainmanage.support.DomainManageProperties;
import com.zkml.domainmanage.support.properties.AbstractProperties;
import com.zkml.domainmanage.support.properties.PropertiesMetadate;

import java.io.*;
import java.util.*;

/**
 * 通过properties读取import信息 见{@link DomainManageProperties#getImportPropertiesPath()}
 */
public class PropertiesImportSource extends AbstractProperties implements ImportSource{
    

    public PropertiesImportSource(String propertiesPath) throws IOException {
        super(propertiesPath);
    }

    /**
     * 每次获取import都读取一次文件，保证实时修改可以生效
     * @param classType
     * @return
     */
    @Override
    public String getImport(String classType) {

        return getProperties().getProperty(classType);
    }

    /**
     * 获取所有import
     * @return
     */
    public Map<String,PropertiesMetadate> getImports(){
        return getPropertiesMap();
    }

    /**
     * 添加import
     * @param name
     * @param value
     */
    public void addImport(String name,String value){
        storeProperties(name,value,null);
    }

    /**
     * 删除import
     * @param name
     */
    public void deleteImport(String name){
        deleteProperties(name);
    }

}
