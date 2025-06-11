package com.iyuba.toelflistening.java.entity;

import com.iyuba.toelflistening.java.db.Word;

import java.util.List;

public class WordQuestion {

    private Word word;

    private List<Word> answerList;

    /**
     * 设置题目的语言
     * type
     * 0：中文
     * 1：日语
     */
    private int type;

    /**
     * 选择位置
     */
    private int choosePosition = -1;

    /**
     * 正确的位置
     */
    private int tPosition;

    /**
     * 考试时间
     */
    private String beginTime;

    /**
     * 测试时间，答题时间
     */
    private String testTime;


    public int gettPosition() {
        return tPosition;
    }

    public void settPosition(int tPosition) {
        this.tPosition = tPosition;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public int getChoosePosition() {
        return choosePosition;
    }

    public void setChoosePosition(int choosePosition) {
        this.choosePosition = choosePosition;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Word getWord() {
        return word;
    }

    public void setWord(Word word) {
        this.word = word;
    }

    public List<Word> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Word> answerList) {
        this.answerList = answerList;
    }
}
