package com.iyuba.toelflistening.java.entity;

public class Checkpoint {


    /**
     * 是否通过,或者是否能够进入次关
     */
    private boolean isPass;


    /**
     * 正确的个数
     */
    private int tNum;


    public boolean isPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    public int gettNum() {
        return tNum;
    }

    public void settNum(int tNum) {
        this.tNum = tNum;
    }
}
