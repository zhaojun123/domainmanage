package com.zkml.domainmanage.support;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * 类加载器工具类
 */
public class ClassLoaderUtils {

    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    public static final String FILE_URL_PREFIX = "file:";

    /**
     * 根据classPath下的路径获取文件流
     * file:开头 根据绝对路径去处理， classpath：开头 获取classpath下的路径 ,不带任何标示按照classpath处理
     * @param path
     * @param classLoader
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(String path,ClassLoader classLoader) throws IOException {
        InputStream is;
        if(path.startsWith(FILE_URL_PREFIX)){
            File file = new File(path.substring(FILE_URL_PREFIX.length()));
            return FileUtils.openInputStream(file);
        }
        if(path.startsWith(CLASSPATH_URL_PREFIX))
            path = path.substring(CLASSPATH_URL_PREFIX.length());
        if(path.startsWith("/"))
            path = path.substring(1);
        if (classLoader != null) {
            is = classLoader.getResourceAsStream(path);
        }
        else {
            is = ClassLoader.getSystemResourceAsStream(path);
        }
        if (is == null) {
            throw new FileNotFoundException("path cannot be opened because it does not exist");
        }
        return is;
    }

    /**
     * 根据路径获取文件流
     * @param path
     * @return
     * @throws IOException
     */
    public static InputStream getInputStream(String path) throws IOException {
        return getInputStream(path,null);
    }


    /**
     * 根据inputStream获取String
     * @param inputStream
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getContent(InputStream inputStream) throws IOException {
        return getContent(inputStream,"utf-8");
    }

    /**
     * 根据inputStream获取String
     * @param inputStream
     * @param encode
     * @return
     */
    public static String getContent(InputStream inputStream,String encode) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream,encode);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuilder result = new StringBuilder();
        for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
            result.append(line).append("\n");
        }
        return result.toString();
    }

    /**
     * 根据文件路径获取文件内容
     * file:开头 根据绝对路径去处理， classpath：开头 获取classpath下的路径
     * @param path
     * @return
     * @throws IOException
     */
    public static String getContent(String path) throws IOException {
        return getContent(path,"utf-8");
    }

    /**
     * 根据文件路径获取文件内容
     * file:开头 根据绝对路径去处理， classpath：开头 获取classpath下的路径
     * @param path
     * @param encode
     * @return
     */
    public static String getContent(String path,String encode) throws IOException {
        InputStream inputStream = getInputStream(path);
        try{
            return getContent(inputStream,encode);
        }finally {
            inputStream.close();
        }
    }

}
