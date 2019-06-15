package com.zkml.domainmanage.support.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zkml.domainmanage.support.DomainManageService;
import com.zkml.domainmanage.support.vo.ResultUtil;
import com.zkml.domainmanage.support.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * domain过滤器，当监测domain路径有没有载入
 */
public class DomainFilter implements Filter{

    DomainManageService domainManageService;

    public DomainFilter(){

    }

    public DomainFilter(DomainManageService domainManageService){
        this.domainManageService = domainManageService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String url = ((HttpServletRequest)servletRequest).getRequestURI();

        if(!url.endsWith("/domainManage/domain") && !domainManageService.checkDomainLoad()){
            ResultVO resultVO = ResultUtil.failResult("domain目录没有载入！");
            ObjectMapper mapper = new ObjectMapper();

            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.setContentType("application/json; charset=utf-8");
            PrintWriter out = null;
            try {
                out = servletResponse.getWriter();
                out.append(mapper.writeValueAsString(resultVO));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }
}
