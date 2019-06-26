package com.zkml.domainmanage.support.metadata;

import com.zkml.domainmanage.support.ContentUtils;
import com.zkml.domainmanage.support.DomainUtils;
import com.zkml.domainmanage.support.ReflectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 初始化domainMetadata处理类
 */
public class DomainDelegate {

    private DomainMetadata domainMetadata;

    private Pattern patternClass;

    private List<OtherMetadata> oldNoteList = new ArrayList<>(); //注释

    private List<AnnotationMetadata> oldAnnotationList = new ArrayList<>(); //注解

    //field列表
    private List<FieldMetadata> fieldList = new ArrayList<>();

    //所有的方法列表
    private List<MethodMetadata> nomalMethodList = new ArrayList<>();

    //get/Set方法列表
    private List<MethodMetadata> gsMethodList = new ArrayList<>();

    //import
    private List<OtherMetadata> importList = new ArrayList<>();

    //按照行分割文件内容
    private List<String> contentList;

    //行数索引
    private int lineIndex = 0;

    public DomainDelegate(DomainMetadata domainMetadata){
        this.domainMetadata = domainMetadata;
        patternClass = Pattern.compile("((?!class).)*(class)(\\s)*"+domainMetadata.getClassName()+"(.)*");
    }

    /**
     * 逐行读取内容，进行正则匹配处理
     */
    public DomainMetadata init(){
        String content = domainMetadata.getContent();
        if(!StringUtils.isBlank(content)){
            contentList = Collections.unmodifiableList(Arrays.asList(content.split("\n")));
            domainMetadata.setContentList(contentList);
        }
        for(lineIndex=0;lineIndex<contentList.size();lineIndex++){
            String contentLine = contentList.get(lineIndex).trim();
            if(analysisPackage(contentLine)){
                oldNoteList.clear();
                oldAnnotationList.clear();
                continue;
            }
            if(analysisImport(contentLine)){
                oldNoteList.clear();
                oldAnnotationList.clear();
                continue;
            }
            if(analysisNote(contentLine))
                continue;
            if(analysisAnnotation(contentLine))
                continue;
            if(analysisClass(contentLine))
                continue;
            if(analysisField(contentLine))
                continue;
            if(analysisMethod(contentLine))
                continue;
            if(StringUtils.isNotBlank(contentLine)){
                oldNoteList.clear();
                oldAnnotationList.clear();
            }
        }
        domainMetadata.setImportList(Collections.unmodifiableList(importList));
        domainMetadata.setFieldMetadataList(Collections.unmodifiableList(fieldList));
        domainMetadata.setMethodMetadataList(Collections.unmodifiableList(gsMethodList));
        return domainMetadata;
    }

    /**
     * 解析package
     * @param content
     * @return
     */
    private boolean analysisPackage(String content){
        if(!content.startsWith("package"))
            return false;
        String packageName = DomainUtils.cleanPackage(content);
        domainMetadata.setFullClassName(packageName+"."+domainMetadata.getClassName());
        domainMetadata.setPackageMetadata(new OtherMetadata.Builder()
                .metadataType(OtherMetadata.MetadataType.PACKAGE)
                .startLine(lineIndex)
                .endLine(lineIndex)
                .content(packageName).build());
        return true;
    }

    /**
     * 解析import
     * @param content
     * @return
     */
    private boolean analysisImport(String content){
        int index = content.indexOf("import");
        if(index !=0)
            return false;
        String importName = DomainUtils.cleanImport(content);
        importList.add(new OtherMetadata.Builder()
                .metadataType(OtherMetadata.MetadataType.IMPORT)
                .startLine(lineIndex)
                .endLine(lineIndex)
                .sortNo(importList.size())
                .content(importName).build());
        return true;
    }

    /**
     * 给baseType注入额外的元素，例如note annotation
     * @param baseType
     */
    private void injectOtherMetadata(BaseTypeMetadata baseType){
        if(!oldNoteList.isEmpty()){
            for(OtherMetadata note:oldNoteList){
                baseType.getNoteList().add(note);
                //返回俩者最小的开始行数，设置成field的开始行数
                baseType.compareStartLine(note);
            }
            oldNoteList.clear();
        }
        if(!oldAnnotationList.isEmpty()){
            for(AnnotationMetadata annotationMetadata:oldAnnotationList){
                baseType.getAnnotationList().add(annotationMetadata);
                //返回俩者最小的开始行数，设置成开始行数
                baseType.compareStartLine(annotationMetadata);
            }
            oldAnnotationList.clear();
        }
    }

    /**
     * 获取class上的注释和注解，忽略内部类
     * @param content
     * @return
     */
    private boolean analysisClass(String content){
        if(!patternClass.matcher(content).matches())
            return false;
        domainMetadata.setClassStartLine(lineIndex);

        ClassMetadata classMetadata = new ClassMetadata();
        classMetadata.setStartLine(lineIndex);
        classMetadata.setEndLine(lineIndex);
        classMetadata.setClassName(domainMetadata.getClassName());
        //内容只记录class那一行代码
        classMetadata.joinLine(contentList);

        injectOtherMetadata(classMetadata);
        domainMetadata.setClassMetadata(classMetadata);
        return true;
    }

