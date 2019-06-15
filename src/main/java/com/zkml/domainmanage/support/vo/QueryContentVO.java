package com.zkml.domainmanage.support.vo;

public class QueryContentVO {

    private Integer line; //内容所在行

    private String content; //内容

    private String highlighter; //需要高亮的部分

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHighlighter() {
        return highlighter;
    }

    public void setHighlighter(String highlighter) {
        this.highlighter = highlighter;
    }
}
