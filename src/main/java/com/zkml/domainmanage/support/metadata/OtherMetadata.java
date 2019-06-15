package com.zkml.domainmanage.support.metadata;

/**
 *  除了domainMetadata、fieldMetadata、methodMetadata之外的通用模型
 */
public class OtherMetadata extends BaseMetada{

    public OtherMetadata(){
        super();
    }

    public OtherMetadata(String content,MetadataType metadataType) {
        super(content);
        this.metadataType = metadataType;
    }

    private MetadataType metadataType;

    public MetadataType getMetadataType() {
        return metadataType;
    }

    public void setMetadataType(MetadataType metadataType) {
        this.metadataType = metadataType;
    }

    public OtherMetadata(Builder builder){
        this.setStartLine(builder.startLine);
        this.setEndLine(builder.endLine);
        this.setContent(builder.content);
        this.setFrontBlankCount(builder.frontBlankCount);
        this.setSortNo(builder.sortNo);
        this.metadataType = builder.metadataType;
    }

    public static class Builder{

        private int startLine = 0; //开始行数

        private int endLine = 0; //结束行数

        private String content; //文件内容

        private int frontBlankCount = 0; //前置空格数

        private int sortNo = -1; //与同类元素的排序 从0开始 -1表示排队尾

        private MetadataType metadataType; //类型

        public Builder startLine(int startLine){
            this.startLine = startLine;
            return this;
        }

        public Builder endLine(int endLine){
            this.endLine = endLine;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder frontBlankCount(int frontBlankCount){
            this.frontBlankCount = frontBlankCount;
            return this;
        }

        public Builder sortNo(int sortNo){
            this.sortNo = sortNo;
            return this;
        }

        public Builder metadataType(MetadataType metadataType){
            this.metadataType = metadataType;
            return this;
        }

        public OtherMetadata build(){
            return new OtherMetadata(this);
        }
    }

    /**
     * 元素类型
     */
    public enum MetadataType{
        IMPORT,
        PACKAGE,
        NOTE
    }

    @Override
    String getFullContent() {
        return getContent();
    }
}
