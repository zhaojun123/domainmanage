package com.zkml.domainmanage.support.metadata.validation;

import org.apache.commons.lang3.StringUtils;

/**
 * javax.validation.constraints.NotEmpty注解模型
 */
public class NotEmptyMetadata extends ValidationMetadata{

    public NotEmptyMetadata() {
        super(ValidationEnum.NOT_EMPTY);
    }

    @Override
    public String assembleContent() {
        if(StringUtils.isNotBlank(getMessage())){
            return "@NotEmpty(message = \""+getMessage()+"\")";
        }else{
            return "@NotEmpty";
        }
    }
}
