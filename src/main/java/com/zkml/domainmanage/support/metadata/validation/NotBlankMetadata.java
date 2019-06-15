package com.zkml.domainmanage.support.metadata.validation;

import org.apache.commons.lang3.StringUtils;

/**
 * javax.validation.constraints.NotBlank注解模型
 */
public class NotBlankMetadata extends ValidationMetadata{


    public NotBlankMetadata() {
        super(ValidationEnum.NOT_BLANK);
    }

    @Override
    public String assembleContent() {
        if(StringUtils.isNotBlank(getMessage())){
            return "@NotBlank(message = \""+getMessage()+"\")";
        }else{
            return "@NotBlank";
        }
    }
}
