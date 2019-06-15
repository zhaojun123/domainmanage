package com.zkml.domainmanage.support.metadata.imports;

import com.zkml.domainmanage.support.DomainUtils;
import com.zkml.domainmanage.support.metadata.*;
import com.zkml.domainmanage.support.metadata.process.BeforeAddMetadataProcess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 针对于import的支持
 */
public class DomainImportSupport implements BeforeAddMetadataProcess {

    public static String[] baseType = {"int","long","double","float","char","byte","short","boolean"
            ,"Integer","Long","Double","Float","Character","Byte","Short","Boolean","String"};

    public static Pattern genericsPattern = Pattern.compile("<([^<>])*>");

    private List<ImportSource> importSourceList;

    public DomainImportSupport(List<ImportSource> importSourceList){
        this.importSourceList = importSourceList;
    }


    /**
     * 过滤baseMetada中所有需要添加import支持的classType
     * @param baseMetadaList
     * @param domainMetadata
     * @param domainMetadataHandle
     */
    @Override
    public void beforeAddProcess(List<? extends BaseMetada> baseMetadaList, DomainMetadata domainMetadata,DomainMetadataHandle domainMetadataHandle) {
        //需要解析的classType集合
        List<String> classTypeList = new ArrayList<>();

        for(BaseMetada baseMetada:baseMetadaList){
            //如果是对field进行添加，则检查fieldType、field相对应的getMethod、setMethod、所属的annotation，看是否需要添加import
            if(baseMetada instanceof FieldMetadata){
                FieldMetadata fieldMetadata = (FieldMetadata)baseMetada;
                String fieldType = fieldMetadata.getFieldType();

                classTypeList.add(fieldType);
                for(BaseAnnotationMetadata annotationMetadata:fieldMetadata.getAnnotationList()){
                    classTypeList.add(annotationMetadata.getAnnotationName());
                }
                //如果get/set method不为空，也检查他们的annotation
                if(fieldMetadata.getgMethod()!=null){
                    for(BaseAnnotationMetadata annotationMetadata:fieldMetadata.getgMethod().getAnnotationList()){
                        classTypeList.add(annotationMetadata.getAnnotationName());
                    }
                }

                if(fieldMetadata.getsMethod()!=null){
                    for(BaseAnnotationMetadata annotationMetadata:fieldMetadata.getsMethod().getAnnotationList()){
                        classTypeList.add(annotationMetadata.getAnnotationName());
                    }
                }
                //如果对class进行添加，则检查class所属的annotation
            }else if(baseMetada instanceof ClassMetadata){
                ClassMetadata classMetadata = (ClassMetadata)baseMetada;
                for(BaseAnnotationMetadata annotationMetadata:classMetadata.getAnnotationList()){
                    classTypeList.add(annotationMetadata.getAnnotationName());
                }
            }
        }

        List<String> importList = getImport(classTypeList);
        if(!CollectionUtils.isEmpty(importList)){
            domainMetadataHandle.add(importList.stream().distinct().map(
                    string->{
                        return new OtherMetadata(string.trim(), OtherMetadata.MetadataType.IMPORT);
                    }
            ).collect(Collectors.toList()), domainMetadata);
            domainMetadata.update(domainMetadataHandle);
        }
    }

    /**
     * 获取classType所需要的import
     * @param classTypes
     * @return
     */
    public List<String> getImport(List<String> classTypes) {
        List<String> result = new ArrayList<>();

        if (CollectionUtils.isEmpty(classTypes))
            return result;

        classTypeRetry:
        for(String classType:classTypes){
            //判断是否是基础类型或者基础类型包装类
            if(DomainUtils.isBaseType(classType))
                continue ;
            //如果有范型，解析范型
            List<String> classTypeList = analysisGenerics(classType,null);

            for(String analysisClassType:classTypeList){
                for(ImportSource importSource:importSourceList){
                    String value = importSource.getImport(analysisClassType);
                    if(StringUtils.isNotBlank(value)){
                        result.add(value);
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * 解析范型，获取fieldType列表
     * @param classType
     * @param list
     * @return
     */
    private static List<String> analysisGenerics(String classType,List<String> list){
        if(list == null){
            list = new ArrayList<>();
        }
        //如果field类型不包含< 则直接添加返回
        if(classType.indexOf("<")<0){
            if(!list.contains(classType))
                list.add(classType);
            return list;
        }
        Matcher matcher = genericsPattern.matcher(classType);
        while(matcher.find()){
            String[] childClassTypes = matcher.group()
                    .replace("<","").replace(">","").split(",");
            for(String childClassType:childClassTypes){
                if(!list.contains(childClassType))
                    list.add(childClassType);
            }
            classType = matcher.replaceFirst("");

        }
        analysisGenerics(classType,list);
        return list;
    }
}
