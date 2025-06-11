package com.iyuba.toelflistening.java.actiity.break_through;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.iyuba.toelflistening.AppClient;
import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.activity.LoginActivity;
import com.iyuba.toelflistening.databinding.ActivityWordDetailsBinding;
import com.iyuba.toelflistening.java.Constant;
import com.iyuba.toelflistening.java.actiity.BaseActivity;
import com.iyuba.toelflistening.java.db.Word;
import com.iyuba.toelflistening.java.model.bean.EvalBean;
import com.iyuba.toelflistening.java.presenter.break_through.WordDetailsPresenter;
import com.iyuba.toelflistening.java.view.WordDetailsContract;
import com.iyuba.toelflistening.utils.GlobalHome;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 单词详情
 */
public class WordDetailsActivity extends BaseActivity<WordDetailsContract.WordDetailsView, WordDetailsContract.WordDetailsPresenter>
        implements WordDetailsContract.WordDetailsView {

    private String TAG = "WordDetailsActivity";

    private ActivityWordDetailsBinding activityWordDetailsBinding;

    /**
     * 从上一个页面接收的单词列表
     */
    private List<Word> dataDTOList;
    //bundle 单词在列表中的位置
    private int position = 0;

    private MediaPlayer player;

    /**
     * 是否显示句子
     */
    private boolean isShowSentence = false;

    /**
     * 听原音的动画
     */
    private AnimationDrawable animation_original;

    /**
     * 上面喇叭的动画
     */
    private AnimationDrawable animation_top_original;

    /**
     * 听跟读的动画
     */
    private AnimationDrawable animation_follow;

    /**
     * 播放的是哪个音频，动画就作用在哪个控件上
     * 1：上面的喇叭
     * 2：听原音
     * 3: 听跟读
     * 0：自动播放单词音频
     */
    private int flag_play = 0;

    /**
     * 动画set1
     */
    private AnimationSet set1;

    private File saveFile;

    private RxPermissions rxPermissions;

    /**
     * 存储权限是否被授权
     */
    private SharedPreferences sp;

    private MediaRecorder mediaRecorder;
    /**
     * 是否正在录制声音
     */
    private boolean isRecord = false;

    /**
     * 是否自动播放语音
     */
    private boolean isAutoPlay = false;

    /**
     * 获取音频焦点
     */
    private AudioManager audioManager;

    /**
     * android8.0以上的音频焦点
     */
    private AudioFocusRequest audioFocusRequest;


    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    //当其他应用申请焦点之后又释放焦点会触发此回调
                    //可重新播放音乐
                    Log.d(TAG, "AUDIOFOCUS_GAIN");
//                        start();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    //长时间丢失焦点,当其他应用申请的焦点为 AUDIOFOCUS_GAIN 时，
                    //会触发此回调事件，例如播放 QQ 音乐，网易云音乐等
                    //通常需要暂停音乐播放，若没有暂停播放就会出现和其他音乐同时输出声音
                    Log.d(TAG, "AUDIOFOCUS_LOSS");
//                        stop();
                    //释放焦点，该方法可根据需要来决定是否调用
                    //若焦点释放掉之后，将不会再自动获得
//                        mAudioManager.abandonAudioFocus(mAudioFocusChange);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    //短暂性丢失焦点，当其他应用申请 AUDIOFOCUS_GAIN_TRANSIENT 或AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE 时，
                    //会触发此回调事件，例如播放短视频，拨打电话等。
                    //通常需要暂停音乐播放
                    if (player != null) {

                        player.pause();
                    }
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //短暂性丢失焦点并作降音处理
                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        getBundle();
        initMediaPlayer();
        initOperation();
        showWord();
        initAnim1();
    }

    /**
     * 初始化动画
     */
    private void initAnim1() {

        set1 = new AnimationSet(true);
        //缩放动画，以中心从原始放大到1.3倍
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        //渐变动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);
        scaleAnimation.setDuration(800);
        scaleAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        set1.setDuration(800);
        set1.addAnimation(scaleAnimation);
        set1.addAnimation(alphaAnimation);
    }

    /**
     * 初始化音频播放器
     */
    private void initMediaPlayer() {

        player = new MediaPlayer();
        player.setLooping(false);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                stopAnimation();
                player.start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    audioFocusRequest
                            = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).build())
                            .setOnAudioFocusChangeListener(onAudioFocusChangeListener)
                            .build();
                    audioManager.requestAudioFocus(audioFocusRequest);
                } else {
                    audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                }


                if (flag_play == 1) {

                    animation_top_original = (AnimationDrawable) activityWordDetailsBinding.wdIvAudio.getDrawable();
                    animation_top_original.start();
                } else if (flag_play == 2) {

                    animation_original = (AnimationDrawable) activityWordDetailsBinding.wdIvListen.getDrawable();
                    animation_original.start();
                } else if (flag_play == 3) {

                    animation_follow = (AnimationDrawable) activityWordDetailsBinding.wdIvFollow.getDrawable();
                    animation_follow.start();
                }
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    audioManager.requestAudioFocus(audioFocusRequest);
                } else {

                    audioManager.abandonAudioFocus(onAudioFocusChangeListener);
                }

                stopAnimation();
            }
        });
    }

    /**
     * 停止动画
     */
    private void stopAnimation() {

        if (animation_original != null) {
            animation_original.selectDrawable(0);
            animation_original.stop();
        }
        if (animation_top_original != null) {
            animation_top_original.selectDrawable(0);
            animation_top_original.stop();
        }
        if (animation_follow != null) {

            animation_follow.selectDrawable(0);
            animation_follow.stop();
        }
    }

    /**
     * 播放音频文件
     *
     * @param urlStr 地址
     */
    private void play(String urlStr) {

//        EventBus.getDefault().post(new MediaPauseEventbus());
        try {
            player.reset();
            player.setDataSource(urlStr);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化点击事件
     */
    private void initOperation() {


        sp = getSharedPreferences(Constant.SP_PERMISSION, MODE_PRIVATE);
        mediaRecorder = new MediaRecorder();

        activityWordDetailsBinding.toolbar.toolbarSwitchRight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    isAutoPlay = true;
                } else {

                    isAutoPlay = false;
                }
            }
        });

        activityWordDetailsBinding.toolbar.toolbarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        activityWordDetailsBinding.toolbar.toolbarIvTitle.setText("单词详情");
        //上面的喇叭
        activityWordDetailsBinding.wdIvAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dataDTOList.size() == 0) {

                    return;
                }
                flag_play = 1;
                Word dataDTO = dataDTOList.get(position);

                String urlStr = dataDTO.getWordAudio();

                play(urlStr);
            }
        });
        //切换单词和句子
        activityWordDetailsBinding.wdIvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dataDTOList.size() == 0) {

                    return;
                }
                Word dataDTO = dataDTOList.get(position);
