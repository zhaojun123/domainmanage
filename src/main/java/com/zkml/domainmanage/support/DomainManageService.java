package com.zkml.domainmanage.support;

import com.zkml.domainmanage.support.exception.DomainServiceException;
import com.zkml.domainmanage.support.input.*;
import com.zkml.domainmanage.support.metadata.*;
import com.zkml.domainmanage.support.metadata.swagger.ApiModelMetadata;
import com.zkml.domainmanage.support.metadata.swagger.ApiModelPropertyMetadata;
import com.zkml.domainmanage.support.metadata.validation.ValidationEnum;
import com.zkml.domainmanage.support.metadata.validation.ValidationMetadata;
import com.zkml.domainmanage.support.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DomainManageService {

    @Autowired
    DomainContext domainContext;

    @Autowired
    DomainTemplateResolver domainTemplateResolver;

    @Autowired
    DomainMetadataHandle domainMetadataHandle;

    @Autowired
    FieldExampleResolver fieldExampleResolver;

    /**
     * 获取domain加载路径列表
     * @return
     */
    public List<String> getlocalPathList(){
        List<String> resultList = new ArrayList<>();
        if(!checkDomainLoad())
            return resultList;
        for(DomainMetadata domainMetadata:domainContext.getDomainTreeCache()){
            resultList.add(domainMetadata.getLocalPath());
        }
        return resultList;
    }

    /**
     * 检测domain是否载入
     * @return
     */
    public boolean checkDomainLoad(){
        if(!CollectionUtils.isEmpty(domainContext.getDomainTreeCache()))
            return true;
        return false;
    }

    /**
     * 获取domain代码
     * @param fileKey
     * @return
     */
    public String code(String fileKey){
        String code = domainContext.getDomainByKey(fileKey).getContent();
        return code;
    }

    /**
     * 获取field字典
     * @return
     */
    public List<FieldDictionaryVO> fieldDictionary(){
        List<FieldDictionaryVO> result = new ArrayList<>();
        if(!checkDomainLoad())
            return result;
        for(DomainMetadata domainMetadata:domainContext.getDomainList()){
            if(!domainMetadata.isDirectory()){
                List<FieldMetadata> fieldList = domainMetadata.getFieldMetadataList();
                for(FieldMetadata field : fieldList){
                    if(DomainUtils.isFieldDictionaryType(field.getFieldType())){
                        FieldDictionaryVO vo = new FieldDictionaryVO();
                        vo.setFieldName(field.getFieldName());
                        vo.setFieldType(field.getFieldType());
                        vo.setNote(field.getProcessedNote());
                        vo.setExample(fieldExampleResolver.getExample(field.getFieldName()));
                        result.add(vo);
                    }
                }
            }
        }
        //去重
        result = result.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<FieldDictionaryVO>(Comparator.comparing(FieldDictionaryVO::getFieldName))), ArrayList::new)
        );
        return result;
    }

    /**
     * 调整field的位置
     * @param switchFieldInput
     */
    public void doSwitchField(SwitchFieldInput switchFieldInput){
        DomainMetadata domainMetadata = domainContext.getDomainByKey(switchFieldInput.getFileKey());
        synchronized (domainMetadata){
            domainMetadata.checkModified(domainMetadataHandle);
            FieldMetadata fieldMetadata = domainMetadata.findFieldByName(switchFieldInput.getFieldName());
            //设置排序
            fieldMetadata.setSortNo(switchFieldInput.getSortNo());
            //设置修改范围只修改field
            domainMetadataHandle.update(fieldMetadata,domainMetadata,FieldMetadata.Status.NONE);
        }
    }

    /**
     * 批量删除Swagger标签
     * @param fileKeys
     */
    public void doBatchDeleteSwagger(String fileKeys){
        String[] fileKeyList = fileKeys.split(",");
        for(String fileKey:fileKeyList){
            DomainMetadata domainMetadata = domainContext.getDomainByKey(fileKey);
            if(domainMetadata.isDirectory())
                continue;
            synchronized (domainMetadata){
                domainMetadata.checkModified(domainMetadataHandle);
                List<FieldMetadata> fieldList = domainMetadata.getFieldMetadataList();

                //需要修改的field
                List<FieldMetadata> updateFieldList = new ArrayList<>();
                //对field循环清理ApiModelProperty标签
                for(FieldMetadata fieldMetadata: fieldList){
                    List<BaseAnnotationMetadata> annotationList = fieldMetadata.getAnnotationList();
                    if(annotationList.removeIf(ApiModelPropertyMetadata.class::isInstance)){
                        //只修改field
                        updateFieldList.add(fieldMetadata);
                    }
                }
                if(!updateFieldList.isEmpty())
                    domainMetadataHandle.update(updateFieldList,domainMetadata,FieldMetadata.Status.NONE);

                //清除class上的ApiModel注解
                ClassMetadata classMetadata = domainMetadata.getClassMetadata();
                List<BaseAnnotationMetadata> annotationList = classMetadata.getAnnotationList();
                if(annotationList.removeIf(ApiModelMetadata.class::isInstance)){
                    domainMetadataHandle.update(classMetadata,domainMetadata);
                }
                //清除importList
                List<String> importList = Arrays.asList("io.swagger.annotations.ApiModel","io.swagger.annotations.ApiModelProperty");
                domainMetadataHandle.delete(importList.stream().map(
                        string->{
                            return new OtherMetadata(string.trim(), OtherMetadata.MetadataType.IMPORT);
                        }
                ).collect(Collectors.toList()),domainMetadata);
            }
        }
    }

    /**
     * 批量添加Swagger标签
     * @param fileKeys
     */
    public void doBatchAddSwagger(String fileKeys){
        String[] fileKeyList = fileKeys.split(",");
        for(String fileKey:fileKeyList){
            DomainMetadata domainMetadata = domainContext.getDomainByKey(fileKey);
            if(domainMetadata.isDirectory())
                continue;
            synchronized (domainMetadata){
                domainMetadata.checkModified(domainMetadataHandle);
                List<FieldMetadata> fieldList = domainMetadata.getFieldMetadataList();

                //需要修改的field
                List<FieldMetadata> updateFieldList = new ArrayList<>();
                retry:
                for(FieldMetadata fieldMetadata: fieldList){
                    List<BaseAnnotationMetadata> annotationList = fieldMetadata.getAnnotationList();
                    boolean required = false;
                    for(BaseAnnotationMetadata annotationMetadata: annotationList){
                        //如果已有相同注解，则略过
                        if(annotationMetadata instanceof ApiModelPropertyMetadata)
                            continue retry;
                        //如果有NotEmpty、NotNull、NotBlank注解，则说明属性必填
                        if(ValidationMetadata.match(annotationMetadata))
                            required = true;
                    }
                    //添加ApiModelPropertyMetadata标签
                    ApiModelPropertyMetadata apiModelPropertyMetadata = new ApiModelPropertyMetadata();
                    apiModelPropertyMetadata.setValue(fieldMetadata.getProcessedNote());
                    apiModelPropertyMetadata.setExample(fieldExampleResolver.getExample(fieldMetadata.getFieldName()));
                    apiModelPropertyMetadata.setRequired(required);
                    annotationList.add(apiModelPropertyMetadata);
                    updateFieldList.add(fieldMetadata);
                }

                if(!updateFieldList.isEmpty())
                    domainMetadataHandle.update(updateFieldList,domainMetadata,FieldMetadata.Status.NONE);

                //TODO 如果返回的是嵌套的VO 多个ApiModel标签会让swagger显示出bug
                /*//添加class上的ApiModel注解
                ClassMetadata classMetadata = domainMetadata.getClassMetadata();
                List<BaseAnnotationMetadata> annotationList = classMetadata.getAnnotationList();
                //检查是否有相同的注解，如果没有则添加
                if(!annotationList.removeIf(ApiModelMetadata.class::isInstance)){
                    ApiModelMetadata apiModelMetadata = new ApiModelMetadata();
                    apiModelMetadata.setValue(classMetadata.getProcessedNote());
                    annotationList.add(apiModelMetadata);
                    domainMetadataHandle.update(classMetadata,domainMetadata);
                }*/
            }
        }
    }

    /**
     * 修改field的Validation注解
     * @param fileKey
     * @param fieldValidationList
     */
    public void doUpdateFieldValidation(String fileKey,List<UpdateFieldValidationInput> fieldValidationList){
        if(!CollectionUtils.isEmpty(fieldValidationList)){
            DomainMetadata domainMetadata = domainContext.getDomainByKey(fileKey);
            synchronized (domainMetadata){
                domainMetadata.checkModified(domainMetadataHandle);

                //需要修改的field
                List<FieldMetadata> updateFieldList = new ArrayList<>();

                for(UpdateFieldValidationInput fieldValidation:fieldValidationList){
                    FieldMetadata fieldMetadata = domainMetadata.findFieldByName(fieldValidation.getFieldName());
                    //设置validation注解
                    List<BaseAnnotationMetadata> annotationList = fieldMetadata.getAnnotationList();
                    //删除NotNull NotEmpty NotBlank的注解
                    annotationList.removeIf(annotationMetadata ->
                            ValidationMetadata.match(annotationMetadata)
                    );
                    //添加新增的validation注解
                    for(ValidationEnum validationEnum:fieldValidation.getValidationList()){
                        ValidationMetadata validationMetadata = validationEnum.createMetadata();
                        validationMetadata.setMessage(fieldValidation.getValidationMessage());
                        annotationList.add(validationMetadata);
                    }
                    updateFieldList.add(fieldMetadata);

                }

                if(!updateFieldList.isEmpty())
                    domainMetadataHandle.update(updateFieldList,domainMetadata,FieldMetadata.Status.NONE);
            }
        }
    }

    /**
     * domain详细页
     * @param fileKey
     * @return
     */
    public DomainVO domainDetail(String fileKey){
        DomainMetadata domainMetadata = domainContext.getDomainByKey(fileKey);
        synchronized (domainMetadata){
            domainMetadata.checkModified(domainMetadataHandle);
            //进行数据组装
            DomainVO domainVO = new DomainVO();
            domainVO.setFileKey(fileKey);
            domainVO.setDirectory(domainMetadata.isDirectory());

            //组装field
            List<FieldVO> fieldList = new ArrayList<>();
            for(FieldMetadata fieldMetadata:domainMetadata.getFieldMetadataList()){
                FieldVO fieldVO = new FieldVO();
                fieldVO.setFieldName(fieldMetadata.getFieldName());
                fieldVO.setFieldType(fieldMetadata.getFieldType());
                fieldVO.setNote(fieldMetadata.getProcessedNote());
                fieldVO.setSortNo(fieldMetadata.getSortNo());

                //组装validation注解
                for(BaseAnnotationMetadata annotationMetadata:fieldMetadata.getAnnotationList()){
                    ValidationEnum validationEnum = ValidationEnum.getByAnnotationName(annotationMetadata.getAnnotationName());
                    //如果不为null 说明这个注解是validation类型
                    if(validationEnum!=null){
                        ValidationMetadata validationMetadata = (ValidationMetadata)annotationMetadata;
                        ValidationVO validationVO = new ValidationVO();
                        validationVO.setAnnotationName(validationMetadata.getAnnotationName());
                        validationVO.setMessage(validationMetadata.getMessage());
                        validationVO.setValidationType(validationMetadata.getValidationType());
                        fieldVO.getValidationMap().put(validationVO.getValidationType().name(),validationVO);
                        fieldVO.setValidationMessage(validationVO.getMessage());
                    }
                }
                fieldList.add(fieldVO);
            }
            domainVO.setFieldList(fieldList);
            return domainVO;
        }
    }

    /**
     * 删除field 同时删除get set
     * @param deleteFieldInput
     * @throws IOException
     */
    public void doDeleteField(DeleteFieldInput deleteFieldInput){
        FieldMetadata fieldMetadata = new FieldMetadata.Builder()
                .fieldName(deleteFieldInput.getFieldName()).build();
        String[] fileKeys = deleteFieldInput.getFileKeys().split(",");
        for(String fileKey:fileKeys){
            //同时删除get set
            domainMetadataHandle.delete(fieldMetadata,domainContext.getDomainByKey(fileKey),FieldMetadata.Status.ALL);
        }
    }

    /**
     * 自定义添加field代码
     * @param addFieldCustomize
     */
    public void doAddFieldCustomize(AddFieldCustomizeInput addFieldCustomize){
        //解析代码文本
        FieldMetadata processedField = domainMetadataHandle.init(new DomainMetadata(addFieldCustomize.getFieldContent()))
                .getFieldMetadataList().get(0);
        processedField.setSortNo(-1); //设置排序
        String[] fileKeys = addFieldCustomize.getFileKeys().split(",");
        for(String fileKey:fileKeys) {
            domainMetadataHandle.add(processedField,domainContext.getDomainByKey(fileKey),FieldMetadata.Status.ALL);
        }
    }

    /**
     * 添加field
     * @param addFieldInput
     */
    public void doAddField(AddFieldInput addFieldInput){
        FieldMetadata.Builder builder = new FieldMetadata.Builder()
                .fieldName(addFieldInput.getFieldName())
                .fieldType(addFieldInput.getFieldType())
                .note(addFieldInput.getNote())
                .status(addFieldInput.getStatus());

        String[] fileKeys = addFieldInput.getFileKeys().split(",");
        FieldMetadata fieldMetadata = builder.build();

        //根据模版组装field、get、set代码
        String templateContent = domainTemplateResolver.parseFieldByTemplate(fieldMetadata);
        //对模版代码进行解析,获取解析后的FieldMetadata
        FieldMetadata processedField = domainMetadataHandle.init(new DomainMetadata(templateContent))
                .getFieldMetadataList().get(0);

        //解析validation注解
        for(ValidationEnum validation:addFieldInput.getValidations()){
            if(StringUtils.isBlank(addFieldInput.getValidationMessage()))
                throw new DomainServiceException("验证失败提示信息不能为空");
            ValidationMetadata validationMetadata = validation.createMetadata();
            validationMetadata.setMessage(addFieldInput.getValidationMessage());
            processedField.getAnnotationList()
                    .add(validationMetadata);
        }

        for(String fileKey:fileKeys){
            processedField.setSortNo(fieldMetadata.getSortNo());
            domainMetadataHandle.add(processedField,domainContext.getDomainByKey(fileKey),fieldMetadata.getStatus());
        }
    }

    /**
     * 初始化DomainTre
     * @param localPathList
     */
    public void initDomainTree(List<String> localPathList){
        try{
            domainContext.initDomainMetadataTree(localPathList,domainMetadataHandle);
        }catch (IOException e){
            e.printStackTrace();
            throw new DomainServiceException("载入domain文件目录失败");
        }

    }

    /**
     * 将DomainTree处理成List<DomainVO>,附带全文查询条件
     * @return
     */
    public List<DomainVO> handleDomainTree(String query){
        List<DomainVO> resultList = new ArrayList<>();
        if(!checkDomainLoad())
            return resultList;
        for(DomainMetadata domainMetadata:domainContext.getDomainTreeCache()){
            resultList.addAll(handleDomainTree(domainMetadata,query));
        }
        return resultList;
    }

    private List<DomainVO> handleDomainTree(DomainMetadata domainMetadata,String query){
        List<DomainVO> domainVOList = new ArrayList<>();
        if(domainMetadata!=null)
            handleDomainTree(0,domainVOList,domainMetadata,query);
        return domainVOList;
    }

    private void handleDomainTree(int level,List<DomainVO> domainVOList,DomainMetadata domainMetadata,String query){
        DomainVO domainVO = new DomainVO();
        domainVO.setClassName(domainMetadata.getClassName());
        domainVO.setDirectory(domainMetadata.isDirectory());
        domainVO.setFileKey(domainMetadata.getFileKey());
        domainVO.setParentFileKey(domainMetadata.getParentFileKey());
        domainVO.setLevel(level);
        ClassMetadata classMetadata = domainMetadata.getClassMetadata();
        if(classMetadata!=null)
            domainVO.setNote(classMetadata.getProcessedNote());
        if(StringUtils.isBlank(query)){
            domainVOList.add(domainVO);
        }else{
            //如果存在查询条件
            List<Integer> queryLineList = ContentUtils.queryContentLine(query,domainMetadata.getContent());
            if(domainVO.isDirectory() || queryLineList.size()>0){
                for(int index:queryLineList){
                    QueryContentVO queryContentVO = new QueryContentVO();
                    queryContentVO.setContent(domainMetadata.getContentList().get(index-1).trim());
                    queryContentVO.setHighlighter(query);
                    queryContentVO.setLine(index);
                    domainVO.getQueryContentList().add(queryContentVO);
                }
                domainVOList.add(domainVO);
            }
        }
        for(DomainMetadata childDomainMetadata:domainMetadata.getDomainMetadataList()){
            handleDomainTree(level+1,domainVOList,childDomainMetadata,query);
        }
    }

}
