package com.zkml.domainmanage.support;

import com.zkml.domainmanage.support.metadata.swagger.SwaggerMetadata;
import com.zkml.domainmanage.support.metadata.swagger.SwaggerProcess;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 加载Swagger注解支持
 */
@Configuration
@ConditionalOnClass(SwaggerMetadata.class)
public class SwaggerAnnotationAutoConfig {

    @Bean
    public SwaggerProcess initSwaggerProcess(){
        return new SwaggerProcess();
    }

}
