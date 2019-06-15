package com.zkml.domainmanage.support;

import com.zkml.domainmanage.support.exception.DomainServiceException;
import com.zkml.domainmanage.support.input.PropertiesInput;
import com.zkml.domainmanage.support.vo.PropertyVO;
import com.zkml.domainmanage.support.vo.ResultUtil;
import com.zkml.domainmanage.support.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.validation.Valid;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping("/domainManage/system")
public class SystemController {

    @Autowired
    SystemService systemService;


    @GetMapping("importList")
    public String importList(String query,Model model){
        List<PropertyVO> propertyList = systemService.importList(query);
        model.addAttribute("importList",propertyList);
        return "importList";
    }

    @PostMapping("doAddImport")
    @ResponseBody
    public ResultVO doAddImport(@Valid PropertiesInput propertiesInput,BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResultUtil.failResult(bindingResult.getFieldError().getDefaultMessage());
        }
        systemService.doAddImport(propertiesInput.getName(),propertiesInput.getValue());
        return ResultUtil.successResult("");
    }

    @PostMapping("doDeleteImport")
    @ResponseBody
    public ResultVO doDeleteImport(@RequestParam(required = true) String name){
        systemService.doDeleteImport(name);
        return ResultUtil.successResult("删除成功");
    }

    @GetMapping("exampleList")
    public String exampleList(String query,Model model){
        List<PropertyVO> propertyList = systemService.exampleList(query);
        model.addAttribute("exampleList",propertyList);
        return "exampleList";
    }

    @PostMapping("doAddExample")
    @ResponseBody
    public ResultVO doAddExample(@Valid PropertiesInput propertiesInput,BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            //如果参数不正确，忽略请求
            return ResultUtil.successResult("");
        }
        systemService.doAddExample(propertiesInput.getName(),propertiesInput.getValue(),propertiesInput.getComment());
        return ResultUtil.successResult("");
    }

    @PostMapping("doDeleteExample")
    @ResponseBody
    public ResultVO doDeleteExample(@RequestParam(required = true) String name){
        systemService.doDeleteExample(name);
        return ResultUtil.successResult("");
    }

    @PostMapping("doImportExample")
    public String doImportExample(MultipartHttpServletRequest request){
        MultipartFile file = request.getFile("file");
        //如果是properties文件，则导入
        if(file.getOriginalFilename().endsWith("properties")){
            systemService.doImportExample(file);
        }
        return "redirect:/domainManage/system/exampleList";
    }

    @PostMapping("doLoadExampleNames")
    @ResponseBody
    public ResultVO doLoadExampleNames(){
        try{
            systemService.doLoadExampleNames();
            return ResultUtil.successResult("操作成功");
        }catch (DomainServiceException e){
            return ResultUtil.failResult(e.getMessage());
        }
    }
}
