package com.iyuba.toelflistening.java.model.break_through;


import com.iyuba.toelflistening.java.Constant;
import com.iyuba.toelflistening.java.entity.ExamRecordPost;
import com.iyuba.toelflistening.java.model.NetWorkManager;
import com.iyuba.toelflistening.java.view.WordAnswerContract;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class WordAnswerModel implements WordAnswerContract.WordAnswerModel {


    @Override
    public Disposable updateExamRecord(ExamRecordPost examRecordPost, WordAnswerContract.Callback callback) {

        return NetWorkManager
                .getRequest()
                .updateExamRecord(Constant.UPDATE_EXAM_RECORD,examRecordPost)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                        callback.success(responseBody);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        callback.error((Exception) throwable);
                    }
                });
    }
}
