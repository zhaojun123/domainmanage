package com.zkml.domainmanage.support;

import com.zkml.domainmanage.support.exception.DomainServiceException;
import com.zkml.domainmanage.support.input.*;
import com.zkml.domainmanage.support.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/domainManage/domain")
public class DomainManageController {

    @Autowired
    DomainManageService domainManageService;

    /**
     * domain管理首页
     * @param localPath
     * @param model
     * @return
     */
    @GetMapping("")
    public String index(String localPath,String query,Model model){
        if(StringUtils.isNotBlank(localPath)){
            try {
                domainManageService.initDomainTree(Arrays.asList(localPath.split(",")));
            } catch (DomainServiceException e) {
                //TODO 这里可以做一些日志记录，或者返回错误页面
                e.printStackTrace();
            }
        }
        List<DomainVO> domainList = domainManageService.handleDomainTree(query);
        List<String> localPathList = domainManageService.getlocalPathList();
        model.addAttribute("domainList",domainList);
        model.addAttribute("localPathList",localPathList);
        return "index";
    }

    /**
     * 添加field
     * @param addFieldInput
     * @param bindingResult
     * @return
     * @throws IOException
     */
    @PostMapping("/doAddField")
    @ResponseBody
    public ResultVO doAddField(@Valid AddFieldInput addFieldInput, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResultUtil.failResult(bindingResult.getFieldError().getDefaultMessage());
        }
        try{
            domainManageService.doAddField(addFieldInput);
            return ResultUtil.successResult("添加成功");
        }catch (DomainServiceException e){
            e.printStackTrace();
            return ResultUtil.failResult(e);
        }
    }

    /**
     * 删除field
     * @param deleteFieldInput
     * @param bindingResult
     * @return
     * @throws IOException
     */
    @PostMapping("/doDeleteField")
    @ResponseBody
    public ResultVO doDeleteField(@Valid DeleteFieldInput deleteFieldInput, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResultUtil.failResult(bindingResult.getFieldError().getDefaultMessage());
        }
        try{
            domainManageService.doDeleteField(deleteFieldInput);
            return ResultUtil.successResult("删除成功");
        }catch (DomainServiceException e){
            e.printStackTrace();
            return ResultUtil.failResult(e);
        }
    }

    /**
     * 自定义添加field代码，
     * 有些特殊情况需要自己写一些复杂的代码，例如加一些自定义注解，在get/set里面写一些逻辑等
     * @param addFieldCustomize
     * @param bindingResult
     * @return
     * @throws IOException
     */
    @PostMapping("/doAddFieldCustomize")
    @ResponseBody
    public ResultVO doAddFieldCustomize(@Valid AddFieldCustomizeInput addFieldCustomize, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResultUtil.failResult(bindingResult.getFieldError().getDefaultMessage());
        }
        try{
            domainManageService.doAddFieldCustomize(addFieldCustomize);
            return ResultUtil.successResult("添加成功");
        }catch (DomainServiceException e){
            e.printStackTrace();
            return ResultUtil.failResult(e);
        }
    }

    /**
     * domain详细页
     * @param fileKey
     * @return
     */
    @GetMapping("/domainDetail")
    public String domainDetail(@RequestParam(required = true)String fileKey,Model model){
        DomainVO domainVO = domainManageService.domainDetail(fileKey);
        model.addAttribute("domain",domainVO);
        return "domainDetail";
    }

    /**
     * 调整field的位置
     * @param switchFieldInput
     * @return
     */
    @PostMapping("/doSwitchField")
    @ResponseBody
    public ResultVO doSwitchField(@Valid SwitchFieldInput switchFieldInput,BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResultUtil.failResult(bindingResult.getFieldError().getDefaultMessage());
        }
        domainManageService.doSwitchField(switchFieldInput);
        return ResultUtil.successResult("");
    }

    /**
     * 修改field的Validation注解
     * @param fieldValidationList
     * @param bindingResult
     * @return
     */
    @PostMapping("/doUpdateFieldValidation")
    @ResponseBody
    public ResultVO doUpdateFieldValidation(@Valid @RequestBody List<UpdateFieldValidationInput> fieldValidationList
            , @RequestParam(required = true) String fileKey
            , BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResultUtil.failResult(bindingResult.getFieldError().getDefaultMessage());
        }
        domainManageService.doUpdateFieldValidation(fileKey,fieldValidationList);
        return ResultUtil.successResult("操作成功");
    }

    /**
     * 批量添加Swagger注解
     * @param fileKeys
     * @return
     */
    @PostMapping("/doBatchAddSwagger")
    @ResponseBody
    public ResultVO doBatchAddSwagger(@RequestParam(required = true)String fileKeys){
        domainManageService.doBatchAddSwagger(fileKeys);
        return ResultUtil.successResult("操作成功");
    }

    /**
     * 批量删除swagger注解
     * @param fileKeys
     * @return
     */
    @PostMapping("/doBatchDeleteSwagger")
    @ResponseBody
    public ResultVO doBatchDeleteSwagger(@RequestParam(required = true)String fileKeys){
        domainManageService.doBatchDeleteSwagger(fileKeys);
        return ResultUtil.successResult("操作成功");
    }

    /**
     * field字典
     * @param model
     * @return
     */
    @GetMapping("fieldDictionary")
    public String fieldDictionary(Model model){
        List<FieldDictionaryVO> fieldList = domainManageService.fieldDictionary();
        model.addAttribute("fieldList",fieldList);
        return "fieldDictionary";
    }

    /**
     * domain代码，如果有query查询条件，则匹配查询的行高亮
     * @param fileKey
     * @param query
     * @param model
     * @return
     */
    @GetMapping("code")
    public String code(@RequestParam(required = true)String fileKey,String query,Model model){
        String code = domainManageService.code(fileKey);
        List<Integer> highlight = ContentUtils.queryContentLine(query,code);
        model.addAttribute("highlight",highlight.toString());
        model.addAttribute("code",code);
        return "code";
    }
}
