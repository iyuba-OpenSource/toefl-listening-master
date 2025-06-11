package com.iyuba.toelflistening.java.model.login;

import com.iyuba.toelflistening.java.Constant;
import com.iyuba.toelflistening.java.model.NetWorkManager;
import com.iyuba.toelflistening.java.model.bean.UserInfoResponse;
import com.iyuba.toelflistening.java.model.bean.WxLoginBean;
import com.iyuba.toelflistening.java.view.WxLoginContract;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WxLoginModel implements WxLoginContract.WxLoginModel {

    @Override
    public Disposable getWxAppletToken(String platform, String format, String protocol, String appid, String sign,
                                       WxLoginContract.Callback callback) {

        return NetWorkManager
                .getRequest()
                .getWxAppletToken(Constant.PROTOCOL_URL, platform, format, protocol, appid, sign)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WxLoginBean>() {
                    @Override
                    public void accept(WxLoginBean wxLoginBean) throws Exception {

                        callback.success(wxLoginBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        callback.error((Exception) throwable);
                    }
                });
    }

    @Override
    public Disposable getUidByToken(String platform, String format, String protocol, String token, String sign, String appid, WxLoginContract.Callback callback) {

        return NetWorkManager
                .getRequest()
                .getUidByToken(Constant.PROTOCOL_URL, platform, format, protocol, token, sign, appid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WxLoginBean>() {
                    @Override
                    public void accept(WxLoginBean wxLoginBean) throws Exception {

                        callback.success(wxLoginBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        callback.error((Exception) throwable);
                    }
                });
    }


    @Override
    public Disposable getUserInfo(String platform, String format, String appid, String protocol, String id,
                                  String myid, String sign, WxLoginContract.UserCallback callback) {

        return NetWorkManager
                .getRequest()
                .getUserInfo(Constant.PROTOCOL_URL, platform, format, appid, protocol, id, myid, sign)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UserInfoResponse>() {
                    @Override
                    public void accept(UserInfoResponse userInfoResponse) throws Exception {

                        callback.success(userInfoResponse);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        callback.error((Exception) throwable);
                    }
                });
    }
}
