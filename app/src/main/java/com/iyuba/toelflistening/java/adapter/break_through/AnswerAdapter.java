package com.iyuba.toelflistening.java.adapter.break_through;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.java.db.Word;

import java.util.List;

public class AnswerAdapter extends BaseQuickAdapter<Word, BaseViewHolder> {

    private int type;

    private int choosePosition = -1;

    /**
     * 正确的位置
     */
    private int tPosition = -1;

    public AnswerAdapter(int layoutResId, @Nullable List<Word> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Word item) {

        if (type == 0) {

            helper.setText(R.id.answer_tv_content, item.getWord());
        } else {

            helper.setText(R.id.answer_tv_content, item.getDef());
        }


        if (choosePosition == -1 && tPosition == -1) {

            helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer);
        } else {

            if(choosePosition == tPosition&&tPosition == helper.getBindingAdapterPosition()){

                helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer_right);
            }else if(choosePosition != tPosition&&choosePosition == helper.getBindingAdapterPosition()){

                helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer_wrong);
            }else if(choosePosition != tPosition&&tPosition == helper.getBindingAdapterPosition()){

                helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer_right);
            }else{

                helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer);
            }
        }
        //显示正确答案的处理
/*        if (choosePosition == -1 && tPosition == -1) {

            helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer);
        } else {

            if (choosePosition == tPosition) {

                if (helper.getAdapterPosition() == tPosition) {

                    helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer_right);
                } else {

                    helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer);
                }
            } else {//位置不一致

                if (choosePosition == helper.getAdapterPosition()) {

                    helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer_wrong);
                } else {

                    if (tPosition == helper.getAdapterPosition()) {

                        helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer_right);
                    } else {

                        helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer);
                    }
                }
            }
        }*/
    }

    public int gettPosition() {
        return tPosition;
    }

    public void settPosition(int tPosition) {
        this.tPosition = tPosition;
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
}
