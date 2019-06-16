package com.zkml.domainmanage.support;

import com.zkml.domainmanage.support.exception.DomainServiceException;
import com.zkml.domainmanage.support.input.PropertiesInput;
import com.zkml.domainmanage.support.metadata.DomainMetadata;
import com.zkml.domainmanage.support.metadata.FieldExampleResolver;
import com.zkml.domainmanage.support.metadata.FieldMetadata;
import com.zkml.domainmanage.support.metadata.imports.PropertiesImportSource;
import com.zkml.domainmanage.support.properties.CommentProperties;
import com.zkml.domainmanage.support.properties.PropertiesMetadate;
import com.zkml.domainmanage.support.properties.PropertiesUtils;
import com.zkml.domainmanage.support.vo.PropertyVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SystemService {

    @Autowired
    private PropertiesImportSource propertiesImportSource;

    @Autowired
    private FieldExampleResolver fieldExampleResolver;

    @Autowired
    private DomainContext domainContext;

    /**
     * 获取import列表
     * @return
     */
    public List<PropertyVO> importList(String query){

        Map<String,PropertiesMetadate> map = propertiesImportSource.getImports();
        return getPropertyList(query,map);
    }

    /**
     * 将map组装成PropertyVO数组
     * @param query 查询条件
     * @param map
     * @return
     */
    private List<PropertyVO> getPropertyList(String query,Map<String,PropertiesMetadate> map){
        List<PropertyVO> resultList = new ArrayList<>();
        Iterator<Map.Entry<String,PropertiesMetadate>> iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,PropertiesMetadate> entry = iterator.next();
            String name = entry.getKey();
            String value = entry.getValue().getValue();
            String comment = entry.getValue().getComment();
            PropertyVO propertyVO = new PropertyVO(name,value,comment);
            //如果有查询条件存在
            if(StringUtils.isNotBlank(query)){
                query = query.trim();
                if(name.indexOf(query)>-1 || value.indexOf(query)>-1){
                    resultList.add(propertyVO);
                }
            }else{
                resultList.add(propertyVO);
            }
        }
        //Comparator 要满足自反性，传递性，对称性，否则可能会报错
        resultList = resultList.stream().sorted((x,y)->{
            if(StringUtils.isBlank(x.getValue())
                    && !StringUtils.isBlank(y.getValue())){
                return -1;
            }
            if(!StringUtils.isBlank(x.getValue())
                    && StringUtils.isBlank(y.getValue())){
                return 1;
            }
            return x.getName().compareTo(y.getName());
        }).collect(Collectors.toList());
        return resultList;
    }

    /**
     * 添加import
     * @param name
     * @param value
     */
    public void doAddImport(String name,String value){
        propertiesImportSource.addImport(name,value);
    }

    /**
     * 删除import
     * @param name
     */
    public void doDeleteImport(String name){
        propertiesImportSource.deleteImport(name);
    }


    /**
     * 获取example列表
     * @param query
     * @return
     */
    public List<PropertyVO> exampleList(String query){
        Map<String,PropertiesMetadate> map = fieldExampleResolver.getExamples();
        return getPropertyList(query,map);
    }

    /**
     * 添加Example
     * @param name
     * @param value
     */
    public void doAddExample(String name,String value,String comment){
        fieldExampleResolver.addExample(name,value,comment,true);
    }

    /**
     * 删除Example
     * @param name
     */
    public void doDeleteExample(String name){
        fieldExampleResolver.deleteExample(name);
    }

    /**
     * 通过properties导入Example
     * @param file
     */
    public void doImportExample(MultipartFile file){
        try {
            CommentProperties properties = PropertiesUtils.load(file.getInputStream());
            properties.stringPropertyNames().stream().forEach(pro->{
                doAddExample(pro,properties.getProperty(pro),properties.getComment(pro));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据导入的domain批量生成相应的name，已有的name略过
     */
    public void doLoadExampleNames(){
        List<DomainMetadata> domainList = domainContext.getDomainList();
        if(CollectionUtils.isEmpty(domainList))
            throw  new DomainServiceException("domain目录没有载入！");
        List<PropertiesMetadate> propertiesList = new ArrayList<>();
        for(DomainMetadata domainMetadata:domainList){
            if(!domainMetadata.isDirectory()){
                for(FieldMetadata field:domainMetadata.getFieldMetadataList()){
                    if(DomainUtils.isFieldDictionaryType(field.getFieldType())){
                        PropertiesMetadate propertiesMetadate = new PropertiesMetadate();
                        propertiesMetadate.setKey(field.getFieldName());
                        propertiesMetadate.setComment(field.getProcessedNote()+"("+field.getFieldType()+")");
                        propertiesMetadate.setValue("");
                        propertiesList.add(propertiesMetadate);
                    }
                }
            }
        }
        if(!CollectionUtils.isEmpty(propertiesList))
            fieldExampleResolver.batchAddExamples(propertiesList,false);
    }
}
