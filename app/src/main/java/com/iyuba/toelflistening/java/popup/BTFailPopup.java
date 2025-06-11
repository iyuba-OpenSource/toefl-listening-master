package com.iyuba.toelflistening.java.popup;

import android.content.Context;
import android.view.View;

import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.databinding.PopupBtFailBinding;

import razerdp.basepopup.BasePopupWindow;

public class BTFailPopup extends BasePopupWindow {

    private PopupBtFailBinding popupBtFailBinding;

    private Callback callback;

    public BTFailPopup(Context context) {
        super(context);
        View view = createPopupById(R.layout.popup_bt_fail);
        popupBtFailBinding = PopupBtFailBinding.bind(view);
        setContentView(view);

        setOutSideDismiss(false);//禁止点击外部消失
        setBackPressEnable(false);//禁止点击返回消失

        initOperation();
    }

    private void initOperation() {


        popupBtFailBinding.btfailTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callback != null) {

                    callback.confirm();
                }
            }
        });
    }

    public void setMessage(String msg) {

        popupBtFailBinding.btfailTvInfo.setText(msg);
    }


    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        void confirm();
    }
}
