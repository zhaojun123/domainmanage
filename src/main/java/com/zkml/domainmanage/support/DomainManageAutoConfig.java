package com.zkml.domainmanage.support;

import com.zkml.domainmanage.support.filter.DomainFilter;
import com.zkml.domainmanage.support.metadata.*;
import com.zkml.domainmanage.support.metadata.process.AfterInitMetadataProcess;
import com.zkml.domainmanage.support.metadata.process.BeforeAddMetadataProcess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(DomainManageProperties.class)
public class DomainManageAutoConfig {

    private DomainManageProperties properties;

    private List<AfterInitMetadataProcess> afterInitMetadataProcessList = new ArrayList();

    private List<BeforeAddMetadataProcess> beforeAddMetadataProcessesList = new ArrayList();

    DomainManageAutoConfig(SpringResourceTemplateResolver springResourceTemplateResolver,DomainManageProperties properties){
        this.properties = properties;
        springResourceTemplateResolver.setPrefix(properties.getWebTemplatePrefix());
        springResourceTemplateResolver.setCharacterEncoding(properties.getEncoding());
        springResourceTemplateResolver.setCacheable(false);
    }

    /**
     * 可以自己实现AfterInitMetadataProcess接口
     * 在DomainMetadata初始化后触发
     * see {@link DomainMetadataHandle#init(DomainMetadata)}
     * @param afterInitMetadataProcessList
     */
    @Autowired(required = false)
    public void setAfterInitMetadataProcessList(List<AfterInitMetadataProcess> afterInitMetadataProcessList){
        if(!CollectionUtils.isEmpty(afterInitMetadataProcessList)){
            this.afterInitMetadataProcessList.addAll(afterInitMetadataProcessList);
        }
    }

    /**
     * 可以自己实现BeforeAddMetadataProcess接口
     * 在BaseMetadata添加前触发
     * see {@link DomainMetadataHandle#add(FieldMetadata, DomainMetadata,FieldMetadata.Status)}
     * see {@link DomainMetadataHandle#update(ClassMetadata, DomainMetadata)}
     * @param beforeAddMetadataProcessesList
     */
    @Autowired(required = false)
    public void setBeforeAddFieldProcessesList(List<BeforeAddMetadataProcess> beforeAddMetadataProcessesList){
        if(!CollectionUtils.isEmpty(beforeAddMetadataProcessesList)){
            this.beforeAddMetadataProcessesList.addAll(beforeAddMetadataProcessesList);
        }
    }

    /**
     * domain处理类
     * @return
     */
    @ConditionalOnMissingBean(DomainMetadataHandle.class)
    @Bean
    public DomainMetadataHandle initDomainMetadataHandle(){
        return new DefaultDomainMetadataHandle(beforeAddMetadataProcessesList,afterInitMetadataProcessList);
    }


    /**
     * domain模版处理类
     * @return
     * @throws IOException
     */
    @ConditionalOnMissingBean(DomainTemplateResolver.class)
    @Bean
    public DomainTemplateResolver initDomainTemplateResolver() throws IOException {
        return new DomainTemplateResolver.DefaultDomainTemplateResolver(properties);
    }

    @ConditionalOnMissingBean(FieldExampleResolver.class)
    @Bean
    public FieldExampleResolver initFieldExampleResolver() throws IOException {
        String propertiesPath = properties.getExamplePropertiesPath();
        if(StringUtils.isBlank(propertiesPath)){
            propertiesPath = System.getProperty("user.dir")+"/example.properties";
            File file = new File(propertiesPath);
            if(!file.exists())
                file.createNewFile();
            propertiesPath = "file:"+propertiesPath;
        }
        return new FieldExampleResolver(propertiesPath);
    }

    /**
     * domain处理上下文
     * @return
     */
    @Bean
    public DomainContext domainContext(){
        DomainContext domainContext = new DomainContext();
        return domainContext;
    }

    /**
     * 添加domain过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean domainFilter(DomainManageService domainManageService){
        DomainFilter domainFilter = new DomainFilter(domainManageService);
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(domainFilter);
        filterRegistrationBean.addUrlPatterns("/domainManage/domain/*");
        return filterRegistrationBean;
    }


}
