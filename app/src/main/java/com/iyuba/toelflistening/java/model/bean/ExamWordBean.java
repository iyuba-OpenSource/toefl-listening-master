package com.iyuba.toelflistening.java.model.bean;

import com.google.gson.annotations.SerializedName;
import com.iyuba.toelflistening.java.db.Word;

import java.util.List;

public class ExamWordBean {


    @SerializedName("data")
    private List<Word> data;
    @SerializedName("size")
    private int size;
    @SerializedName("message")
    private String message;

    public List<Word> getData() {
        return data;
    }

    public void setData(List<Word> data) {
        this.data = data;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
