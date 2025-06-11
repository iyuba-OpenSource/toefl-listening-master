package com.iyuba.toelflistening.java.popup;

import android.content.Context;
import android.view.View;

import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.databinding.PopupAnalysisBinding;
import com.iyuba.toelflistening.java.db.Word;

import razerdp.basepopup.BasePopupWindow;

public class AnalysisPopup extends BasePopupWindow {

    private PopupAnalysisBinding popupAnalysisBinding;

    public AnalysisPopup(Context context) {
        super(context);
        View view = createPopupById(R.layout.popup_analysis);
        popupAnalysisBinding = PopupAnalysisBinding.bind(view);
        setContentView(view);

        initOperation();
    }

    private void initOperation() {

        popupAnalysisBinding.analysisIvX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
    }


    public void setJpWord(Word word) {

        popupAnalysisBinding.analysisTvWord.setText(word.getWord());
        popupAnalysisBinding.analysisTvPron.setText(word.getPron());
        popupAnalysisBinding.analysisTvWordch.setText(word.getDef());
//        popupAnalysisBinding.analysisTvSentence.setText(word.getSentence());
//        popupAnalysisBinding.analysisTvSentencech.setText(word.getSentenceCh());
    }


}
