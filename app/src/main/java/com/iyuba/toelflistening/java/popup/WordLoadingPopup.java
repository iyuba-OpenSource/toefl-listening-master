package com.iyuba.toelflistening.java.popup;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.iyuba.toelflistening.R;

import razerdp.basepopup.BasePopupWindow;

public class WordLoadingPopup extends BasePopupWindow {

    private TextView loading_tv_content;

    private int progress = -1;

    public WordLoadingPopup(Context context) {
        super(context);

        View view = createPopupById(R.layout.popup_loading);
        setContentView(view);
    }

    @Override
    public void onViewCreated(@NonNull View contentView) {
        super.onViewCreated(contentView);

        loading_tv_content = contentView.findViewById(R.id.loading_tv_content);

    }

    public void setContent(String content) {

        loading_tv_content.setText(content);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
