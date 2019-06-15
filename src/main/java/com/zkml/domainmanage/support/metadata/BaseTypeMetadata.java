package com.zkml.domainmanage.support.metadata;

import com.zkml.domainmanage.support.ContentUtils;
import com.zkml.domainmanage.support.DomainUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * method、field、class模型共同的抽象类
 */
abstract class BaseTypeMetadata extends BaseMetada{

    public BaseTypeMetadata(){
        super();
    }

    public BaseTypeMetadata(String content) {
        super(content);
    }

    private List<OtherMetadata> noteList = new ArrayList<>(); //注释

    private List<BaseAnnotationMetadata> annotationList = new ArrayList<>();//注解

    private OtherMetadata onlyShowNote; //针对于有些人书写习惯是把注释和代码写在同一行，所以这些注释只用于显示，不做写操作

    public OtherMetadata getOnlyShowNote() {
        return onlyShowNote;
    }

    public void setOnlyShowNote(OtherMetadata onlyShowNote) {
        this.onlyShowNote = onlyShowNote;
    }

    public List<OtherMetadata> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<OtherMetadata> noteList) {
        this.noteList = noteList;
    }

    public List<BaseAnnotationMetadata> getAnnotationList() {
        return annotationList;
    }

    public void setAnnotationList(List<BaseAnnotationMetadata> annotationList) {
        this.annotationList = annotationList;
    }

    /**
     * 组合note、annotation、自身的文本
     * @return
     */
    @Override
    public String getFullContent(){
        int frontBlankCount = getFrontBlankCount();
        List<String> metadataContentList = new ArrayList<>();
        for(OtherMetadata note:noteList){
            metadataContentList.add(note.getFullContent());
        }
        for(BaseAnnotationMetadata annotationMetadata:annotationList){
            metadataContentList.add(annotationMetadata.getFullContent());
        }
        metadataContentList.add(getContent());
        return ContentUtils.joinContents(frontBlankCount,metadataContentList);
    }

    /**
     * 去除注释的 /* // @ 等格式，获取比较干净的注释文本
     * @return
     */
    public String getProcessedNote(){
        StringBuffer result = new StringBuffer();
        for(OtherMetadata note:noteList){
            result.append(".").append(DomainUtils.processedNoteContent(note.getContent()));
        }
        if(onlyShowNote!=null)
            result.append(".").append(DomainUtils.processedNoteContent(onlyShowNote.getContent()));
        return result.length()>0?result.substring(1):"";
    }
}