//                if (isShowSentence) {//

                activityWordDetailsBinding.wdTvWord.setText(dataDTO.getWord());
                activityWordDetailsBinding.wdTvWordchBottom.setText(dataDTO.getDef());
                isShowSentence = false;
//                } else {
//
//                    activityWordDetailsBinding.wdTvWord.setText(dataDTO.getSentence());
//                    activityWordDetailsBinding.wdTvWordchBottom.setText(dataDTO.getSentenceCh());
//                    isShowSentence = true;
//                }
            }
        });
        //上一个
        activityWordDetailsBinding.wdButPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dataDTOList.size() == 0) {

                    return;
                }
                position--;
                if (position < 0) {

                    position = 0;
                    return;
                }
                flag_play = 0;
                showWord();
            }
        });
        //下一个
        activityWordDetailsBinding.wdButNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dataDTOList.size() == 0) {

                    return;
                }
                position++;
                if (position >= dataDTOList.size()) {
                    position = dataDTOList.size() - 1;
                    return;
                }
                flag_play = 0;//自动播放时不触发动画
                showWord();
            }
        });
        //听原音
        activityWordDetailsBinding.wdLlListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dataDTOList.size() == 0) {

                    return;
                }

                flag_play = 2;
                Word dataDTO = dataDTOList.get(position);
