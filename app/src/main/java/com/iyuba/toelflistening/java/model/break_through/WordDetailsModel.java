package com.iyuba.toelflistening.java.model.break_through;


import com.iyuba.toelflistening.java.model.NetWorkManager;
import com.iyuba.toelflistening.java.model.bean.EvalBean;
import com.iyuba.toelflistening.java.model.bean.WordCollectBean;
import com.iyuba.toelflistening.java.view.WordDetailsContract;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class WordDetailsModel implements WordDetailsContract.WordDetailsModel {


/*    @Override
    public Disposable jtest(Map<String, RequestBody> params, WordDetailsContract.EvaluteRecordCallback evaluteRecordCallback) {

        return NetWorkManager
                .getRequestForIUser()
                .jtest(params)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<EvaluteRecordBean>() {
                    @Override
                    public void accept(EvaluteRecordBean evaluteRecordBean) throws Exception {

                        evaluteRecordCallback.success(evaluteRecordBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        evaluteRecordCallback.error((Exception) throwable);
                    }
                });
    }*/

    @Override
    public Disposable updateWord(String url, String groupName, String mod, String word, String userId,
                                 String format, WordDetailsContract.WordCollectCallback wordCollectCallback) {

        return NetWorkManager
                .getRequest()
                .updateWord(url, groupName, mod, word, userId, format)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WordCollectBean>() {
                    @Override
                    public void accept(WordCollectBean wordCollectBean) throws Exception {

                        wordCollectCallback.success(wordCollectBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        wordCollectCallback.error((Exception) throwable);
                    }
                });
    }

    @Override
    public Disposable eval(String url, RequestBody requestBody, WordDetailsContract.EvalCallback evalCallback) {

        return NetWorkManager
                .getRequest()
                .eval(url, requestBody)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<EvalBean>() {
                    @Override
                    public void accept(EvalBean evalBean) throws Exception {

                        evalCallback.success(evalBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        evalCallback.error((Exception) throwable);
                    }
                });
    }
}
