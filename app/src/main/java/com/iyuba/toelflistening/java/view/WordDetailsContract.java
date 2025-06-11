package com.iyuba.toelflistening.java.view;


import com.iyuba.toelflistening.java.model.BaseModel;
import com.iyuba.toelflistening.java.model.bean.EvalBean;
import com.iyuba.toelflistening.java.model.bean.WordCollectBean;
import com.iyuba.toelflistening.java.presenter.IBasePresenter;

import io.reactivex.disposables.Disposable;
import okhttp3.RequestBody;

public interface WordDetailsContract {

    interface WordDetailsView extends LoadingView {


        void updateCollectComplete(String wordid, String type);


        void eval(EvalBean evalBean);
    }


    interface WordDetailsPresenter extends IBasePresenter<WordDetailsView> {

        void updateWord(String url, String groupName, String mod, String word, String userId, String format, String rowid);

        void eval(RequestBody requestBody);
    }

    interface WordDetailsModel extends BaseModel {

        Disposable updateWord(String url, String groupName, String mod,
                              String word, String userId, String format, WordCollectCallback wordCollectCallback);

        Disposable eval(String url, RequestBody requestBody, EvalCallback evalCallback);
    }

    interface EvalCallback {

        void success(EvalBean evalBean);

        void error(Exception e);
    }

    interface WordCollectCallback {

        void success(WordCollectBean wordCollectBean);

        void error(Exception e);
    }
}
