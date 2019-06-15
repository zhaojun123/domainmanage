package com.zkml.domainmanage.support.metadata;

import java.util.List;

/**
 * 基础Metada
 */
public abstract class BaseMetada {

    private int startLine = 0; //开始行数 这里的开始结束行数包括元素所属的注解、注释等在一起

    private int endLine = 0; //结束行数

    //文件内容  只记录元素本身的文本信息，不包括所属的注解、注释，如果需要完整文本请使用getFullContent()，由子类继承实现
    private String content;

    private int frontBlankCount = 0; //前置空格数，

    private int sortNo = -1; //与同类元素的排序 从0开始 -1表示排队尾

    private int frontBlankLineCount = 0; //前置空行数，和前面同类型元素保持的间距数

    public BaseMetada(){

    }

    public BaseMetada(String content){
        this.content = content;
    }

    public int getFrontBlankCount() {
        return frontBlankCount;
    }

    public void setFrontBlankCount(int frontBlankCount) {
        this.frontBlankCount = frontBlankCount;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFrontBlankLineCount() {
        return frontBlankLineCount;
    }

    public void setFrontBlankLineCount(int frontBlankLineCount) {
        this.frontBlankLineCount = frontBlankLineCount;
    }

    /**
     * 和另外一个Metada进行比较，获得较小的startLine，并且进行设置
     * @param compareMetada
     * @param compareMetada
     */
    public void compareStartLine(BaseMetada compareMetada){
        setStartLine(Math.min(startLine,compareMetada.getStartLine()));
    }

    /**
     * 根据行数生成文本
     * @param line
     */
    public void joinLine(List<String> line){
        StringBuilder contentBuilder = new StringBuilder("");
        int index = startLine;
        while(index <= endLine){
            contentBuilder.append("\n").append(line.get(index));
            index++;
        }
        setContent(contentBuilder.substring(1).toString());
    }

    /**
     * 获取该元素以及附属元素的文本内容组合,由子类继承实现
     * @return
     */
    abstract String getFullContent();
}
