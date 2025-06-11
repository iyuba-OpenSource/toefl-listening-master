package com.iyuba.toelflistening.java.util;

import android.content.Context;
import android.widget.TextView;

import com.iyuba.toelflistening.R;

import razerdp.basepopup.BasePopupWindow;

public class LoadingPopup extends BasePopupWindow {

    private TextView loading_tv_content;

    public LoadingPopup(Context context) {
        super(context);
        setContentView(createPopupById(R.layout.popup_loading));
        loading_tv_content = findViewById(R.id.loading_tv_content);
    }


    public void setMessage(String msg) {

        loading_tv_content.setText(msg);
    }
}
