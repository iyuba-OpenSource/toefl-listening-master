package com.iyuba.toelflistening.java.view;


import com.iyuba.toelflistening.java.model.BaseModel;
import com.iyuba.toelflistening.java.model.bean.ExamWordBean;
import com.iyuba.toelflistening.java.model.bean.SyncDataBean;
import com.iyuba.toelflistening.java.presenter.IBasePresenter;

import io.reactivex.disposables.Disposable;

public interface BreakThroughContract {


    interface BreakThroughView extends LoadingView {

        void getExamWord(ExamWordBean examWordBean);


        void syncWordData(SyncDataBean syncDataBean);
    }

    interface BreakThroughPresenter extends IBasePresenter<BreakThroughView> {


        void getExamDetail(String format, int appId, int uid,
                           String lesson, String TestMode, int mode, String sign);

        void getExamWord(String url, String type, int level, int wordNumber);
    }

    interface BreakThroughModel extends BaseModel {

        Disposable getExamDetail(String url, String format, int appId, int uid,
                                 String lesson, String TestMode, int mode, String sign, SyncDataCallback callback);

        Disposable getExamWord(String url, String type, int level, int wordNumber, ExamWordCallback examWordCallback);
    }


    interface SyncDataCallback {

        void success(SyncDataBean syncDataBean);

        void error(Exception e);
    }

    interface ExamWordCallback {

        void success(ExamWordBean examWordBean);

        void error(Exception e);
    }

}
