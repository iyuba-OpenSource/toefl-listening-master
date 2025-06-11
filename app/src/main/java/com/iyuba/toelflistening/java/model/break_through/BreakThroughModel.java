package com.iyuba.toelflistening.java.model.break_through;


import com.iyuba.toelflistening.java.model.NetWorkManager;
import com.iyuba.toelflistening.java.model.bean.ExamWordBean;
import com.iyuba.toelflistening.java.model.bean.SyncDataBean;
import com.iyuba.toelflistening.java.view.BreakThroughContract;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class BreakThroughModel implements BreakThroughContract.BreakThroughModel {


    @Override
    public Disposable getExamDetail(String url, String format, int appId, int uid, String lesson, String TestMode,
                                    int mode, String sign, BreakThroughContract.SyncDataCallback callback) {

        return NetWorkManager
                .getRequest()
                .getExamDetail(url, format, appId, uid, lesson, TestMode, mode, sign)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<SyncDataBean>() {
                    @Override
                    public void accept(SyncDataBean syncDataBean) throws Exception {

                        callback.success(syncDataBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        callback.error((Exception) throwable);
                    }
                });
    }

    @Override
    public Disposable getExamWord(String url, String type, int level, int wordNumber, BreakThroughContract.ExamWordCallback examWordCallback) {

        return NetWorkManager
                .getRequest()
                .getExamWord(url, type, level, wordNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ExamWordBean>() {
                    @Override
                    public void accept(ExamWordBean examWordBean) throws Exception {

                        examWordCallback.success(examWordBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        examWordCallback.error((Exception) throwable);
                    }
                });
    }
}
