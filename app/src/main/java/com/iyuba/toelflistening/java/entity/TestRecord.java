package com.iyuba.toelflistening.java.entity;


public class TestRecord {
    public String uid;
    public String BeginTime;//测试的开始时间
    public int TitleNum;     //题号，序号
    public String UserAnswer;    //用户答案
    public String RightAnswer;    //正确答案
    public int AnswerResut;    //正确与否：0错误；1：正确
    public String TestTime;    //测试时间
    public int IsUpload;
    public int LessonId; // 本地单词库中保存的 单词的id
    public String Category; //类型
    public int TestId;
    public String TestMode; //W: 词汇 G：语法 L：听力 S：口语 R：阅读 X：写作
    public String AppName;
}
