package com.zkml.domainmanage.support.metadata;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Annotation基类
 */
public abstract class BaseAnnotationMetadata extends BaseMetada{

    String annotationName;

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    @Override
    String getFullContent() {
        return getContent();
    }

    @Override
    public String getContent(){
        String content = super.getContent();
        if(content == null)
            content = assembleContent();
        return content;
    }


    /**
     * 组装输出最终的注解文本
     * @return
     */
    public abstract String assembleContent();

    /**
     * 根据各注解自己的特点对输入的文本进行进一步解析
     */
    public abstract void analysisContent();

    /**
     * 根据属性名获取annotation标签对应的属性值,只获取第一个匹配的值
     * @param attrName
     * @return
     */
    public <T> T getFirstAttrByName(String attrName,Class<T> returnType){
        List<T> list = getAttrByName(attrName,returnType);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }else{
            return list.get(0);
        }
    }

    /**
     * String 转换成其它基本类型
     * @param content
     * @param classType
     * @param <T>
     * @return
     */
    private <T> T stringSwitchType(String content,Class<T> classType){
        if(Boolean.class.getName().equals(classType.getName())){
            return (T)Boolean.valueOf(content);
        }else if(Integer.class.getName().equals(classType.getName())){
            return (T)Integer.valueOf(content);
        }else if(Long.class.getName().equals(classType.getName())){
            return (T)Long.valueOf(content);
        }else if(Double.class.getName().equals(classType.getName())){
            return (T)Double.valueOf(content);
        }else if(Float.class.getName().equals(classType.getName())){
            return (T)Float.valueOf(content);
        }else if(BigDecimal.class.getName().equals(classType.getName())){
            return (T)new BigDecimal(content);
        }
        return (T)content;
    }

    /**
     * 根据属性名获取annotation标签对应的属性值（只支持简单基础类型）
     * @return
     */
    public <T> List<T> getAttrByName(String attrName,Class<T> returnType){
        List<T> resultList = new ArrayList<>();
        String content = getContent();
        if(StringUtils.isBlank(content) || StringUtils.isBlank(attrName))
            return resultList;

        Pattern pattern = Pattern.compile(attrName+"(\\s)*=(\\s)*((\\{((?![\\}]).)*[\\}])|((?![,\\)\\{\\}]).)*[,\\)])");
        Matcher matcher = pattern.matcher(content);
        List<String> valueList = new ArrayList<>();
        while(matcher.find()){
            String group = matcher.group();
            //如果是以}结尾，说明是多个值
            if(group.endsWith("}")){
                valueList.add(group.split("=")[1]);
            }else{
                valueList.add(group.substring(0,group.length()-1).split("=")[1]);
            }
        }
        //如果没匹配到
        if(valueList.size()==0){
            //如果要查的attrName是value
            if(attrName.equalsIgnoreCase("value") && content.indexOf("=")<0){
                if(content.indexOf("(")>0){
                    String group = content.split("\\(")[1].replace(")","");
                    valueList.add(group);
                }
            }
        }
        for(String group:valueList){
            group = group.replace("\"","");
            if(group.endsWith("}")){
                String[] values = group.replace("{","")
                        .replace("}","")
                        .split(",");
                for(String value:values){
                    resultList.add(stringSwitchType(value.trim(),returnType));
                }
            }else{
                resultList.add(stringSwitchType(group.trim(),returnType));
            }
        }
        return resultList;
    }
}
