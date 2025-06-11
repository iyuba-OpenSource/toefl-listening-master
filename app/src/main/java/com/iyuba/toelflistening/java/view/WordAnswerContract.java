package com.iyuba.toelflistening.java.view;


import com.iyuba.toelflistening.java.entity.ExamRecordPost;
import com.iyuba.toelflistening.java.model.BaseModel;
import com.iyuba.toelflistening.java.model.bean.ExamRecordBean;
import com.iyuba.toelflistening.java.presenter.IBasePresenter;

import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public interface WordAnswerContract {


    interface WordAnswerView extends LoadingView {

        void updateExamRecordComplete(ExamRecordBean examRecordBean);
    }

    interface WordAnswerPresenter extends IBasePresenter<WordAnswerView> {


        void updateExamRecord(ExamRecordPost bean);

    }


    interface WordAnswerModel extends BaseModel {

        Disposable updateExamRecord(ExamRecordPost bean, Callback callback);
    }

    interface Callback {

        void success(ResponseBody examRecordBean);

        void error(Exception e);
    }
}
