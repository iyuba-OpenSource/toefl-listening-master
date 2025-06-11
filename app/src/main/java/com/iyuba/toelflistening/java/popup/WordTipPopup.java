package com.iyuba.toelflistening.java.popup;

import android.content.Context;
import android.view.View;

import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.databinding.PopupWordTipBinding;

import razerdp.basepopup.BasePopupWindow;

public class WordTipPopup extends BasePopupWindow {


    private PopupWordTipBinding popupWordTipBinding;

    private Callback callback;

    public WordTipPopup(Context context) {
        super(context);
        View view = createPopupById(R.layout.popup_word_tip);
        popupWordTipBinding = PopupWordTipBinding.bind(view);
        setContentView(view);

        initOperation();
    }


    private void initOperation() {

        popupWordTipBinding.wtTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callback != null) {
                    callback.cancel();
                }
            }
        });
        popupWordTipBinding.wtTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callback != null) {
                    callback.confirm();
                }
            }
        });
    }


    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        void cancel();

        void confirm();
    }
}
