package com.allen.demo.utils;

import java.io.Serializable;

public class JsonResult<T> implements Serializable {

    private T data;
    private int err_no;
    private int isSuccess;
    private String err_tips;

    /**
     * 若没有数据返回，默认错误码为0
     */
    public JsonResult(Boolean success) {
        this.isSuccess = success ? 1 : 0;
        this.err_no = success ? 1 : 0;
        this.err_tips = success ? "" : "unknown error";
    }
    public JsonResult(int err_no, String err_tips) {
        this.err_no = err_no;
        this.err_tips = err_tips;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getErr_no() {
        return err_no;
    }

    public void setErr_no(int err_no) {
        this.err_no = err_no;
    }

    public int getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(int isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getErr_tips() {
        return err_tips;
    }

    public void setErr_tips(String err_tips) {
        this.err_tips = err_tips;
    }

    /**
     * 有数据返回时，状态码为0，
     * @param data
     */
    public JsonResult(T data) {
        this.data = data;
        this.err_no = 0;
        this.err_tips = "";
    }
}