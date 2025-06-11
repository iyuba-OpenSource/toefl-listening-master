package com.iyuba.toelflistening.java.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EvalBean {


    @SerializedName("result")
    private String result;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private DataDTO data;

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

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("sentence")
        private String sentence;
        @SerializedName("words")
        private List<WordsDTO> words;
        @SerializedName("scores")
        private int scores;
        @SerializedName("total_score")
        private double totalScore;
        @SerializedName("filepath")
        private String filepath;
        @SerializedName("URL")
        private String url;

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }

        public List<WordsDTO> getWords() {
            return words;
        }

        public void setWords(List<WordsDTO> words) {
            this.words = words;
        }

        public int getScores() {
            return scores;
        }

        public void setScores(int scores) {
            this.scores = scores;
        }

        public double getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(double totalScore) {
            this.totalScore = totalScore;
        }

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public static class WordsDTO {
            @SerializedName("index")
            private String index;
            @SerializedName("content")
            private String content;
            @SerializedName("pron")
            private String pron;
            @SerializedName("pron2")
            private String pron2;
            @SerializedName("user_pron")
            private String userPron;
            @SerializedName("user_pron2")
            private String userPron2;
            @SerializedName("score")
            private String score;
            @SerializedName("insert")
            private String insert;
            @SerializedName("delete")
            private String delete;
            @SerializedName("substitute_orgi")
            private String substituteOrgi;
            @SerializedName("substitute_user")
            private String substituteUser;

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getPron() {
                return pron;
            }

            public void setPron(String pron) {
                this.pron = pron;
            }

            public String getPron2() {
                return pron2;
            }

            public void setPron2(String pron2) {
                this.pron2 = pron2;
            }

            public String getUserPron() {
                return userPron;
            }

            public void setUserPron(String userPron) {
                this.userPron = userPron;
            }

            public String getUserPron2() {
                return userPron2;
            }

            public void setUserPron2(String userPron2) {
                this.userPron2 = userPron2;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getInsert() {
                return insert;
            }

            public void setInsert(String insert) {
                this.insert = insert;
            }

            public String getDelete() {
                return delete;
            }

            public void setDelete(String delete) {
                this.delete = delete;
            }

            public String getSubstituteOrgi() {
                return substituteOrgi;
            }

            public void setSubstituteOrgi(String substituteOrgi) {
                this.substituteOrgi = substituteOrgi;
            }

            public String getSubstituteUser() {
                return substituteUser;
            }

            public void setSubstituteUser(String substituteUser) {
                this.substituteUser = substituteUser;
            }
        }
    }
}
