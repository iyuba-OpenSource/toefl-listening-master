package com.iyuba.toelflistening.java.actiity.break_through;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iyuba.module.toolbox.MD5;
import com.iyuba.toelflistening.AppClient;
import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.activity.MainActivity;
import com.iyuba.toelflistening.databinding.ActivityWordAnswerBinding;
import com.iyuba.toelflistening.java.Constant;
import com.iyuba.toelflistening.java.actiity.BaseActivity;
import com.iyuba.toelflistening.java.adapter.break_through.AnswerAdapter;
import com.iyuba.toelflistening.java.db.Word;
import com.iyuba.toelflistening.java.entity.ExamRecordPost;
import com.iyuba.toelflistening.java.entity.Score;
import com.iyuba.toelflistening.java.entity.TestRecord;
import com.iyuba.toelflistening.java.entity.WordQuestion;
import com.iyuba.toelflistening.java.model.bean.ExamRecordBean;
import com.iyuba.toelflistening.java.popup.AnalysisPopup;
import com.iyuba.toelflistening.java.popup.BTFailPopup;
import com.iyuba.toelflistening.java.popup.BTSuccessPopup;
import com.iyuba.toelflistening.java.popup.WordTipPopup;
import com.iyuba.toelflistening.java.presenter.break_through.WordAnswerPresenter;
import com.iyuba.toelflistening.java.view.WordAnswerContract;
import com.iyuba.toelflistening.utils.GlobalHome;

import org.litepal.LitePal;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 单词作答页面
 */
