package com.zkml.domainmanage.support.domain.directoryA;
import java.util.List;
import javax.validation.constraints.NotBlank;

/**
 * Test1
 */
public class Test1 {

    @NotBlank(message = "x不能为空")
    private String x = "1"; //x

    /**
     * a
     */
    @NotBlank(message = "a不能为空")
    private String a;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    /**
     * a
     * @return
     */
    public String getA() {
        return a;
    }

    /**
     * a
     * @param a
     */
    public void setA(String a) {
        this.a = a;
    }





}