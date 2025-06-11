package com.iyuba.toelflistening.java.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iyuba.module.toolbox.MD5;
import com.iyuba.toelflistening.AppClient;
import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.activity.MemberCentreActivity;
import com.iyuba.toelflistening.databinding.FragmentBreakThroughBinding;
import com.iyuba.toelflistening.java.Constant;
import com.iyuba.toelflistening.java.actiity.break_through.BTWordsActivity;
import com.iyuba.toelflistening.java.adapter.BreakThroughAdapter;
import com.iyuba.toelflistening.java.db.Word;
import com.iyuba.toelflistening.java.entity.Checkpoint;
import com.iyuba.toelflistening.java.model.bean.ExamWordBean;
import com.iyuba.toelflistening.java.model.bean.SyncDataBean;
import com.iyuba.toelflistening.java.popup.BTMorePopup;
import com.iyuba.toelflistening.java.popup.WordLoadingPopup;
import com.iyuba.toelflistening.java.presenter.break_through.BreakThroughPresenter;
import com.iyuba.toelflistening.java.util.GridSpacingItemDecoration;
import com.iyuba.toelflistening.java.view.BreakThroughContract;
import com.iyuba.toelflistening.utils.GlobalHome;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 单词闯关
 */
public class BreakThroughFragment extends BaseFragment<BreakThroughContract.BreakThroughView, BreakThroughContract.BreakThroughPresenter>
        implements BreakThroughContract.BreakThroughView {


    private FragmentBreakThroughBinding fragmentBreakThroughBinding;

    private BTMorePopup btMorePopup;

    private WordLoadingPopup loadingPopup;

    private int progress = 0;

    private BreakThroughAdapter breakThroughAdapter;

    private SharedPreferences sp;

    /**
     * 单词数量
     */
    private int wordNum = 30;

    private GridSpacingItemDecoration gridSpacingItemDecoration;


    public BreakThroughFragment() {

    }

    public static BreakThroughFragment newInstance() {
        BreakThroughFragment fragment = new BreakThroughFragment();
        return fragment;
    }

    /**
     * 更新bundle
     */
    public void updateBundle() {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        breakThroughAdapter = new BreakThroughAdapter(R.layout.item_break_through, new ArrayList<>());
        sp = getContext().getSharedPreferences(Constant.SP_BREAK_THROUGH, Context.MODE_PRIVATE);
        wordNum = sp.getInt(Constant.SP_KEY_WORD_NUM, 30);
        breakThroughAdapter.setWordNum(wordNum);
        breakThroughAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (GlobalHome.Companion.isLogin()) {

                    if (!GlobalHome.Companion.isVip() && position != 0) {

                        showBuyVipDialog();
                        return;
                    }
                } else {

                    if (position != 0) {

                        showBuyVipDialog();
                        return;
                    }
                }
                Checkpoint checkpoint = breakThroughAdapter.getItem(position);
                if (checkpoint.isPass()) {

                    BTWordsActivity.startActivity(getActivity(), position, 0);
                } else {

                    toast("此关未解锁");
                }

            }
        });

        gridSpacingItemDecoration = new GridSpacingItemDecoration(3, 40, true);
    }


    /**
     * 是否购买会员
     */
    private void showBuyVipDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setMessage("非VIP用户单词闯关体验闯1关，VIP会员无限单词闯关,是否购买会员？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        VipActivity.startActivity(getActivity(), 0);
                        startActivity(new Intent(requireActivity(), MemberCentreActivity.class));
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initOperation();
//        initData();
    }

    @Override
    public void onResume() {
        super.onResume();

        int count = LitePal.count(Word.class);
        if (count == 0) {

            progress = 0;
            showLoading("第一次加载较慢，\n请不要退出\n更新进度：" + progress + "%");
            presenter.getExamWord(Constant.GET_EXAM_WORD, AppClient.appName, 1, 3500);
        } else {
            initData();
        }
    }

    private void initOperation() {

        fragmentBreakThroughBinding.toolbar.toolbarIvBack.setVisibility(View.GONE);

        fragmentBreakThroughBinding.toolbar.toolbarIvTitle.setText("单词闯关");
        fragmentBreakThroughBinding.toolbar.toolbarIvRight.setImageResource(R.mipmap.icon_getyun);
        fragmentBreakThroughBinding.toolbar.toolbarIbRight2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initMorePopup();
            }
        });
        fragmentBreakThroughBinding.toolbar.toolbarIvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progress = 0;
                showLoading("第一次加载较慢，\n请不要退出\n更新进度：" + progress + "%");
                presenter.getExamWord(Constant.GET_EXAM_WORD, AppClient.appName, 1, 3500);

            }
        });
        fragmentBreakThroughBinding.btRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        fragmentBreakThroughBinding.btRv.setAdapter(breakThroughAdapter);
        if (fragmentBreakThroughBinding.btRv.getItemDecorationCount() == 0) {

            fragmentBreakThroughBinding.btRv.addItemDecoration(gridSpacingItemDecoration);
        }
    }

    @SuppressLint("Range")
    private void initData() {

        //处理单词库变更，title也要变
        fragmentBreakThroughBinding.toolbar.toolbarIvTitle.setText("单词闯关");

        //此level的数量
        int size = LitePal.count(Word.class);
        int checkpointNuM = size / wordNum;//关卡数量

        //已通过的关卡
        int index = 0;
        List<Checkpoint> checkpointList = new ArrayList<>();
        for (int i = 0; i < checkpointNuM; i++) {


            boolean isJump = false;
            for (int j = 0; j < checkpointList.size(); j++) {

                Checkpoint checkpoint = checkpointList.get(j);
                if (!checkpoint.isPass()) {

                    isJump = true;
                    break;
                }
            }

            if (isJump) {//含没有通过的关卡则直接跳出，后面的都是没有通过的

                index = i;
                break;
            } else {

                //获取正确的数量和这一关的总数量
                Cursor cursor = LitePal.findBySQL("SELECT  COUNT(*) as count,SUM(answer_status) as tcount from (SELECT * FROM word  order by id  LIMIT " + wordNum + " OFFSET " + (i * wordNum) + ")");
                cursor.moveToFirst();
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                int tCount = cursor.getInt(cursor.getColumnIndex("tcount"));
                cursor.close();

                Checkpoint checkpoint = dealPass(count, tCount);
                checkpointList.add(checkpoint);
            }
        }
        //后面的都是没有通过的
        for (int i = index; i < checkpointNuM; i++) {

            Checkpoint checkpoint = new Checkpoint();
            checkpoint.setPass(false);
            checkpoint.settNum(0);
            checkpointList.add(checkpoint);
        }

        //设置可进行的最高关卡,将已通过的关卡设置成通过状态(已通过的关卡可能再次闯关会变成未通过状态)
        int position = 0;
        for (int i = (checkpointList.size() - 1); i >= 0; i--) {

            Checkpoint checkpoint = checkpointList.get(i);
            if (position != 0) {

                checkpoint.setPass(true);
            }
            if (checkpoint.isPass()) {

                position++;
            }
        }
        if (position < checkpointList.size()) {

            checkpointList.get(position).setPass(true);
        }
        breakThroughAdapter.setNewData(checkpointList);
    }


    /**
     * 计算这一关的正确率，大于等于80%算通过
     *
     * @return
     */
    private Checkpoint dealPass(int count, int tCount) {

        int accuracy = (int) (100.0 * tCount / count);
        Checkpoint checkpoint = new Checkpoint();
        checkpoint.settNum(tCount);
        if (accuracy >= 80) {

            checkpoint.setPass(true);
        } else {

            checkpoint.setPass(false);
        }
        return checkpoint;
    }


    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container) {
        fragmentBreakThroughBinding = FragmentBreakThroughBinding.inflate(inflater, container, false);
        return fragmentBreakThroughBinding.getRoot();
    }

    @Override
    protected BreakThroughContract.BreakThroughPresenter initPresenter() {
        return new BreakThroughPresenter();
    }

    @Override
    public void showLoading(String msg) {

        if (loadingPopup == null) {

            loadingPopup = new WordLoadingPopup(getContext());
        }
        loadingPopup.setContent(msg);
        loadingPopup.showPopupWindow();
    }

    @Override
    public void hideLoading() {

        if (loadingPopup != null) {

            loadingPopup.dismiss();
        }
    }

    /**
     * 选择单词个数弹窗
     */
    private void initMorePopup() {

        if (btMorePopup == null) {

            List<String> strings = new ArrayList<>();
            strings.add("每关30个单词");
            strings.add("每关50个单词");
            strings.add("每关70个单词");
            strings.add("每关90个单词");
            strings.add("每关100个单词");

            btMorePopup = new BTMorePopup(getContext());
            btMorePopup.initOperation(strings);
            btMorePopup.setCallback(new BTMorePopup.Callback() {
                @Override
                public void getString(int position) {

                    if (position == 0) {

                        sp.edit().putInt(Constant.SP_KEY_WORD_NUM, 30).apply();
                        wordNum = 30;
                        initData();
                    } else if (position == 1) {

                        sp.edit().putInt(Constant.SP_KEY_WORD_NUM, 50).apply();
                        wordNum = 50;
                        initData();
                    } else if (position == 2) {

                        sp.edit().putInt(Constant.SP_KEY_WORD_NUM, 70).apply();
                        wordNum = 70;
                        initData();
                    } else if (position == 3) {

                        sp.edit().putInt(Constant.SP_KEY_WORD_NUM, 90).apply();
                        wordNum = 90;
                        initData();
                    } else {

                        sp.edit().putInt(Constant.SP_KEY_WORD_NUM, 100).apply();
                        wordNum = 100;
                        initData();
                    }
                    breakThroughAdapter.setWordNum(wordNum);
                    btMorePopup.dismiss();
                }
            });
        }
        btMorePopup.showPopupWindow(fragmentBreakThroughBinding.toolbar.toolbarIbRight2);
    }


    @Override
    public void toast(String msg) {

        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }


    Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            if (msg.what == 1) {

                if (progress == 100) {
                    initData();
                    hideLoading();
                } else {

                    loadingPopup.setContent("第一次加载较慢，\n请不要退出\n更新进度：" + progress + "%");
//                        showLoading("第一次加载较慢，\n请不要退出\n更新进度：" + progress + "%");
                }
            } else if (msg.what == 2) {

                Word word = (Word) msg.obj;
                word.save();
            } else {

                Word word = (Word) msg.obj;
                int id = msg.arg1;
                word.updateAll("rowid = ?", id + "");
            }
            return false;
        }
    });

    @Override
    public void getExamWord(ExamWordBean examWordBean) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Word> jpWordList = examWordBean.getData();
                for (int i = 0; i < jpWordList.size(); i++) {

                    Word word = jpWordList.get(i);
                    List<Word> words = LitePal.where("rowid = ?", word.getRowid() + "").find(Word.class);
                    if (words.size() == 0) {

                        Message message = handler.obtainMessage();
                        message.obj = word;
                        message.what = 2;
                        handler.sendMessage(message);
                    } else {

                        Message message = handler.obtainMessage();
                        message.obj = word;
                        message.what = 3;
                        message.arg1 = word.getRowid();
                        handler.sendMessage(message);
//                        word.updateAll("wordid = ?", word.getId() + "");
                    }
                    progress = (int) (100.0 * (i + 1) / jpWordList.size());
                    Log.d("qweewq", progress + "");


                    if (loadingPopup != null) {//过滤重复的
                        if (loadingPopup.getProgress() != progress) {

                            loadingPopup.setProgress(progress);
                            handler.sendEmptyMessage(1);
                        }
                    }
                }
                //判断用户是否登录，如果登录则同步数据
                if (GlobalHome.Companion.isLogin()) {

                    SimpleDateFormat simpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
                    simpleDateFormat.applyPattern("yyyy-MM-dd");
                    String dateStr = simpleDateFormat.format(new Date());

                    String sign = MD5.getMD5ofStr(GlobalHome.Companion.getUid() + Constant.BREAK_CATEGORY_TYPE + 2 + "W" + Constant.APPID + dateStr);
                    presenter.getExamDetail("json", Constant.APPID, GlobalHome.Companion.getUid(), Constant.BREAK_CATEGORY_TYPE, "W", 2, sign);
                }
            }
        }).start();
    }

    @Override
    public void syncWordData(SyncDataBean syncDataBean) {

        //正确
        List<SyncDataBean.DataRightDTO> dataRightDTOList = syncDataBean.getDataRight();
        for (int i = 0; dataRightDTOList != null && i < dataRightDTOList.size(); i++) {

            SyncDataBean.DataRightDTO dataRightDTO = dataRightDTOList.get(i);
            List<Word> words = LitePal.where("rowid = ?", dataRightDTO.getId() + "").find(Word.class);
            if (words.size() != 0) {

                Word word = words.get(0);
                word.setAnswer_status(1);
                word.updateAll("rowid = ?", dataRightDTO.getId() + "");
            }
        }
        //错误
        List<SyncDataBean.DataRightDTO> dataWrongDTOList = syncDataBean.getDataWrong();
        for (int i = 0; dataWrongDTOList != null && i < dataWrongDTOList.size(); i++) {

            SyncDataBean.DataRightDTO dataRightDTO = dataWrongDTOList.get(i);
            List<Word> words = LitePal.where("rowid = ?", dataRightDTO.getId() + "").find(Word.class);
            if (words.size() != 0) {

                Word word = words.get(0);
                word.setAnswer_status(2);
                word.updateAll("rowid = ?", dataRightDTO.getId() + "");
            }
        }

        toast("数据同步完成");

        initData();
    }
}