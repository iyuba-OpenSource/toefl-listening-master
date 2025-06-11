package com.iyuba.toelflistening.java.model.bean;

public class ExamRecordBean {

    private int jiFen;//获得积分数
    private String message;//返回信息
    private int result; //1成功 0失败


    public int getJiFen() {
        return jiFen;
    }

    public void setJiFen(int jiFen) {
        this.jiFen = jiFen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
