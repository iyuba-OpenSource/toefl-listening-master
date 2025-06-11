package com.iyuba.toelflistening.java.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * 单词闯关的单词
 */
public class Word extends LitePalSupport implements Serializable {


    @SerializedName("random")
    private String random;
    @SerializedName("star")
    private int star;
    @SerializedName("def")
    private String def;
    @SerializedName("pron")
    private String pron;
    @SerializedName("sound")
    private int sound;
    @SerializedName("wordAudio")
    private String wordAudio;
    @SerializedName("word")
    private String word;
    @SerializedName("rowid")
    private int rowid;

    /**
     * 答题状态
     * 0：未答题
     * 1：回答正确
     * 2：回答错误
     */
    @Column(defaultValue = "0")
    private int answer_status;


    /**
     * 是否显示中文
     */
    @Column(ignore = true)
    private boolean isShow = false;


    /**
     * 评测后得到的链接
     */
    @Column(ignore = true)
    private String url;
    /**
     * 得分
     */
    @Column(ignore = true)
    private String total_score;


    /**
     * 收藏的标志位
     * 0： 没有收藏
     * 1： 已收藏
     */
    private int collect = 0;


    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTotal_score() {
        return total_score;
    }

    public void setTotal_score(String total_score) {
        this.total_score = total_score;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public int getAnswer_status() {
        return answer_status;
    }

    public void setAnswer_status(int answer_status) {
        this.answer_status = answer_status;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(String pron) {
        this.pron = pron;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public String getWordAudio() {
        return wordAudio;
    }

    public void setWordAudio(String wordAudio) {
        this.wordAudio = wordAudio;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }
}
