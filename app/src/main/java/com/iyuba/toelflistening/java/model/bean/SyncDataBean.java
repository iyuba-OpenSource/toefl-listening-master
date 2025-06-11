package com.iyuba.toelflistening.java.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SyncDataBean {


    @SerializedName("result")
    private int result;
    @SerializedName("mode")
    private String mode;
    @SerializedName("totalRight")
    private int totalRight;
    @SerializedName("msg")
    private String msg;
    @SerializedName("uid")
    private int uid;
    @SerializedName("dataWrong")
    private List<DataRightDTO> dataWrong;
    @SerializedName("testMode")
    private String testMode;
    @SerializedName("lesson")
    private String lesson;
    @SerializedName("dataRight")
    private List<DataRightDTO> dataRight;
    @SerializedName("totalWrong")
    private int totalWrong;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getTotalRight() {
        return totalRight;
    }

    public void setTotalRight(int totalRight) {
        this.totalRight = totalRight;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public List<DataRightDTO> getDataWrong() {
        return dataWrong;
    }

    public void setDataWrong(List<DataRightDTO> dataWrong) {
        this.dataWrong = dataWrong;
    }

    public String getTestMode() {
        return testMode;
    }

    public void setTestMode(String testMode) {
        this.testMode = testMode;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public List<DataRightDTO> getDataRight() {
        return dataRight;
    }

    public void setDataRight(List<DataRightDTO> dataRight) {
        this.dataRight = dataRight;
    }

    public int getTotalWrong() {
        return totalWrong;
    }

    public void setTotalWrong(int totalWrong) {
        this.totalWrong = totalWrong;
    }


    public static class DataRightDTO {
        @SerializedName("score")
        private int score;
        @SerializedName("userAnswer")
        private String userAnswer;
        @SerializedName("testMode")
        private String testMode;
        @SerializedName("testTime")
        private String testTime;
        @SerializedName("id")
        private int id;
        @SerializedName("lessonName")
        private String lessonName;

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }

        public String getTestMode() {
            return testMode;
        }

        public void setTestMode(String testMode) {
            this.testMode = testMode;
        }

        public String getTestTime() {
            return testTime;
        }

        public void setTestTime(String testTime) {
            this.testTime = testTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLessonName() {
            return lessonName;
        }

        public void setLessonName(String lessonName) {
            this.lessonName = lessonName;
        }
    }
}
