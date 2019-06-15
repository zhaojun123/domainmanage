package com.zkml.domainmanage.support.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtils {

    /**
     * 加载inputStream生成Properties，同时关闭inputStream
     * @param inputStream
     * @return
     */
    public static CommentProperties load(InputStream inputStream){
        CommentProperties properties = new CommentProperties();
        try{
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

}
