package com.zkml.domainmanage.support.metadata;

import com.zkml.domainmanage.support.ContentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法模型 开始行数、结束行数、内容 包括了注解、注释
 */
public class MethodMetadata extends BaseTypeMetadata{

    public MethodMetadata(){
        super();
    }

    public MethodMetadata(String content) {
        super(content);
    }

    private String methodName; //方法名称

    private FieldMetadata fieldMetadata; //属于哪个field

    private int frontBlankCount = 4; //前置空格数

    private int frontBlankLineCount = 1; //前置空行数，和前面同类型元素保持的间距数

    public FieldMetadata getFieldMetadata() {
        return fieldMetadata;
    }

    public void setFieldMetadata(FieldMetadata fieldMetadata) {
        this.fieldMetadata = fieldMetadata;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public int getFrontBlankLineCount() {
        return frontBlankLineCount;
    }

    @Override
    public void setFrontBlankLineCount(int frontBlankLineCount) {
        this.frontBlankLineCount = frontBlankLineCount;
    }

    @Override
    public int getFrontBlankCount() {
        return frontBlankCount;
    }

    @Override
    public void setFrontBlankCount(int frontBlankCount) {
        this.frontBlankCount = frontBlankCount;
    }

}
