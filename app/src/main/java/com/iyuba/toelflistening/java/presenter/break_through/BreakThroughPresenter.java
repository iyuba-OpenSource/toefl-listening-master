package com.iyuba.toelflistening.java.presenter.break_through;


import com.iyuba.toelflistening.java.Constant;
import com.iyuba.toelflistening.java.model.bean.ExamWordBean;
import com.iyuba.toelflistening.java.model.bean.SyncDataBean;
import com.iyuba.toelflistening.java.model.break_through.BreakThroughModel;
import com.iyuba.toelflistening.java.presenter.BasePresenter;
import com.iyuba.toelflistening.java.view.BreakThroughContract;

import java.net.UnknownHostException;

import io.reactivex.disposables.Disposable;

public class BreakThroughPresenter extends BasePresenter<BreakThroughContract.BreakThroughView, BreakThroughContract.BreakThroughModel>
        implements BreakThroughContract.BreakThroughPresenter {

    @Override
    protected BreakThroughContract.BreakThroughModel initModel() {
        return new BreakThroughModel();
    }


    @Override
    public void getExamDetail(String format, int appId, int uid, String lesson, String TestMode, int mode, String sign) {

        Disposable disposable = model.getExamDetail(Constant.GET_EXAM_DETAIL, format, appId, uid, lesson, TestMode, mode, sign, new BreakThroughContract.SyncDataCallback() {

            @Override
            public void success(SyncDataBean syncDataBean) {

                if (syncDataBean.getResult() == 1) {

                    view.syncWordData(syncDataBean);
                }
            }

            @Override
            public void error(Exception e) {

                if (e instanceof UnknownHostException) {

                    view.toast("请求超时");
                }
            }
        });
        addSubscribe(disposable);
    }

    @Override
    public void getExamWord(String url, String type, int level, int wordNumber) {

        Disposable disposable = model.getExamWord(url, type, level, wordNumber, new BreakThroughContract.ExamWordCallback() {
            @Override
            public void success(ExamWordBean examWordBean) {

                if (examWordBean.getMessage().equals("success")) {

                    view.getExamWord(examWordBean);
                }
            }

            @Override
            public void error(Exception e) {

                if (e instanceof UnknownHostException) {

                    view.toast("请求超时");
                }
            }
        });
        addSubscribe(disposable);
    }
}
