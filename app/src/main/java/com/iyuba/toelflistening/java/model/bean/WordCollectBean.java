package com.iyuba.toelflistening.java.model.bean;

import com.google.gson.annotations.SerializedName;

public class WordCollectBean {

    @SerializedName("result")
    private int result;
    @SerializedName("strWord")
    private String strWord;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getStrWord() {
        return strWord;
    }

    public void setStrWord(String strWord) {
        this.strWord = strWord;
    }
}
