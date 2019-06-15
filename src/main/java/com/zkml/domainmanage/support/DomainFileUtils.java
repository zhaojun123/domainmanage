package com.zkml.domainmanage.support;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件目录操作工具类
 */
public class DomainFileUtils {


    /**
     * 扫描目录，获取目录/文件集合 只扫描java文件
     * @param localPath
     * @return
     */
    public static Directory scanDirectory(String localPath) throws IOException {
        localPath = localPath.trim();
        Directory directory = new Directory();
        File file = new File(localPath);
        directory.setFileKey(file.getName());
        return scanDirectory(file,directory);
    }

    private static Directory scanDirectory(File file,Directory directory) throws IOException {

        directory.setLocalPath(file.getPath());
        directory.setName(file.getName());
        directory.setLastModified(file.lastModified());

        if(file.isDirectory()){
            directory.setDirectory(true);
            File[] files = file.listFiles();
            for(File childFile:files){
                Directory childDirectory = new Directory();
                childDirectory.setFileKey(directory.getFileKey()+"."+childFile.getName());
                if(childFile.isDirectory()){
                    directory.getDirectoryList().add(childDirectory);
                }else{
                    //只加载java文件
                    if(childFile.getName().endsWith(".java"))
                        directory.getFileList().add(childDirectory);
                }
                scanDirectory(childFile,childDirectory);
            }
        }else{
            directory.setContent(redFileContent(file));
        }
        return directory;
    }

    /**
     * 获取文件内容
     * @param file
     * @return
     * @throws IOException
     */
    public static String redFileContent(File file) throws IOException {
        return FileUtils.readFileToString(file,"utf-8");
    }


    /**
     * 写文件内容
     * @param path
     * @param content
     * @return
     * @throws IOException
     */
    public static void writeFileContent(String path,String content) throws IOException {
        FileUtils.write(new File(path),content,"utf-8");
    }

    /**
     * 文件/目录
     */
    public static class Directory{

        private String localPath; //本地地址

        private String fileKey = ""; //文件唯一标示 从顶级文件目录开始的相对文件路径

        private String name; //名称

        private String content; //文件内容

        private List<Directory> fileList = new ArrayList(); //下属文件

        private List<Directory> directoryList = new ArrayList(); //下属目录

        private boolean directory = false; //是否文件夹

        public boolean isDirectory() {
            return directory;
        }

        public void setDirectory(boolean directory) {
            this.directory = directory;
        }

        private long lastModified = 0; //最后修改日期

        public String getFileKey() {
            return fileKey;
        }

        public void setFileKey(String fileKey) {
            this.fileKey = fileKey;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getLocalPath() {
            return localPath;
        }

        public void setLocalPath(String localPath) {
            this.localPath = localPath;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Directory> getFileList() {
            return fileList;
        }

        public void setFileList(List<Directory> fileList) {
            this.fileList = fileList;
        }

        public List<Directory> getDirectoryList() {
            return directoryList;
        }

        public void setDirectoryList(List<Directory> directoryList) {
            this.directoryList = directoryList;
        }
    }

}
