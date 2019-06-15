package com.zkml.domainmanage.support.metadata.swagger;

/**
 * ApiModelProperty模型
 */
public class ApiModelPropertyMetadata extends SwaggerMetadata{

    public ApiModelPropertyMetadata(){
        super(SwaggerEnum.API_MODEL_PROPERTY);
    }

    //字段说明
    private String value;

    //字段示例值
    private String example;

    //是否必填
    private Boolean required = false;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String assembleContent() {
        return "@ApiModelProperty(value = \""+value+"\",example = \""+example+"\",required = "+required+")";
    }

    @Override
    public void analysisContent() {
        this.value = getFirstAttrByName("value",String.class);
        this.example = getFirstAttrByName("example",String.class);
        this.required = getFirstAttrByName("value",Boolean.class);
        if(this.required == null)
            required = false;
    }
}
