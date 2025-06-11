package com.iyuba.toelflistening.java.presenter;


import com.iyuba.toelflistening.java.model.bean.UserInfoResponse;
import com.iyuba.toelflistening.java.model.bean.WxLoginBean;
import com.iyuba.toelflistening.java.model.login.WxLoginModel;
import com.iyuba.toelflistening.java.view.WxLoginContract;

import java.net.UnknownHostException;

import io.reactivex.disposables.Disposable;

public class WxLoginPresenter extends BasePresenter<WxLoginContract.WxLoginView, WxLoginContract.WxLoginModel>
        implements WxLoginContract.WxLoginPresenter {


    @Override
    protected WxLoginContract.WxLoginModel initModel() {
        return new WxLoginModel();
    }

    @Override
    public void getWxAppletToken(String platform, String format, String protocol, String appid, String sign) {

        Disposable disposable = model.getWxAppletToken(platform, format, protocol, appid, sign, new WxLoginContract.Callback() {

            @Override
            public void success(WxLoginBean wxLoginBean) {


                if (wxLoginBean.getResult() == 200) {

                    view.getWxAppletToken(wxLoginBean);
                } else {
                    view.toast(wxLoginBean.getMessage());
                    view.hideLoading();
                }
            }

            @Override
            public void error(Exception e) {

                view.hideLoading();
                if (e instanceof UnknownHostException) {

                    view.toast("请求超时");
                }
            }
        });
        addSubscribe(disposable);
    }

    @Override
    public void getUidByToken(String platform, String format, String protocol, String token, String sign, String appid) {

        Disposable disposable = model.getUidByToken(platform, format, protocol, token, sign, appid,
                new WxLoginContract.Callback() {
                    @Override
                    public void success(WxLoginBean wxLoginBean) {

                        if (wxLoginBean.getResult() == 200) {

                            view.getUidByToken(wxLoginBean);
                        } else if (wxLoginBean.getResult() == 302) {

                            view.toast("微信小程序登录失败");
                            view.hideLoading();
                        } else {

                            view.toast(wxLoginBean.getMessage());
                            view.hideLoading();
                        }
                    }

                    @Override
                    public void error(Exception e) {

                        view.hideLoading();
                        if (e instanceof UnknownHostException) {

                            view.toast("请求超时");
                        }
                    }
                });
        addSubscribe(disposable);
    }

    @Override
    public void getUserInfo(String platform, String format, String appid, String protocol, String id, String myid, String sign) {

        Disposable disposable = model.getUserInfo(platform, format, appid, protocol, id, myid, sign, new WxLoginContract.UserCallback() {
            @Override
            public void success(UserInfoResponse userInfoResponse) {

                view.getUserInfo(userInfoResponse, myid);
                view.hideLoading();
            }

            @Override
            public void error(Exception e) {

                view.hideLoading();
                if (e instanceof UnknownHostException) {

                    view.toast("请求超时");
                }
            }
        });
        addSubscribe(disposable);
    }
}
