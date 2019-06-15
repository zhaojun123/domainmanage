import com.zkml.domainmanage.support.Application;
import com.zkml.domainmanage.support.ClassLoaderUtils;
import com.zkml.domainmanage.support.DomainContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@ContextConfiguration(classes = Application.class)
public class DomainContextTest {

    @Autowired
    DomainContext domainContext;

    /*@Test
    public void initDomainMetadataTree() throws IOException {
       //domainContext.initDomainMetadataTree("/Users/zhaojun/dianzishu/springCloud/ms_domainmanage_support/src/main/java/com/zkml/domainmanage/support/domain");
       //System.out.println(domainContext.getDomainTreeCache());
        //System.out.println(ClassLoaderUtils.getContent("file:/Users/zhaojun/dianzishu/springCloud/ms_domainmanage_support/src/main/resources/templates/domainmanage/index.html"));

        Pattern leftPattern = Pattern.compile("<([^<>])*>");
        Matcher leftMatcher = leftPattern.matcher("List<1,Map<2,Map<4,List<5>>>,3>");
        while(leftMatcher.find()){

        }
    }*/

    public static Pattern pattern = Pattern.compile("<([^<>])*>");

    public static void main(String[] args){
        List<String> fieldTypeList = analysisGenerics("List<1,Map<2,Map<4,List<5>>>,3>",null);
        System.out.println(fieldTypeList);
    }

    private static List<String> analysisGenerics(String fieldType,List<String> list){
        if(list == null){
            list = new ArrayList<>();
        }
        //如果field类型不包含< 则直接添加返回
        if(fieldType.indexOf("<")<0){
            if(!list.contains(fieldType))
                list.add(fieldType);
            return list;
        }
        Matcher matcher = pattern.matcher(fieldType);
        while(matcher.find()){
            String[] childFieldTypes = matcher.group()
                    .replace("<","").replace(">","").split(",");
            for(String childFieldType:childFieldTypes){
                if(!list.contains(childFieldType))
                    list.add(childFieldType);
            }
            fieldType = matcher.replaceFirst("");

        }
        analysisGenerics(fieldType,list);
        return list;
    }
}
