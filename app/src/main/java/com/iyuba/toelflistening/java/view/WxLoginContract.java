package com.iyuba.toelflistening.java.view;


import com.iyuba.toelflistening.java.model.BaseModel;
import com.iyuba.toelflistening.java.model.bean.UserInfoResponse;
import com.iyuba.toelflistening.java.model.bean.WxLoginBean;
import com.iyuba.toelflistening.java.presenter.IBasePresenter;

import io.reactivex.disposables.Disposable;

public interface WxLoginContract {

    interface WxLoginView extends LoadingView {


        void getWxAppletToken(WxLoginBean wxLoginBean);

        void getUidByToken(WxLoginBean wxLoginBean);

        void getUserInfo(UserInfoResponse userInfoResponse, String uid);
    }


    interface WxLoginPresenter extends IBasePresenter<WxLoginView> {

        void getWxAppletToken(String platform, String format, String protocol, String appid, String sign);

        void getUidByToken(String platform, String format, String protocol, String token, String sign, String appid);

        void getUserInfo(String platform, String format, String appid,
                         String protocol, String id, String myid, String sign);
    }

    interface WxLoginModel extends BaseModel {

        Disposable getWxAppletToken(String platform, String format, String protocol, String appid, String sign, Callback callback);

        Disposable getUidByToken(String platform, String format, String protocol, String token, String sign, String appid, Callback callback);


        Disposable getUserInfo(String platform, String format, String appid,
                               String protocol, String id, String myid, String sign, UserCallback callback);
    }

    interface UserCallback {

        void success(UserInfoResponse userInfoResponse);

        void error(Exception e);
    }

    interface Callback {

        void success(WxLoginBean wxLoginBean);

        void error(Exception e);
    }
}
