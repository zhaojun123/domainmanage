package com.zkml.domainmanage.support.metadata;

import com.zkml.domainmanage.support.*;

import java.io.IOException;

/**
 * 读取代码模版进行相应的处理
 */
public interface DomainTemplateResolver {

    /**
     * 套用模版解析获取 field的content getMethod的Content setMethodContent的集合
     * @param object
     */
    String parseFieldByTemplate(Object object);

    /**
     * 默认实现，从file文件读取模版进行处理
     */
    class DefaultDomainTemplateResolver implements DomainTemplateResolver{

        private DomainManageProperties domainManageProperties;

        private final  String fieldTemplate;

        private final  String smethodTemplate;

        private final  String gmethodTemplate;

        public DefaultDomainTemplateResolver(DomainManageProperties domainManageProperties) throws IOException {
            this.domainManageProperties = domainManageProperties;
            //获取模版内容，去除空格行,默认情况下
            this.fieldTemplate = ContentUtils.trimBlankLines(
                    ClassLoaderUtils.getContent(domainManageProperties.getFieldTemplate()));
            this.smethodTemplate = ContentUtils.trimBlankLines(
                    ClassLoaderUtils.getContent(domainManageProperties.getSmethodTemplate()));
            this.gmethodTemplate = ContentUtils.trimBlankLines(
                    ClassLoaderUtils.getContent(domainManageProperties.getGmethodTemplate()));
        }

        @Override
        public String parseFieldByTemplate(Object object) {
            FieldMetadata fieldMetadata = (FieldMetadata)object;
            //拼接模版
            String fieldName = fieldMetadata.getFieldName();
            String fieldType = fieldMetadata.getFieldType();
            String note = fieldMetadata.getProcessedNote();

            String fieldResult = this.fieldTemplate
                    .replace("#{fieldName}",fieldName)
                    .replace("#{fieldType}",fieldType)
                    .replace("#{note}",note);

            if(fieldMetadata.getStatus()== FieldMetadata.Status.GET
                    || fieldMetadata.getStatus() == FieldMetadata.Status.ALL){
                fieldResult = fieldResult +"\n"+parseGMethodByTemplate(fieldName,fieldType,note);
            }

            if(fieldMetadata.getStatus() == FieldMetadata.Status.SET
                    || fieldMetadata.getStatus() == FieldMetadata.Status.ALL){
                fieldResult = fieldResult +"\n"+ parseSMethodByTemplate(fieldName,fieldType,note);
            }

            return fieldResult;
        }

        /**
         * 解析getMethod
         * @param fieldName
         * @param fieldType
         * @param note
         * @return
         */
        private String parseGMethodByTemplate(String fieldName,String fieldType,String note){
            String getMethodName = ReflectUtils.getMethodName(fieldName);
            return gmethodTemplate.replace("#{fieldName}",fieldName)
                    .replace("#{fieldType}",fieldType)
                    .replace("#{getMethodName}",getMethodName)
                    .replace("#{note}",note);
        }

        /**
         * 解析setMethod
         * @param fieldName
         * @param fieldType
         * @param note
         * @return
         */
        private String parseSMethodByTemplate(String fieldName,String fieldType,String note){
            String setMethodName = ReflectUtils.setMethodName(fieldName);
            return smethodTemplate.replace("#{fieldName}",fieldName)
                    .replace("#{fieldType}",fieldType)
                    .replace("#{setMethodName}",setMethodName)
                    .replace("#{note}",note);
        }

    }


}
