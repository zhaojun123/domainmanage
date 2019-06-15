package com.zkml.domainmanage.support.metadata.validation;
import org.apache.commons.lang3.StringUtils;

/**
 * javax.validation.constraints.NotNull注解模型
 */
public class NotNullMetadata extends ValidationMetadata{


    public NotNullMetadata() {
        super(ValidationEnum.NOT_NULL);
    }


    @Override
    public String assembleContent() {
        if(StringUtils.isNotBlank(getMessage())){
            return "@NotNull(message = \""+getMessage()+"\")";
        }else{
            return "@NotNull";
        }
    }
}
