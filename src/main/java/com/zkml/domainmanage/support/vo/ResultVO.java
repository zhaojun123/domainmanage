package com.zkml.domainmanage.support.vo;

public class ResultVO<T> {

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";

    private String result = SUCCESS;
    private String message;
    private T model;

    public ResultVO(String result,T model,String message){
        this.result = result;
        this.model = model;
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }
}
