package com.zkml.domainmanage.support.metadata;

import com.zkml.domainmanage.support.DomainFileUtils;
import com.zkml.domainmanage.support.DomainUtils;
import com.zkml.domainmanage.support.exception.DomainServiceException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DomainMetadata{

    private String localPath; //本地地址

    private String fileName; //文件/目录名称

    private String fileKey; // 唯一标示

    private String parentFileKey; //父

    private String className; //class名称

    private int classStartLine; // class开始的行数

    private String fullClassName; //类全路径名称 com.zkml.domainmanage.support.metadata.DomainMetadata

    private List<String> contentList; //文本分行存储,unmodifiableList

    private boolean directory = false; //是否目录

    private ClassMetadata classMetadata; //class

    private List<FieldMetadata> fieldMetadataList = new ArrayList(); //field集合

    private List<MethodMetadata> methodMetadataList = new ArrayList(); //方法集合

    private List<DomainMetadata> domainMetadataList = new ArrayList(); //子文件/目录集合

    private List<OtherMetadata> importList = new ArrayList(); //import文件 去掉了import关键字 和;

    private OtherMetadata packageMetadata;  //去掉了package关键字和 ;

    private long lastModified = 0; //最后修改时间

    private String content; //domain的内容

    public ClassMetadata getClassMetadata() {
        return classMetadata;
    }

    public void setClassMetadata(ClassMetadata classMetadata) {
        this.classMetadata = classMetadata;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OtherMetadata getPackageMetadata() {
        return packageMetadata;
    }

    public void setPackageMetadata(OtherMetadata packageMetadata) {
        this.packageMetadata = packageMetadata;
    }

    public int getClassStartLine() {
        return classStartLine;
    }

    public void setClassStartLine(int classStartLine) {
        this.classStartLine = classStartLine;
    }

    public List<String> getContentList() {
        return contentList;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public DomainMetadata(){
        super();
    }

    public DomainMetadata(String content) {
        this.content = content;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getParentFileKey() {
        return parentFileKey;
    }

    public void setParentFileKey(String parentFileKey) {
        this.parentFileKey = parentFileKey;
    }

    public List<MethodMetadata> getMethodMetadataList() {
        return methodMetadataList;
    }

    public void setMethodMetadataList(List<MethodMetadata> methodMetadataList) {
        this.methodMetadataList = methodMetadataList;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<FieldMetadata> getFieldMetadataList() {
        return fieldMetadataList;
    }

    public void setFieldMetadataList(List<FieldMetadata> fieldMetadataList) {
        this.fieldMetadataList = fieldMetadataList;
    }

    public List<DomainMetadata> getDomainMetadataList() {
        return domainMetadataList;
    }

    public void setDomainMetadataList(List<DomainMetadata> domainMetadataList) {
        this.domainMetadataList = domainMetadataList;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public List<OtherMetadata> getImportList() {
        return importList;
    }

    public void setImportList(List<OtherMetadata> importList) {
        this.importList = importList;
    }

    /**
     * 将contentList copy一份可修改的list
     * @return
     */
    public List<String> copyContentList(){
        List<String> newContentList = new ArrayList<>();
        Collections.copy(newContentList,this.contentList);
        return newContentList;
    }

    /**
     * 当监测到文件被修改时调用该方法进行更新
     * @param domainMetadataHandle
     * @return
     */
    public void update(DomainMetadataHandle domainMetadataHandle){
        File file = new File(this.localPath);
        String content = null;
        try {
            content = DomainFileUtils.redFileContent(file);
        } catch (IOException e) {
            throw new DomainServiceException("读取文件【"+this.localPath+"】报错",e);
        }
        this.setContent(content);
        this.lastModified = file.lastModified();
        domainMetadataHandle.init(this);
    }

    /**
     * 静态方法从Directory创建DomainMetadata
     * @param directory
     * @param domainMetadataHandle
     * @return
     */
    public static DomainMetadata create(DomainMetadataHandle domainMetadataHandle,DomainFileUtils.Directory directory){
        DomainMetadata domainMetadata = new DomainMetadata();
        return create(domainMetadataHandle,domainMetadata,directory);
    }

    private static DomainMetadata create(DomainMetadataHandle domainMetadataHandle,DomainMetadata domainMetadata, DomainFileUtils.Directory directory) {
        domainMetadata.setContent(directory.getContent());
        domainMetadata.setFileKey(directory.getFileKey());
        domainMetadata.setFileName(directory.getName());
        domainMetadata.setClassName(domainMetadata.getFileName().replace(".java", ""));
        domainMetadata.setLastModified(directory.getLastModified());
        domainMetadata.setDirectory(directory.isDirectory());
        domainMetadata.setLocalPath(directory.getLocalPath());
        for(DomainFileUtils.Directory childDirectory:directory.getDirectoryList()){
            DomainMetadata childDomain = new DomainMetadata();
            childDomain.setParentFileKey(domainMetadata.getFileKey());
            domainMetadata.getDomainMetadataList().add(create(domainMetadataHandle,childDomain,childDirectory));
        }

        for(DomainFileUtils.Directory childDirectory:directory.getFileList()){
            DomainMetadata childDomain = new DomainMetadata();
            childDomain.setParentFileKey(domainMetadata.getFileKey());
            domainMetadata.getDomainMetadataList().add(create(domainMetadataHandle,childDomain,childDirectory));
        }
        domainMetadataHandle.init(domainMetadata);
        return domainMetadata;
    }

    /**
     * 根据fieldName查询FieldMetadata
     * @param fieldName
     * @return
     */
    public FieldMetadata findFieldByName(String fieldName){
        if(fieldName == null)
            return null;
        for(FieldMetadata fieldMetadata:fieldMetadataList){
            if(fieldName.equals(fieldMetadata.getFieldName())){
                return fieldMetadata;
            }
        }
        return null;
    }

    /**
     * 根据methodName查询MethodMetadata
     * @param methodName
     * @return
     */
    public MethodMetadata findMethodByName(String methodName){
        if(methodName == null)
            return null;
        for(MethodMetadata methodMetadata:methodMetadataList){
            if(methodName.equals(methodMetadata.getMethodName()))
                return methodMetadata;
        }
        return null;
    }

    /**
     * 根据importName查询import
     * @param importName
     * @return
     */
    public OtherMetadata findImportByName(String importName){
        if(importName == null)
            return null;
        //对importName进行清理
        importName = DomainUtils.cleanImport(importName);
        for(OtherMetadata importMetadata:importList){
            if(importName.equals(importMetadata.getContent()))
                return importMetadata;
        }
        return null;
    }

    /**
     * 根据packageName查询packageMetadata
     * @param packageName
     * @return
     */
    public OtherMetadata findPackageByName(String packageName){
        if(packageName == null)
            return null;
        //对packageName进行清理
        packageName = DomainUtils.cleanPackage(packageName);
        if(packageMetadata!=null && packageName.equals(packageMetadata.getContent()))
            return packageMetadata;
        return null;
    }

    /**
     * 查看这个import是不是在同一个包下
     * @param importName
     * @return
     */
    public boolean samePackage(String importName){
        if(importName == null)
            return false;
        String packageName = DomainUtils.getPackageByImport(importName);
        if(findPackageByName(packageName)!=null)
            return true;
        return false;
    }

    /**
     * 检查文件是否有改动 如果有则重新初始化分析
     * @param domainMetadataHandle
     */
    public void checkModified(DomainMetadataHandle domainMetadataHandle) {
        File file = new File(this.getLocalPath());
        if(file.lastModified() != this.getLastModified())
            this.update(domainMetadataHandle);
    }


}

