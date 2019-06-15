package com.zkml.domainmanage.support;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("domain.manage")
public class DomainManageProperties {

    private String webTemplatePrefix = "classpath:/html/domainmanage/";

    private String encoding = "UTF-8";

    //如果路径是在项目外，请使用 file:开头的绝对路径
    private String fieldTemplate = "classpath:/templates/domainmanage/fieldTemplate";

    private String smethodTemplate = "classpath:/templates/domainmanage/smethodTemplate";

    private String gmethodTemplate = "classpath:/templates/domainmanage/gmethodTemplate";

    //默认地址在${user.dir}/import.properties
    private String importPropertiesPath;

    //默认地址在${user.dir}/example.properties
    private String examplePropertiesPath;

    public String getExamplePropertiesPath() {
        return examplePropertiesPath;
    }

    public void setExamplePropertiesPath(String examplePropertiesPath) {
        this.examplePropertiesPath = examplePropertiesPath;
    }

    public String getImportPropertiesPath() {
        return importPropertiesPath;
    }

    public void setImportPropertiesPath(String importPropertiesPath) {
        this.importPropertiesPath = importPropertiesPath;
    }

    public String getFieldTemplate() {
        return fieldTemplate;
    }

    public void setFieldTemplate(String fieldTemplate) {
        this.fieldTemplate = fieldTemplate;
    }

    public String getSmethodTemplate() {
        return smethodTemplate;
    }

    public void setSmethodTemplate(String smethodTemplate) {
        this.smethodTemplate = smethodTemplate;
    }

    public String getGmethodTemplate() {
        return gmethodTemplate;
    }

    public void setGmethodTemplate(String gmethodTemplate) {
        this.gmethodTemplate = gmethodTemplate;
    }

    public String getWebTemplatePrefix() {
        return webTemplatePrefix;
    }

    public void setWebTemplatePrefix(String webTemplatePrefix) {
        this.webTemplatePrefix = webTemplatePrefix;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
