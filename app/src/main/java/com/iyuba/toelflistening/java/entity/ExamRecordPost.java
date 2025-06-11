package com.iyuba.toelflistening.java.entity;


import java.util.List;

/**
 * Created by iyuba on 2018/11/8.
 */

public class ExamRecordPost {
    private String uid;
    private String appId;
    private String lesson;
    private String sign;
    private String format;
    private String DeviceId;
    private int mode;
    private List<TestRecord> testList;
    private List<Score> scoreList;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String DeviceId) {
        this.DeviceId = DeviceId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public List<TestRecord> getTestList() {
        return testList;
    }

    public void setTestList(List<TestRecord> testList) {
        this.testList = testList;
    }

    public List<Score> getScoreList() {
        return scoreList;
    }

    public void setScoreList(List<Score> scoreList) {
        this.scoreList = scoreList;
    }
}
