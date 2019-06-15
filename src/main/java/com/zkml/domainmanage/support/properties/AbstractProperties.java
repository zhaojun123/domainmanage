package com.zkml.domainmanage.support.properties;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Properties基类，读取src目录下的Properties文件，达到实时获取数据的效果
 */
public abstract class AbstractProperties{

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    private Resource resource;

    public AbstractProperties(String propertiesPath) throws IOException {
        resource = resourceLoader.getResource(propertiesPath);
    }

    /**
     * 获取Properties
     * @return
     */
    protected CommentProperties getProperties() {
        try {
            return PropertiesUtils.load(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return new CommentProperties();
        }
    }

    /**
     * 获取所有的Properties
     * @return
     */
    public Map<String,PropertiesMetadate> getPropertiesMap(){
        Map<String,PropertiesMetadate> result = new HashMap();
        CommentProperties properties = getProperties();
        Set<String> set = properties.stringPropertyNames();
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()){
            PropertiesMetadate propertiesMetadate = new PropertiesMetadate();
            String key = iterator.next();
            propertiesMetadate.setKey(key);
            propertiesMetadate.setValue(properties.getProperty(key));
            propertiesMetadate.setComment(properties.getComment(key));
            result.put(key,propertiesMetadate);
        }
        return result;
    }

    /**
     * 添加Property 并且写入文件
     * @param name
     * @param value
     */
    protected void storeProperties(String name,String value,String comment){
        CommentProperties properties = getProperties();
        properties.setProperty(name,value,comment);
        storeProperties(properties);
    }

    private void storeProperties(CommentProperties properties){
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(resource.getFile());
            FileUtils.write(resource.getFile(),"");
            properties.store(fileOutputStream,null);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 批量添加Property 并且写入文件
     * @param propertiesList
     */
    protected void storeProperties(List<PropertiesMetadate> propertiesList){
        CommentProperties properties = getProperties();
        propertiesList.stream().forEach(pro -> {
            properties.setProperty(pro.getKey(),pro.getValue(),pro.getComment());
        });
        storeProperties(properties);
    }


    /**
     * 删除某个Property，并且写入文件
     * @param name
     */
    protected void deleteProperties(String name){
        CommentProperties properties = getProperties();
        properties.remove(name);
        storeProperties(properties);
    }
}
