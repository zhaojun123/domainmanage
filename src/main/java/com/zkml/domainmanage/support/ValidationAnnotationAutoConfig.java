package com.zkml.domainmanage.support;

import com.zkml.domainmanage.support.metadata.validation.ValidationMetadata;
import com.zkml.domainmanage.support.metadata.validation.ValidationProcess;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 加载javax.validation注解支持
 */
@Configuration
@ConditionalOnClass(ValidationMetadata.class)
public class ValidationAnnotationAutoConfig {

    @Bean
    public ValidationProcess initValidationProcess(){
        return new ValidationProcess();
    }
}
