package com.iyuba.toelflistening.java.adapter.break_through;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.java.db.Word;

import java.util.List;

public class CPWordAdapter extends BaseQuickAdapter<Word, BaseViewHolder> {

    public CPWordAdapter(int layoutResId, @Nullable List<Word> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Word item) {

        //日文
        helper.setText(R.id.cpword_tv_word, item.getWord());
        if (item.getPron() == null) {

            helper.setText(R.id.cpword_tv_pron, "");
        } else {

            helper.setText(R.id.cpword_tv_pron, "[" + item.getPron() + "]");
        }

        //中文
        helper.setText(R.id.cpword_tv_wordch, item.getDef());

        int aStatus = item.getAnswer_status();
        if (aStatus == 0) {

            helper.setTextColor(R.id.cpword_tv_word, Color.BLACK);
            helper.setTextColor(R.id.cpword_tv_wordch, Color.BLACK);
            helper.setTextColor(R.id.cpword_tv_pron, Color.BLACK);

        } else if (aStatus == 1) {

            int gColor = mContext.getResources().getColor(R.color.colorPrimary);
            helper.setTextColor(R.id.cpword_tv_word, gColor);
            helper.setTextColor(R.id.cpword_tv_wordch, gColor);
            helper.setTextColor(R.id.cpword_tv_pron, gColor);
        } else {

            int rColor = Color.parseColor("#E61A1A");
            helper.setTextColor(R.id.cpword_tv_word, rColor);
            helper.setTextColor(R.id.cpword_tv_wordch, rColor);
            helper.setTextColor(R.id.cpword_tv_pron, rColor);
        }

        if (item.isShow()) {

            helper.setGone(R.id.cpword_iv_info, true);
            helper.setGone(R.id.cpword_tv_word, false);
            helper.setGone(R.id.cpword_tv_wordch, true);
            helper.setGone(R.id.cpword_tv_pron, false);
        } else {

            helper.setGone(R.id.cpword_iv_info, false);
            helper.setGone(R.id.cpword_tv_word, true);
            helper.setGone(R.id.cpword_tv_wordch, false);
            helper.setGone(R.id.cpword_tv_pron, true);
        }
        helper.addOnClickListener(R.id.cpword_iv_info);
    }
}
