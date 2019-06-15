package com.zkml.domainmanage.support.metadata.swagger;

/**
 * ApiModel注解模型
 */
public class ApiModelMetadata extends SwaggerMetadata {

    //描述
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ApiModelMetadata() {
        super(SwaggerEnum.API_MODEL);
    }

    @Override
    public String assembleContent() {
        return "@ApiModel(\""+value+"\")";
    }

    @Override
    public void analysisContent() {
        this.value = getFirstAttrByName("value",String.class);
    }
}
