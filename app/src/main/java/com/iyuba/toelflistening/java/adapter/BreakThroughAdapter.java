package com.iyuba.toelflistening.java.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.java.entity.Checkpoint;

import java.util.List;

public class BreakThroughAdapter extends BaseQuickAdapter<Checkpoint, BaseViewHolder> {

    //没关多少个单词
    private int wordNum = 30;

    public BreakThroughAdapter(int layoutResId, @Nullable List<Checkpoint> data) {
        super(layoutResId, data);
    }


    public int getWordNum() {
        return wordNum;
    }

    public void setWordNum(int wordNum) {
        this.wordNum = wordNum;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Checkpoint item) {


        helper.setText(R.id.bt_tv_checkpoint, "第" + (helper.getAdapterPosition() + 1) + "关");
        if (item.isPass()) {

//            helper.setTextColor(R.id.bt_tv_checkpoint, mContext.getResources().getColor(R.color.colorPrimary));
            helper.setText(R.id.bt_tv_true, item.gettNum() + "/" + wordNum);
            helper.setBackgroundRes(R.id.bt_rl, R.drawable.shape_rctg_bg_bt_green);
        } else {

//            helper.setTextColor(R.id.bt_tv_checkpoint, mContext.getResources().getColor(R.color.black));
            helper.setText(R.id.bt_tv_true, "未解锁");
            helper.setBackgroundRes(R.id.bt_rl, R.drawable.shape_rctg_bg_bt_gray);
        }
    }
}
