package com.zkml.domainmanage.support.metadata.imports;

/**
 * import数据源
 */
public interface ImportSource {

    /**
     * 获取这个classType所需要的import
     * @param classType
     * @return
     */
    String getImport(String classType);

}
