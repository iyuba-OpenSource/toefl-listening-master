package com.iyuba.toelflistening.java.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.iyuba.toelflistening.R;

import java.util.List;

public class MoreAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public MoreAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {

        helper.setText(R.id.more_tv_content, item);
    }
}
