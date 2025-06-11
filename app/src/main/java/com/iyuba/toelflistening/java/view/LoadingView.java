package com.iyuba.toelflistening.java.view;

public interface LoadingView extends BaseView {


    void showLoading(String msg);

    void hideLoading();

    void toast(String msg);
}
