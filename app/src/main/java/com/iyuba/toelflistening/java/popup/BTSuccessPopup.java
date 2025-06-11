package com.iyuba.toelflistening.java.popup;

import android.content.Context;
import android.view.View;

import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.databinding.PopupBtSuccessBinding;

import razerdp.basepopup.BasePopupWindow;

/**
 * 通关弹窗
 */
public class BTSuccessPopup extends BasePopupWindow {

    private PopupBtSuccessBinding popupBtSuccessBinding;

    private Callback callback;

    public BTSuccessPopup(Context context) {
        super(context);
        View view = createPopupById(R.layout.popup_bt_success);
        popupBtSuccessBinding = PopupBtSuccessBinding.bind(view);
        setContentView(view);

        initOperation();
    }

    private void initOperation() {

        popupBtSuccessBinding.btsButNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callback != null) {

                    callback.next();
                }
            }
        });
        popupBtSuccessBinding.btsIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callback != null) {

                    callback.close();
                }
            }
        });
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {

        void close();

        void next();
    }
}
