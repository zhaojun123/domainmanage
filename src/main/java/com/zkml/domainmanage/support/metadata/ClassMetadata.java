package com.zkml.domainmanage.support.metadata;

import com.zkml.domainmanage.support.ContentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * class模型，包括className、class上的注释、class上的注解
 */
public class ClassMetadata extends BaseTypeMetadata{

    private String className; //class名称

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