//                if (isShowSentence) {//显示句子

                String uriStr = dataDTO.getWordAudio();
                play(uriStr);

//                } else {
//
//                    String urlStr;
//                    if (dataDTO.getSound().startsWith("http")) {
//
//                        urlStr = dataDTO.getSound();
//                    } else {
//
//                        urlStr = Constant.URL_STATIC2 + "/Japan/" + dataDTO.getSource() + "/word/" + dataDTO.getSound().split("_")[0]
//                                + "/"
//                                + dataDTO.getSound() + ".mp3";
//                    }
//                    play(urlStr);
//                }
            }
        });
        //点击评测
        activityWordDetailsBinding.wdLlTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestRecord();
            }
        });
        //听跟读
        activityWordDetailsBinding.wdLlFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Word dataDTO = dataDTOList.get(position);
                if (dataDTO.getUrl() == null) {

                    return;
                }

                flag_play = 3;
                String uriStr = Constant.USERSPEECH_URL + "/voa/" + dataDTO.getUrl();
                play(uriStr);
            }
        });
        //收藏
        activityWordDetailsBinding.wdIbCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                collect();
            }
        });
    }

    /**
     * 收藏功能
     */
    private void collect() {

        if (!GlobalHome.Companion.getUserInfo().isSuccess()) {

            startActivity(new Intent(WordDetailsActivity.this, LoginActivity.class));
            toast("请登录");
            return;
        }
        Word word = dataDTOList.get(position);
        if (word.getCollect() == 1) {

            presenter.updateWord(Constant.UPDATE_WORD, "Iyuba", "delete", word.getWord(), GlobalHome.Companion.getUid() + "",
                    "json", word.getRowid() + "");
        } else {

            presenter.updateWord(Constant.UPDATE_WORD, "Iyuba", "insert", word.getWord(), GlobalHome.Companion.getUid() + "",
                    "json", word.getRowid() + "");
        }
    }


    private void getBundle() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            dataDTOList = (List<Word>) bundle.getSerializable("WORD_LIST");
            position = bundle.getInt("POSITION", 0);
        }
    }

    /**
     * @param activity
     * @param dataDTOList 单词列表
     */
    public static void startActivity(Activity activity, List<Word> dataDTOList, int position) {

        Intent intent = new Intent(activity, WordDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("WORD_LIST", (Serializable) dataDTOList);
        bundle.putInt("POSITION", position);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }


    @Override
    public View initLayout() {
        activityWordDetailsBinding = ActivityWordDetailsBinding.inflate(getLayoutInflater());
        return activityWordDetailsBinding.getRoot();
    }

    @Override
    public WordDetailsContract.WordDetailsPresenter initPresenter() {
        return new WordDetailsPresenter();
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(WordDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 请求权限
     */
    @SuppressLint("CheckResult")
    private void requestRecord() {

        if (rxPermissions == null) {
            rxPermissions = new RxPermissions(WordDetailsActivity.this);
        }
        if (rxPermissions.isGranted(Manifest.permission.RECORD_AUDIO)) {//授权了

            recordAudio();
        } else {
            int state = sp.getInt(Constant.SP_KEY_PERMISSION_RECORD, 0);
            if (state == 0) {

                new AlertDialog.Builder(WordDetailsActivity.this)
                        .setTitle("权限说明")
                        .setMessage("录音权限：评测需要录音权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                rxPermissions.request(Manifest.permission.RECORD_AUDIO)
                                        .subscribe(new Consumer<Boolean>() {
                                            @Override
                                            public void accept(Boolean aBoolean) throws Exception {

                                                if (aBoolean) {

                                                    recordAudio();
                                                } else {

                                                    sp.edit().putInt(Constant.SP_KEY_PERMISSION_RECORD, 1).apply();
                                                    toast("你禁止了录音权限");
                                                }
                                            }
                                        });
                            }
                        })
                        .show();
            } else {

                toast("你禁止了录音权限，请在权限管理中打开");
            }
        }
    }


    /**
     * 录制音频
     */
    private void recordAudio() {

//        EventBus.getDefault().post(new MediaPauseEventbus());

        if (isRecord) {

            isRecord = false;
            mediaRecorder.stop();
            activityWordDetailsBinding.wdIvTestAnim.clearAnimation();

            Word dataDTO = dataDTOList.get(position);

            String uid = GlobalHome.Companion.getUid() + "";
            if (GlobalHome.Companion.getUserInfo().isEmpty()) {

                uid = "0";
            } else {

                uid = GlobalHome.Companion.getUid() + "";
            }


            MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
            RequestBody fileBody = RequestBody.create(type, saveFile);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("type", AppClient.appName)
                    // .addFormDataPart("userId", "0")
                    .addFormDataPart("userId", uid)
                    .addFormDataPart("newsId", dataDTO.getRowid() + "")
                    .addFormDataPart("paraId", "0")
                    .addFormDataPart("IdIndex", "0")
                    .addFormDataPart("sentence", dataDTO.getWord())
                    .addFormDataPart("file", saveFile.getName(), fileBody)
                    .addFormDataPart("wordId", "0")
                    .addFormDataPart("flg", "2")
                    .addFormDataPart("appId", Constant.APPID + "")
                    .build();

            presenter.eval(requestBody);
        } else {

            isRecord = true;
            activityWordDetailsBinding.wdIvTestAnim.startAnimation(set1);
            //获取音乐目录
            saveFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), System.currentTimeMillis() + ".mp3");
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            mediaRecorder.reset();
            mediaRecorder.setOutputFile(saveFile.getAbsolutePath());
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 从收藏进入此页面不成立
     *
     * @return
     */
    private String getType(int level) {

    /*    String newsId;
        if (isShowSentence) {
            //短句
            newsId = Constant.bookDataDTO.getName() + "ShortSentence";
        } else {

            if (level < 6) {

                newsId = "n" + Constant.bookDataDTO.getLevel();
            } else {

                newsId = Constant.bookDataDTO.getName() + "word";
            }

        }
        return newsId;*/
        return "";
    }

    /**
     * 显示单词
     */
    public void showWord() {

        if (dataDTOList.size() == 0) {

            return;
        }
        Word dataDTO = dataDTOList.get(position);

        //获取最新数据，收藏标志会变化
        List<Word> jpWordList = LitePal.where("rowid = ?", dataDTO.getRowid() + "").find(Word.class);
        if (jpWordList.size() != 0) {

            jpWordList.get(0).setUrl(dataDTO.getUrl());
            jpWordList.get(0).setTotal_score(dataDTO.getTotal_score());

            dataDTO = jpWordList.get(0);
            dataDTOList.set(position, jpWordList.get(0));
        }

        //top上班部分  日语单词
        activityWordDetailsBinding.wdTvWordTop.setText(dataDTO.getWord());
        //pron
        if (dataDTO.getPron() == null) {

            activityWordDetailsBinding.wdTvPron.setText("");
        } else {
            activityWordDetailsBinding.wdTvPron.setText("[" + dataDTO.getPron() + "]");
        }
        //中文解释
//        if (dataDTO.getSpeech() == null) {//词性是否为空

        activityWordDetailsBinding.wdTvWordch.setText(dataDTO.getDef());
//        } else {
//
//            activityWordDetailsBinding.wdTvWordch.setText("【" + dataDTO.getSpeech() + "】" + " " + dataDTO.getWordCh());
//        }
        //是否收藏
        if (dataDTO.getCollect() == 1) {

            activityWordDetailsBinding.wdIbCollect.setImageResource(R.mipmap.ic_collected);
        } else {

            activityWordDetailsBinding.wdIbCollect.setImageResource(R.mipmap.ic_uncollect);
        }

        //下半部分  日语单词或者句子
//        if (isShowSentence) {//显示句子
//
//            activityWordDetailsBinding.wdTvWord.setText(dataDTO.getSentence());
//            //中文句子或者中文单词
//            activityWordDetailsBinding.wdTvWordchBottom.setText(dataDTO.getSentenceCh());
//        } else {//显示单词
        activityWordDetailsBinding.wdTvWord.setText(dataDTO.getWord());
        //中文句子或者中文单词
        activityWordDetailsBinding.wdTvWordchBottom.setText(dataDTO.getDef());
//        }
        //单词下标
        activityWordDetailsBinding.wdTvIndex.setText((position + 1) + "/" + dataDTOList.size());
        //是否自动播放
        if (isAutoPlay) {

            String urlStr = dataDTO.getWordAudio();
            play(urlStr);
        }
        //评测前
        if (dataDTO.getUrl() == null) {

            activityWordDetailsBinding.wdLlFollow.setVisibility(View.INVISIBLE);
            activityWordDetailsBinding.wdIvScore.setVisibility(View.INVISIBLE);
            activityWordDetailsBinding.wdLlScore.setVisibility(View.INVISIBLE);
            activityWordDetailsBinding.wdTvLowscore.setVisibility(View.INVISIBLE);

        } else {//评测后

            activityWordDetailsBinding.wdLlFollow.setVisibility(View.VISIBLE);

            double scoreD = Double.parseDouble(dataDTO.getTotal_score()) * 20.0;
            if (scoreD < 60) {//低于60分

                activityWordDetailsBinding.wdIvScore.setVisibility(View.VISIBLE);
                activityWordDetailsBinding.wdTvLowscore.setVisibility(View.VISIBLE);
                activityWordDetailsBinding.wdLlScore.setVisibility(View.INVISIBLE);
            } else {

                activityWordDetailsBinding.wdIvScore.setVisibility(View.INVISIBLE);
                activityWordDetailsBinding.wdTvLowscore.setVisibility(View.INVISIBLE);
                activityWordDetailsBinding.wdLlScore.setVisibility(View.VISIBLE);
                activityWordDetailsBinding.wdTvScore.setText(scoreD + "");
            }
        }
    }

    @Override
    public void updateCollectComplete(String wordid, String type) {


        Word collectWord = null;
        for (int i = 0; i < dataDTOList.size(); i++) {

            Word word = dataDTOList.get(i);
            if ((word.getRowid() + "").equals(wordid)) {

                collectWord = word;
                break;
            }
        }
        if (collectWord != null) {

            if (type.equals("insert")) {

                collectWord.setCollect(1);
            } else {

                collectWord.setToDefault("collect");
                collectWord.setCollect(0);
            }

            List<Word> jpWordList = LitePal.where("rowid = ?", collectWord.getRowid() + "").find(Word.class);
            if (jpWordList.size() == 0) {//数据库没有此数据

                collectWord.save();
            } else {//有此数据更新数据库

                collectWord.updateAll("rowid = ?", collectWord.getRowid() + "");
            }
        }
        //更新界面
        Word curWord = dataDTOList.get(position);
        if (curWord == collectWord) {

            if (type.equals("insert")) {

                activityWordDetailsBinding.wdIbCollect.setImageResource(R.mipmap.ic_collected);
            } else {

                activityWordDetailsBinding.wdIbCollect.setImageResource(R.mipmap.ic_uncollect);
            }
        }
    }

    @Override
    public void eval(EvalBean evalBean) {

        //记录评测后获得的音频链接及得分
        Word dataDTO = dataDTOList.get(position);
        dataDTO.setUrl(evalBean.getData().getUrl());
        dataDTO.setTotal_score(evalBean.getData().getTotalScore() + "");
        showWord();
    }
}