public class WordAnswerActivity extends BaseActivity<WordAnswerContract.WordAnswerView, WordAnswerContract.WordAnswerPresenter>
        implements WordAnswerContract.WordAnswerView {

    private ActivityWordAnswerBinding activityWordAnswerBinding;

    private List<Word> jpWordList;

    private List<WordQuestion> wordQuestions;

    private Random random;
    private int position;

    private AnswerAdapter answerAdapter;

    private AnalysisPopup analysisPopup;

    /**
     * 闯关成功
     */
    private BTSuccessPopup btSuccessPopup;

    /**
     * 尚未闯关成功
     */
    private WordTipPopup wordTipPopup;

    /**
     * 闯关失败
     */
    private BTFailPopup btFailPopup;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = 0;
        random = new Random();
        decimalFormat = new DecimalFormat("##.0");
        getBundle();
        initData();
        initOperation();
        showData();
    }

    @Override
    public View initLayout() {

        activityWordAnswerBinding = ActivityWordAnswerBinding.inflate(getLayoutInflater());
        return activityWordAnswerBinding.getRoot();
    }

    @Override
    public WordAnswerContract.WordAnswerPresenter initPresenter() {
        return new WordAnswerPresenter();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            initWordTipPopup();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initOperation() {

        activityWordAnswerBinding.toolbar.toolbarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initWordTipPopup();
            }
        });
        activityWordAnswerBinding.toolbar.toolbarIvTitle.setText((position + 1) + "/" + jpWordList.size());

        activityWordAnswerBinding.waRvAnswers.setLayoutManager(new LinearLayoutManager(this));
        answerAdapter = new AnswerAdapter(R.layout.item_answer, new ArrayList<>());
        activityWordAnswerBinding.waRvAnswers.setAdapter(answerAdapter);
        answerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int listPosition) {

                //选择了就不能再选了
                if (answerAdapter.getChoosePosition() != -1 && answerAdapter.gettPosition() != -1) {

                    return;
                }

                WordQuestion wordQuestion = wordQuestions.get(position);
                wordQuestion.setChoosePosition(listPosition);//记录选择的位置
                wordQuestion.setTestTime(getCurrentTime());//记录作答的时间
                answerAdapter.setChoosePosition(listPosition);//适配器记录选择的位置
                answerAdapter.settPosition(wordQuestion.gettPosition());//适配器记录正确答案的位置
                answerAdapter.notifyDataSetChanged();

                //显示下一个按钮
                activityWordAnswerBinding.waLlButton.setVisibility(View.VISIBLE);
                //在数据库中记录
                Word word = wordQuestion.getWord();
                if (listPosition == wordQuestion.gettPosition()) {//选中的是正确的

                    word.setAnswer_status(1);
                } else {

                    word.setAnswer_status(2);
                }
                word.updateAll("rowid = ?", word.getRowid() + "");//更新本地数据库
                //检测是否闯关失败，失败则弹窗
                isFail();
            }
        });
        //下一个
        activityWordAnswerBinding.waButNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == (wordQuestions.size() - 1)) {

                    initBTSuccessPopup();
                    return;
                }

                position++;
                //设置答题开始时间
                wordQuestions.get(position).setBeginTime(getCurrentTime());

                activityWordAnswerBinding.toolbar.toolbarIvTitle.setText((position + 1) + "/" + jpWordList.size());
                answerAdapter.settPosition(-1);
                answerAdapter.setChoosePosition(-1);
                showData();
            }
        });
        activityWordAnswerBinding.waButAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initAnalysisPopup(wordQuestions.get(position).getWord());
            }
        });
    }

    /**
     * 检测闯关是否失败
     */
    private void isFail() {

        int totalDoQuestion = 0;//共做多少道题
        int tNUM = 0;//已做题正确数量
        int fNum = 0;//已做题错误数量

        for (int i = 0; i < wordQuestions.size(); i++) {

            WordQuestion wq = wordQuestions.get(i);
            if (wq.getChoosePosition() == -1) {
                break;
            } else {
                totalDoQuestion++;
                if (wq.gettPosition() == wq.getChoosePosition()) {
                    tNUM++;
                } else {
                    fNum++;
                }
            }
        }
        double fPercentage = 100.0 * fNum / wordQuestions.size();//总的错误率
        double questionPercentage = 100.0 * totalDoQuestion / wordQuestions.size();//总做题进度
        if (fPercentage > 20 && questionPercentage >= 20) {

            double tPercentage = 100.0 * tNUM / totalDoQuestion;
            initBTFailPopup("共做：" + totalDoQuestion + "题，做对：" + tNUM + "题，正确比例" + decimalFormat.format(tPercentage) + "%");
        }
    }


    /**
     * 解释、解析弹窗
     *
     * @param word
     */
    private void initAnalysisPopup(Word word) {

        if (analysisPopup == null) {

            analysisPopup = new AnalysisPopup(this);
        }

        analysisPopup.setJpWord(word);
        analysisPopup.showPopupWindow();
    }


    /**
     * 闯关未完成
     */
    private void initWordTipPopup() {

        if (wordTipPopup == null) {

            wordTipPopup = new WordTipPopup(this);
            wordTipPopup.setCallback(new WordTipPopup.Callback() {
                @Override
                public void cancel() {

                    wordTipPopup.dismiss();
                }

                @Override
                public void confirm() {

                    presenter.updateExamRecord(getExamRecordBody());
                    finish();
                }
            });
        }
        wordTipPopup.showPopupWindow();
    }

    /**
     * 闯关失败的弹窗
     */
    private void initBTFailPopup(String msg) {

        if (btFailPopup == null) {

            btFailPopup = new BTFailPopup(WordAnswerActivity.this);
            btFailPopup.setCallback(new BTFailPopup.Callback() {
                @Override
                public void confirm() {

                    finish();
                }
            });
        }
        btFailPopup.setMessage(msg);
        btFailPopup.showPopupWindow();
    }

    private String getCurTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(System.currentTimeMillis());
    }

    public String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String strCurrTime = formatter.format(curDate);
        return strCurrTime;
    }

    /**
     * 获取答题数据
     *
     * @return
     */
    private ExamRecordPost getExamRecordBody() {

        int uid = 0;
        if (GlobalHome.Companion.isLogin()) {

            uid = GlobalHome.Companion.getUid();
        }
        String strMd5 = uid + "" + AppClient.appId + Constant.BREAK_CATEGORY_TYPE + "iyubaExam" + getCurTime();
        String sign = MD5.getMD5ofStr(strMd5);
        ExamRecordPost body = new ExamRecordPost();
        body.setAppId(AppClient.appId + "");
        body.setUid(uid + "");
        body.setLesson(Constant.BREAK_CATEGORY_TYPE);
        body.setSign(sign);
        body.setFormat("json");//返回格式
        body.setDeviceId(android.os.Build.MODEL);
        body.setMode(2); //1测试 , 2学习

        //获取answerList
        List<TestRecord> answerList = new ArrayList<TestRecord>();
        for (int i = 0; i < wordQuestions.size(); i++) {

            WordQuestion wordQuestion = wordQuestions.get(i);

            if (wordQuestion.getChoosePosition() == -1) {//未作答不上传到服务器

                continue;
            }

            TestRecord testRecord = new TestRecord();
            if (wordQuestion.getType() == 0) {//中文

                testRecord.UserAnswer = wordQuestion.getAnswerList().get(wordQuestion.getChoosePosition()).getWord();
                testRecord.RightAnswer = wordQuestion.getWord().getWord();
            } else {

                testRecord.UserAnswer = wordQuestion.getAnswerList().get(wordQuestion.getChoosePosition()).getDef();
                testRecord.RightAnswer = wordQuestion.getWord().getDef();
            }
            testRecord.BeginTime = wordQuestion.getBeginTime();
            testRecord.TestTime = wordQuestion.getTestTime();
            testRecord.TestMode = "W";
            testRecord.Category = "单词闯关";
            testRecord.LessonId = wordQuestion.getWord().getRowid();
            testRecord.TestId = 0;
            testRecord.TitleNum = 0;
            testRecord.IsUpload = 1;
            testRecord.uid = GlobalHome.Companion.isLogin() ? "0" : GlobalHome.Companion.getUid() + "";

            if (wordQuestion.gettPosition() == wordQuestion.getChoosePosition()) {
                testRecord.AnswerResut = 1;
            } else {
                testRecord.AnswerResut = 0;
            }
            answerList.add(testRecord);
        }
        body.setTestList(answerList);

        //正确的个数
        int rightCount = 0;
        for (int i = 0; i < answerList.size(); i++) {
            if (1 == answerList.get(i).AnswerResut) {
                rightCount++;
            }
        }
        body.setTestList(answerList);
        List list = new ArrayList<Score>();
        Score score = new Score();
        score.lessontype = "W"; //单词:W,句子评测:S,听力真题:L
        score.category = "单词闯关"; //
        score.Score = rightCount * 100.0 / wordQuestions.size() + "";
        score.testCnt = wordQuestions.size() + "";
        list.add(score);
        body.setScoreList(list);
        return body;
    }

    private void initData() {

        //找出此level单词最小id与最大id
        List<Word> jps = LitePal.order("rowid").limit(1).find(Word.class);
        int minId = jps.get(0).getRowid();
        List<Word> jpsMax = LitePal.order("rowid  desc").limit(1).find(Word.class);
        int maxId = jpsMax.get(0).getRowid();


        wordQuestions = new ArrayList<>();
        for (int i = 0; i < jpWordList.size(); i++) {

            Word word = jpWordList.get(i);
            WordQuestion wordQuestion = new WordQuestion();
            wordQuestion.setWord(word);
            wordQuestion.setAnswerList(getAnswerData(minId, maxId));
            int tPosition = random.nextInt(4);
            wordQuestion.getAnswerList().set(tPosition, word);
            wordQuestion.settPosition(tPosition);
            wordQuestion.setType((random.nextInt(2) + 1) % 2);
            wordQuestions.add(wordQuestion);
        }
        //设置开始时间
        wordQuestions.get(0).setBeginTime(getCurrentTime());
    }


    private void showData() {

        //隐藏按钮
        activityWordAnswerBinding.waLlButton.setVisibility(View.INVISIBLE);

        WordQuestion wordQuestion = wordQuestions.get(position);
        if (wordQuestion.getType() == 0) {

            activityWordAnswerBinding.waTvQuestion.setText(wordQuestion.getWord().getDef());
        } else {

            activityWordAnswerBinding.waTvQuestion.setText(wordQuestion.getWord().getWord());
        }
        answerAdapter.setType(wordQuestion.getType());
        answerAdapter.setNewData(wordQuestion.getAnswerList());
    }

    /**
     * 创建答题项
     *
     * @param min
     * @param max
     * @return
     */
    private List<Word> getAnswerData(int min, int max) {

        List<Word> answerList = new ArrayList<>();
        do {

            int id = random.nextInt((max + 1) - min) + min;
            List<Word> jpWords = LitePal.where("rowid = ?", id + "").find(Word.class);
            if (jpWords.size() != 0) {//有数据

                Word addWord = jpWords.get(0);
                boolean isAdd = true;//添加不重复的选项
                for (int i = 0; i < answerList.size(); i++) {

                    Word jp = answerList.get(i);
                    if (jp.getRowid() == addWord.getRowid()) {

                        isAdd = false;
                    }
                }
                if (isAdd) {

                    answerList.add(addWord);
                }
            }
        } while (answerList.size() != 4);
        return answerList;
    }

    private void getBundle() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            jpWordList = (List<Word>) bundle.getSerializable("DATAS");
        }
    }

    public static void startActivity(Activity activity, List<Word> jpWordList) {

        Intent intent = new Intent(activity, WordAnswerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("DATAS", (Serializable) jpWordList);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 闯关弹窗
     */
    private void initBTSuccessPopup() {

        if (btSuccessPopup == null) {
            btSuccessPopup = new BTSuccessPopup(this);
            btSuccessPopup.setCallback(new BTSuccessPopup.Callback() {
                @Override
                public void close() {

                    presenter.updateExamRecord(getExamRecordBody());
                    startActivity(new Intent(WordAnswerActivity.this, MainActivity.class));
                }

                @Override
                public void next() {

                    presenter.updateExamRecord(getExamRecordBody());
                    startActivity(new Intent(WordAnswerActivity.this, MainActivity.class));
                }
            });
        }
        btSuccessPopup.showPopupWindow();
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(WordAnswerActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateExamRecordComplete(ExamRecordBean examRecordBean) {

    }
}