    /**
     * 解析field
     * @return
     */
    private boolean analysisField(String content){
        if(!DomainUtils.isMatcherField(content))
            return false;
        String fieldName = DomainUtils.getFieldNameByContent(content);
        String fieldType = DomainUtils.getFieldTypeByContent(content);
        FieldMetadata fieldMetadata = new FieldMetadata.Builder()
                .startLine(lineIndex)
                .endLine(lineIndex)
                .fieldName(fieldName)
                .fieldType(fieldType)
                .build();
        //只记录field本身的content，不包括注解、注释
        fieldMetadata.joinLine(contentList);
        injectOtherMetadata(fieldMetadata);

        //也有人习惯将//写在field末尾，这里也需要解析
        String[] chars = content.split(";");
        if(chars.length>1 && chars[1].trim().indexOf("/")==0){
                OtherMetadata  note = new OtherMetadata.Builder()
                        .startLine(lineIndex)
                        .endLine(lineIndex)
                        .content(chars[1])
                        .metadataType(OtherMetadata.MetadataType.NOTE)
                        .build();
            fieldMetadata.setOnlyShowNote(note);
        }
        //查看是否有对应的get set方法
        for(MethodMetadata methodMetadata:nomalMethodList){
            if(checkGetMethod(fieldName,methodMetadata.getMethodName())){
                fieldMetadata.setgMethod(methodMetadata);
                gsMethodList.add(methodMetadata);
                methodMetadata.setFieldMetadata(fieldMetadata);
                break;
            }
            if(checkSetMethod(fieldName,methodMetadata.getMethodName())){
                fieldMetadata.setsMethod(methodMetadata);
                gsMethodList.add(methodMetadata);
                methodMetadata.setFieldMetadata(fieldMetadata);
                break;
            }
        }
        fieldMetadata.setSortNo(fieldList.size()); //排序
        fieldList.add(fieldMetadata);
        return true;
    }

    /**
     * 解析方法
     * @param content
     * @return
     */
    private boolean analysisMethod(String content){
        if(!DomainUtils.isMatcherMethod(content))
            return false;
        MethodMetadata methodMetadata = new MethodMetadata();
        String methodName = DomainUtils.getMethodNameByContent(content);
        methodMetadata.setStartLine(lineIndex);
        methodMetadata.setEndLine(matchCloseIndex("\\{","\\}"));
        methodMetadata.setMethodName(methodName);
        //只记录method本身的content，不包括注释 注解
        methodMetadata.joinLine(contentList);

        injectOtherMetadata(methodMetadata);
        //查看是否有对应的get set方法
        for(FieldMetadata fieldMetadata:fieldList){
            if(checkGetMethod(fieldMetadata.getFieldName(),methodName)){
                fieldMetadata.setgMethod(methodMetadata);
                gsMethodList.add(methodMetadata);
                methodMetadata.setFieldMetadata(fieldMetadata);
                break;
            }
            if(checkSetMethod(fieldMetadata.getFieldName(),methodName)){
                fieldMetadata.setsMethod(methodMetadata);
                gsMethodList.add(methodMetadata);
                methodMetadata.setFieldMetadata(fieldMetadata);
                break;
            }
        }
        nomalMethodList.add(methodMetadata);
        return true;
    }

    /**
     * 解析注释
     * @return
     */
    private boolean analysisNote(String content){
        if(content.indexOf("/")!=0)
            return false;
        OtherMetadata note = new OtherMetadata.Builder()
                .metadataType(OtherMetadata.MetadataType.NOTE)
                .build();
        note.setStartLine(lineIndex);
        if(content.indexOf("//")==0){ //单行注释
            note.setEndLine(lineIndex);
        }else{ // /** */多行注释
            note.setEndLine(matchCloseIndex("/\\*","\\*/"));
        }
        note.joinLine(contentList);
        //如果发现注释不为空，则在原基础上新增注释
        oldNoteList.add(note);
        return true;
    }

    /**
     * 解析注解
     * @return
     */
    private boolean analysisAnnotation(String content){
        if(content.indexOf("@")!=0)
            return false;
        String annotationName = DomainUtils.getAnnotationNameByContent(content);
        AnnotationMetadata annotation = new AnnotationMetadata.Builder()
                .annotationName(annotationName)
                .startLine(lineIndex)
                .endLine(matchCloseIndex("\\(","\\)"))
                .build();
        annotation.joinLine(contentList);
        oldAnnotationList.add(annotation);
        return true;
    }

    /**
     * 查看是否是该field对应的get方法
     * @param fieldName
     * @param methodName
     * @return
     */
    private boolean checkGetMethod(String fieldName,String methodName){
        return methodName.equals(ReflectUtils.getMethodName(fieldName));
    }

    /**
     * 查看是否是该field对应的set方法
     * @param fieldName
     * @param methodName
     * @return
     */
    private boolean checkSetMethod(String fieldName,String methodName){
        return methodName.equals(ReflectUtils.setMethodName(fieldName));
    }

    /**
     * 匹配闭合 例如（） {},返回闭合结束的行
     * @param leftSign
     * @param rightSign
     * @return
     */
    private int matchCloseIndex(String leftSign,String rightSign){
        int leftCount =0;
        Pattern leftPattern = Pattern.compile(leftSign);
        Pattern rightPattern = Pattern.compile(rightSign);
        while(contentList.size()>lineIndex){
            String content = contentList.get(lineIndex).trim();
            Matcher leftMatcher = leftPattern.matcher(content);
            Matcher rightMatcher = rightPattern.matcher(content);
            while(leftMatcher.find()){
                leftCount++;
            }
            while(rightMatcher.find()){
                leftCount--;
            }
            if(leftCount <=0)
                break;
            lineIndex++;
        }
        return lineIndex;
    }
}

