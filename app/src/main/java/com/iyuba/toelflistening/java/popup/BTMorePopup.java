package com.iyuba.toelflistening.java.popup;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.databinding.PopupBtMoreBinding;
import com.iyuba.toelflistening.java.adapter.MoreAdapter;

import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class BTMorePopup extends BasePopupWindow {


    private PopupBtMoreBinding popupBtMoreBinding;

    private MoreAdapter moreAdapter;

    private Callback callback;

    public BTMorePopup(Context context) {
        super(context);

        View view = createPopupById(R.layout.popup_bt_more);
        popupBtMoreBinding = PopupBtMoreBinding.bind(view);
        setContentView(view);

        setAlignBackground(true);
    }

    public void initOperation(List<String> strings) {

        popupBtMoreBinding.moreRv.setLayoutManager(new LinearLayoutManager(getContext()));
        moreAdapter = new MoreAdapter(R.layout.item_more, strings);
        popupBtMoreBinding.moreRv.setAdapter(moreAdapter);
        moreAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (callback != null) {

                    callback.getString(position);
                }
            }
        });
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        void getString(int position);
    }
}
