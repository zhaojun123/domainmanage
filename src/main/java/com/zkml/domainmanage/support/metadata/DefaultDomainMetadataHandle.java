package com.zkml.domainmanage.support.metadata;

import com.zkml.domainmanage.support.ContentUtils;
import com.zkml.domainmanage.support.DomainFileUtils;
import com.zkml.domainmanage.support.DomainUtils;
import com.zkml.domainmanage.support.exception.DomainServiceException;
import com.zkml.domainmanage.support.metadata.process.AfterInitMetadataProcess;
import com.zkml.domainmanage.support.metadata.process.BeforeAddMetadataProcess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultDomainMetadataHandle implements DomainMetadataHandle{

    private List<AfterInitMetadataProcess> afterInitMetadataProcessList;

    private List<BeforeAddMetadataProcess> beforeAddMetadataProcessesList;

    public DefaultDomainMetadataHandle(List<BeforeAddMetadataProcess> beforeAddMetadataProcessesList
            ,List<AfterInitMetadataProcess> afterInitMetadataProcessList){
        this.afterInitMetadataProcessList = afterInitMetadataProcessList;
        this.beforeAddMetadataProcessesList = beforeAddMetadataProcessesList;
    }

    /**
     * 初始化DomainMetadata,这里传入的DomainMetadata包含localPath、fileName,content、className
     * 需要解析fieldMetadataList、methodMetadataList、importList、classNote、fullClassName
     * @param domainMetadata
     */
    public DomainMetadata init(DomainMetadata domainMetadata){
        if(!domainMetadata.isDirectory()){
            synchronized (domainMetadata){
                DomainDelegate domainDelegate = new DomainDelegate(domainMetadata);
                domainMetadata =  domainDelegate.init();
                for(AfterInitMetadataProcess afterInitMetadataProcess:afterInitMetadataProcessList){
                    afterInitMetadataProcess.afterInitProcess(domainMetadata);
                }
                return domainMetadata;
            }
        }
        return null;
    }

    @Override
    public void delete(List<FieldMetadata> fieldMetadataList,DomainMetadata domainMetadata,FieldMetadata.Status status){
        if(!domainMetadata.isDirectory()){
            synchronized (domainMetadata){
                domainMetadata.checkModified(this);
                //需要删除的对象
                List<BaseMetada> removeBaseMetadaList = new ArrayList<>();
                for(FieldMetadata fieldMetadata:fieldMetadataList){
                    //根据fieldName在domain中查找
                    FieldMetadata oldFieldMetadata = domainMetadata.findFieldByName(fieldMetadata.getFieldName());
                    if(oldFieldMetadata!=null){
                        removeBaseMetadaList.add(oldFieldMetadata);

                        if(oldFieldMetadata.getsMethod()!=null
                                && (status== FieldMetadata.Status.SET
                                || status == FieldMetadata.Status.ALL))
                            removeBaseMetadaList.add(oldFieldMetadata.getsMethod());

                        if(oldFieldMetadata.getgMethod()!=null
                                && (status== FieldMetadata.Status.GET
                                || status == FieldMetadata.Status.ALL))
                            removeBaseMetadaList.add(oldFieldMetadata.getgMethod());
                    }
                }
                deleteTofile(domainMetadata,removeBaseMetadaList);
                //重新初始化分析该文件
                domainMetadata.update(this);
            }
        }
    }

    /**
     * 删除field
     * @param fieldMetadata
     */
    @Override
    public void delete(FieldMetadata fieldMetadata,DomainMetadata domainMetadata,FieldMetadata.Status status){
        delete(Arrays.asList(fieldMetadata),domainMetadata,status);
    }

    @Override
    public void delete(List<OtherMetadata> importList, DomainMetadata domainMetadata) {
        if(!domainMetadata.isDirectory()){
            synchronized (domainMetadata){
                domainMetadata.checkModified(this);
                //需要删除的对象
                List<BaseMetada> removeBaseMetadaList = new ArrayList<>();
                for(OtherMetadata otherMetadata : importList){
                    OtherMetadata old = domainMetadata.findImportByName(otherMetadata.getContent());
                    if(old != null){
                        removeBaseMetadaList.add(old);
                    }
                }
                if(!CollectionUtils.isEmpty(removeBaseMetadaList)){
                    deleteTofile(domainMetadata,removeBaseMetadaList);
                    //重新初始化分析该文件
                    domainMetadata.update(this);
                }
            }
        }
    }

    /**
     * 批量修改field
     * @param fieldMetadataList
     * @param domainMetadata
     * @param status
     */
    public void update(List<FieldMetadata> fieldMetadataList,DomainMetadata domainMetadata,FieldMetadata.Status status){
        if(!domainMetadata.isDirectory()) {
            synchronized (domainMetadata) {
                delete(fieldMetadataList,domainMetadata,status);
                add(fieldMetadataList,domainMetadata,status);
            }
        }
    }

    /**
     * 修改field
     * @param fieldMetadata
     */
    @Override
    public void update(FieldMetadata fieldMetadata,DomainMetadata domainMetadata,FieldMetadata.Status status){
        if(!domainMetadata.isDirectory()){
            synchronized (domainMetadata){
                //先删除后添加
                delete(fieldMetadata,domainMetadata,status);
                add(fieldMetadata,domainMetadata,status);
            }
        }
    }

    /**
     * 批量添加importList
     * @param importList
     * @param domainMetadata
     */
    @Override
    public void add(List<OtherMetadata> importList, DomainMetadata domainMetadata) {
        if(!domainMetadata.isDirectory()){
            synchronized (domainMetadata){
                if(!CollectionUtils.isEmpty(importList)){
                    domainMetadata.checkModified(this);
                    for(BeforeAddMetadataProcess beforeAddMetadataProcess:beforeAddMetadataProcessesList){
                        beforeAddMetadataProcess.beforeAddProcess(importList,domainMetadata,this);
                    }
                    List<BaseMetada> addBaseMetadaList = new ArrayList<>();

                    for(OtherMetadata importMetadata:importList){

                        //默认插入点设置在 package 后面一行
                        int insertImportIndex = domainMetadata.getPackageMetadata().getStartLine()+1;

                        //默认前置空格为0
                        int importFrontBlankCount = 0;

                        //检查import的内容 添上import关键字
                        String importName = importMetadata.getContent().trim();
                        if(!importName.startsWith("import"))
                            importName = "import "+importName;
                        if(!importName.endsWith(";"))
                            importName = importName+";";
                        importMetadata.setContent(importName);
                        importMetadata.setStartLine(insertImportIndex);
                        importMetadata.setFrontBlankCount(importFrontBlankCount);

                        handleBeforeAdd(domainMetadata.getImportList(),importMetadata);
                        addBaseMetadaList.add(importMetadata);
                    }
                    //写入文件
                    addToFile(domainMetadata,addBaseMetadaList);
                    //重新初始化分析该文件
                    domainMetadata.update(this);
                }
            }
        }
    }

    /**
     * 批量添加field
     * @param fieldMetadataList
     * @param domainMetadata
     * @param status
     */
    @Override
    public void add(List<FieldMetadata> fieldMetadataList,DomainMetadata domainMetadata,FieldMetadata.Status status){
        if(!domainMetadata.isDirectory()) {
            synchronized (domainMetadata) {
                domainMetadata.checkModified(this);
                for(BeforeAddMetadataProcess beforeAddMetadataProcess:beforeAddMetadataProcessesList){
                    beforeAddMetadataProcess.beforeAddProcess(fieldMetadataList,domainMetadata,this);
                }
                domainMetadata.checkModified(this);
                //等待添加的元素
                List<BaseMetada> addBaseMetadaList = new ArrayList<>();
                //为了保证原有的field顺序，这里对list进行反转
                Collections.reverse(fieldMetadataList);
                for(FieldMetadata fieldMetadata:fieldMetadataList){
                    //默认插入点设置在class 后面一行
                    int insertFieldIndex = domainMetadata.getClassStartLine()+1;

                    //默认前置空格为4
                    int fieldFrontBlankCount = 4;
                    fieldMetadata.setStartLine(insertFieldIndex);
                    fieldMetadata.setFrontBlankCount(fieldFrontBlankCount);
                    handleBeforeAdd(domainMetadata.getFieldMetadataList(),fieldMetadata);


                    //如果有set方法
                    MethodMetadata sMethod = fieldMetadata.getsMethod();
                    if(sMethod!=null && (status == FieldMetadata.Status.SET
                            || status == FieldMetadata.Status.ALL)){

                        sMethod.setFrontBlankCount(fieldMetadata.getFrontBlankCount());
                        sMethod.setStartLine(fieldMetadata.getStartLine());
                        handleBeforeAdd(domainMetadata.getMethodMetadataList(),sMethod);
                        addBaseMetadaList.add(sMethod);
                    }

                    //如果有get方法
                    MethodMetadata gMethod = fieldMetadata.getgMethod();
                    if(gMethod!=null && (status == FieldMetadata.Status.GET
                            || status == FieldMetadata.Status.ALL)){

                        gMethod.setFrontBlankCount(fieldMetadata.getFrontBlankCount());
                        gMethod.setStartLine(fieldMetadata.getStartLine());
                        handleBeforeAdd(domainMetadata.getMethodMetadataList(),gMethod);
                        addBaseMetadaList.add(gMethod);
                    }

                    addBaseMetadaList.add(fieldMetadata);

                }
                //写入文件
                addToFile(domainMetadata,addBaseMetadaList);
                //重新初始化分析该文件
                domainMetadata.update(this);
            }
        }
    }

    /**
     * 添加field
     * @param fieldMetadata 包含fieldContent getMethodContent，setMethodContent，sortNo，importNameList,annotationList
     */
    @Override
    public void add(FieldMetadata fieldMetadata,DomainMetadata domainMetadata,FieldMetadata.Status status){
        add(Arrays.asList(fieldMetadata), domainMetadata,status);
    }

    @Override
    public void update(ClassMetadata classMetadata,DomainMetadata domainMetadata) {
        if(!domainMetadata.isDirectory()){
            synchronized (domainMetadata){
                domainMetadata.checkModified(this);
                for(BeforeAddMetadataProcess beforeAddMetadataProcess:beforeAddMetadataProcessesList){
                    beforeAddMetadataProcess.beforeAddProcess(Arrays.asList(classMetadata),domainMetadata,this);
                }
                domainMetadata.checkModified(this);

                //删除class上包含的note annotation
                ClassMetadata oldClass = domainMetadata.getClassMetadata();
                if(oldClass.getStartLine() != oldClass.getEndLine()){
                    oldClass.setEndLine(oldClass.getEndLine()-1);
                    List<BaseMetada> removeBaseMetadaList = new ArrayList<>();
                    removeBaseMetadaList.add(oldClass);
                    deleteTofile(domainMetadata,removeBaseMetadaList);
                    init(domainMetadata);
                }

                //重新添加class上包含的note annotation
                int insertIndex = domainMetadata.getClassStartLine();
                List<BaseMetada> addBaseMetadaList = new ArrayList<>();
                for(BaseAnnotationMetadata annotation:classMetadata.getAnnotationList()){
                    annotation.setStartLine(insertIndex);
                    addBaseMetadaList.add(annotation);
                }
                for(OtherMetadata note : classMetadata.getNoteList()){
                    note.setStartLine(insertIndex);
                    addBaseMetadaList.add(note);
                }
                if(!CollectionUtils.isEmpty(addBaseMetadaList))
                    addToFile(domainMetadata,addBaseMetadaList);
                domainMetadata.update(this);
            }
        }
    }


    /**
     * 在domain中查找相应的baseMetada是否重复
     * @param domainMetadata
     * @param baseMetada
     * @return
     */
    private boolean duplicateBaseMetada(DomainMetadata domainMetadata,BaseMetada baseMetada){
        if(baseMetada == null)
            return false;
        if(baseMetada instanceof FieldMetadata){
            FieldMetadata fieldMetadata = (FieldMetadata)baseMetada;
            String fileName = DomainUtils.getFieldNameByContent(fieldMetadata.getContent());
            if(domainMetadata.findFieldByName(fileName)!=null)
                return true;
        }else if(baseMetada instanceof MethodMetadata){
            MethodMetadata methodMetadata = (MethodMetadata)baseMetada;
            String methodName = DomainUtils.getMethodNameByContent(methodMetadata.getContent());
            if(domainMetadata.findMethodByName(methodName)!=null)
                return true;
        }else if(baseMetada instanceof OtherMetadata){
            OtherMetadata otherMetadata = (OtherMetadata)baseMetada;
            switch (otherMetadata.getMetadataType()){
                //针对于import 存在重复或者import属于同包都返回true
                case IMPORT:
                    if(domainMetadata.findImportByName(otherMetadata.getContent())!= null
                            ||domainMetadata.samePackage(otherMetadata.getContent()))
                        return true;
                    break;
                case PACKAGE:
                    if(domainMetadata.findPackageByName(otherMetadata.getContent())!=null)
                        return true;
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    /**
     * 将需要删除的元素组装好，根据startLine从大到小 依次删除，写入文件
     * @param domainMetadata
     * @param baseMetadaList
     * @throws IOException
     */
    private DomainMetadata deleteTofile(DomainMetadata domainMetadata,List<BaseMetada> baseMetadaList){
        //对baseMetadaList进行排序 按照 StartLine从大到小
        Collections.sort(baseMetadaList,(a, b)-> b.getStartLine() - a.getStartLine());

        //从domain中copy一份内容
        List<String> resultContentList = DomainUtils.copyModifiableList(domainMetadata.getContentList());

        for(BaseMetada baseMetada:baseMetadaList){
            //检查需要删除的元素前一行是不是空行，并且该元素是需要保持间距的，则删除前面一个空行
            if(baseMetada.getStartLine()>0 && baseMetada.getFrontBlankLineCount()>0){
                if(StringUtils.isBlank(resultContentList.get(baseMetada.getStartLine()-1)))
                    baseMetada.setStartLine(baseMetada.getStartLine()-1);
            }
            ListIterator<String> it = resultContentList.listIterator(baseMetada.getStartLine());
            for (int i=0, n=baseMetada.getEndLine()-baseMetada.getStartLine(); i<=n; i++) {
                it.next();
                it.remove();
            }
        }
        String resultContent = DomainUtils.joinStringList(resultContentList);

        try {
            DomainFileUtils.writeFileContent(domainMetadata.getLocalPath(),resultContent);
            domainMetadata.setContent(resultContent);
            domainMetadata.setContentList(Collections.unmodifiableList(resultContentList));
        }catch (IOException e) {
            throw new DomainServiceException("写入文件【"+domainMetadata.getLocalPath()+"】出错",e);
        }
        return  domainMetadata;
    }

    /**
     * 将需要添加的元素组装好，根据startLine从大到小 依次添加到相应的位置，写入文件
     * @param domainMetadata
     * @param baseMetadaList
     */
    private DomainMetadata addToFile(DomainMetadata domainMetadata,List<BaseMetada> baseMetadaList){

        //对baseMetadaList进行排序 按照 StartLine从大到小
        Collections.sort(baseMetadaList,(a,b)-> b.getStartLine() - a.getStartLine());

        //从domain中copy一份内容
        List<String> resultContentList = DomainUtils.copyModifiableList(domainMetadata.getContentList());

        for(BaseMetada baseMetada:baseMetadaList){

            //如果不重复则添加
            if(!duplicateBaseMetada(domainMetadata,baseMetada)){
                String baseMetadaContent = baseMetada.getFullContent();
                //设置空行
                for(int i=0;i<baseMetada.getFrontBlankLineCount();i++){
                    if(baseMetada.getSortNo()!=-1){
                        baseMetadaContent = baseMetadaContent+"\n";
                    }else{
                        baseMetadaContent = "\n"+baseMetadaContent;
                    }
                }
                List<String> contentList = Arrays.stream(baseMetadaContent.split("\n",-1)).collect(Collectors.toList());

                resultContentList.addAll(baseMetada.getStartLine(),contentList);
            }
        }
        String resultContent = DomainUtils.joinStringList(resultContentList);
        try {
            DomainFileUtils.writeFileContent(domainMetadata.getLocalPath(),resultContent);
            domainMetadata.setContent(resultContent);
            domainMetadata.setContentList(Collections.unmodifiableList(resultContentList));
        }catch (IOException e) {
            throw new DomainServiceException("写入文件【"+domainMetadata.getLocalPath()+"】出错",e);
        }
        return domainMetadata;
    }

    /**
     * 添加前的处理
     * 1、对插入点进行定位 2、调整代码的前置空格数，保持和相邻元素一致
     * @param metadaList 同类元素列表
     * @param addBaseMetada 需要添加的元素
     */
    private void handleBeforeAdd(List<? extends BaseMetada> metadaList,BaseMetada addBaseMetada){
        //排序位置
        int sortNo = addBaseMetada.getSortNo();
        int defaultInsertIndex = addBaseMetada.getStartLine();
        int defaultFrontBlankCount = addBaseMetada.getFrontBlankCount();

        //定位相邻的相同元素，获得插入行数和前置空白个数
        if(!CollectionUtils.isEmpty(metadaList)){
            if(sortNo>metadaList.size()-1){
                sortNo = -1;
                addBaseMetada.setSortNo(-1);
            }

            BaseMetada adjoinBaseMetadata; //相邻的同类元素

            if(sortNo>=0){
                adjoinBaseMetadata = metadaList.get(sortNo);
                defaultInsertIndex = adjoinBaseMetadata.getStartLine();
            }else{
                adjoinBaseMetadata = metadaList.get(metadaList.size()-1);
                defaultInsertIndex = adjoinBaseMetadata.getEndLine()+1;
            }
            defaultFrontBlankCount = adjoinBaseMetadata.getFrontBlankCount();
        }else{
            //如果没有相邻的相同元素，则修改自身排序为-1
            addBaseMetada.setSortNo(-1);
        }
        addBaseMetada.setStartLine(defaultInsertIndex);
        addBaseMetada.setFrontBlankCount(defaultFrontBlankCount);
        //根据前置空格数，对整体内容进行调整
        String content = ContentUtils.trimFrontBlankContent(addBaseMetada.getContent(),defaultFrontBlankCount);

        addBaseMetada.setContent(content);
    }

}
