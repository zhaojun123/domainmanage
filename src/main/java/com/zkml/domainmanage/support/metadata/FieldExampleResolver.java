package com.zkml.domainmanage.support.metadata;


import com.zkml.domainmanage.support.properties.AbstractProperties;
import com.zkml.domainmanage.support.properties.CommentProperties;
import com.zkml.domainmanage.support.properties.PropertiesMetadate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class FieldExampleResolver extends AbstractProperties{


        public FieldExampleResolver(String propertiesPath) throws IOException {
            super(propertiesPath);
        }

        /**
         * 获取fieldName相应的例子数据
         * @param fieldName
         * @return
         */
        public String getExample(String fieldName) {
            return getProperties().getProperty(fieldName,"");
        }

        /**
         * 获取所有Example， key=fieldName,value=PropertiesMetadate
         * @return
         */
        public Map<String, PropertiesMetadate> getExamples() {
            return getPropertiesMap();
        }

        /**
         * 添加example
         * @param fieldName
         * @param example
         * @param overwrite 如果有重复的key是否覆盖
         */
        public void addExample(String fieldName, String example,String comment, boolean overwrite) {
            CommentProperties properties = getProperties();
            if(properties.containsKey(fieldName)){
                if(overwrite)
                    storeProperties(fieldName,example,comment);
            }else{
                storeProperties(fieldName,example,comment);
            }
        }

        /**
         * 批量添加example key=fieldName,value=example
         * @param propertiesList
         * @param overwrite 如果有重复的key是否覆盖
         */
        public void batchAddExamples(List<PropertiesMetadate> propertiesList, boolean overwrite) {
            CommentProperties properties = getProperties();
            propertiesList = propertiesList.stream().filter(entry -> {
                if (properties.containsKey(entry.getKey())) {
                    if (overwrite)
                        return true;
                } else {
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            storeProperties(propertiesList);
        }

        /**
         * 删除example
         * @param fieldName
         */
        public void deleteExample(String fieldName) {
            deleteProperties(fieldName);
        }

}
