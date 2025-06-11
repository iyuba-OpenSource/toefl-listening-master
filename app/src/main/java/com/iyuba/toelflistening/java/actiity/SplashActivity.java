package com.iyuba.toelflistening.java.actiity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Log;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.data.local.HeadlineInfoHelper;
import com.iyuba.headlinelibrary.data.local.db.HLDBManager;
import com.iyuba.imooclib.IMooc;
import com.iyuba.imooclib.data.local.IMoocDBManager;
import com.iyuba.module.dl.BasicDLDBManager;
import com.iyuba.module.favor.data.local.BasicFavorDBManager;
import com.iyuba.module.favor.data.local.BasicFavorInfoHelper;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.iyuba.share.ShareExecutor;
import com.iyuba.share.mob.MobShareExecutor;
import com.iyuba.toelflistening.AppClient;
import com.iyuba.toelflistening.BuildConfig;
import com.iyuba.toelflistening.R;
import com.iyuba.toelflistening.activity.MainActivity;
import com.iyuba.toelflistening.activity.UseInstructionsActivity;
import com.iyuba.toelflistening.bean.LoginResponse;
import com.iyuba.toelflistening.dao.UserDao;
import com.iyuba.toelflistening.databinding.ActivitySplashBinding;
import com.iyuba.toelflistening.java.Constant;
import com.iyuba.toelflistening.java.model.bean.AdEntryBean;
import com.iyuba.toelflistening.java.presenter.SplashPresenter;
import com.iyuba.toelflistening.java.util.PrivacyPopup;
import com.iyuba.toelflistening.java.view.SplashContract;
import com.iyuba.toelflistening.utils.GlobalHome;
import com.iyuba.toelflistening.utils.YouDaoHelper;
import com.iyuba.toelflistening.utils.logic.GlobalPlayManager;
import com.iyuba.widget.unipicker.IUniversityPicker;
import com.mob.MobSDK;
import com.umeng.commonsdk.UMConfigure;
import com.yd.saas.base.interfaces.AdViewSpreadListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdSpread;
import com.yd.saas.ydsdk.manager.YdConfig;
import com.youdao.sdk.common.OAIDHelper;
import com.youdao.sdk.common.YouDaoAd;
import com.youdao.sdk.common.YoudaoSDK;

import java.util.ArrayList;
import java.util.List;

import cn.fly.verify.FlyVerify;
import cn.fly.verify.PreVerifyCallback;
import cn.fly.verify.common.exception.VerifyException;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.helper.PersonalSPHelper;
import personal.iyuba.personalhomelibrary.ui.widget.dialog.ShareBottomDialog;

/**
 * 启动广告
 */
public class SplashActivity extends BaseActivity<SplashContract.SplashView, SplashContract.SplashPresenter>
        implements SplashContract.SplashView, AdViewSpreadListener {


    private SharedPreferences sp;

    private PrivacyPopup privacyPopup;

    private ActivitySplashBinding binding;

    private AdEntryBean.DataDTO dataDTO;

    private boolean isAdCLick = false;

    private CountDownTimer webTimer;

    private YdSpread mSplashAd;

    /**
     * 是否请求了title的广告
     */
    private boolean isRequestTitle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dealWindow();
        initView();

        sp = getSharedPreferences("toefl", MODE_PRIVATE);
        boolean pState = sp.getBoolean("firstLogin", false);
        if (!pState) {

            initPrivacyPopup();
        } else {

            init();

            UserDao userDao = UserDao.INSTANCE;
            LoginResponse loginResponse = userDao.getLoginResponse();
            int uid;
            if (loginResponse.getUid() != -1) {

                uid = loginResponse.getUid();
            } else {
                uid = 0;
            }
            if (System.currentTimeMillis() > BuildConfig.AD_TIME) {

                presenter.getAdEntryAll(AppClient.adAppId + "", 1, uid + "");
            }
            countDownTimer.start();
        }
    }

    private void initView() {

    }

    @Override
    public View initLayout() {
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public SplashContract.SplashPresenter initPresenter() {
        return new SplashPresenter();
    }

    private void initPrivacyPopup() {

        if (privacyPopup == null) {

            privacyPopup = new PrivacyPopup(this);
            privacyPopup.setCallback(new PrivacyPopup.Callback() {
                @Override
                public void yes() {


//                    LocalBroadcastManager.getInstance(SplashActivity.this).sendBroadcastSync(new Intent(Constant.ACTION_INIT));

                    init();
                    sp.edit().putBoolean("firstLogin", true).apply();
                    privacyPopup.dismiss();

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void no() {

                    privacyPopup.dismiss();
                    finish();
                }

                @Override
                public void user() {

                    Intent intent = new Intent(SplashActivity.this, UseInstructionsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("out_web_page", AppClient.termsUse);
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    MyWebActivity.startActivity(SplashActivity.this, Constant.URL_PROTOCOLUSE, "用户协议");
                }

                @Override
                public void privacy() {

                    Intent intent = new Intent(SplashActivity.this, UseInstructionsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("out_web_page", AppClient.privacy);
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    MyWebActivity.startActivity(SplashActivity.this, Constant.URL_PROTOCOLPRI, "隐私政策");
                }
            });
        }
        privacyPopup.showPopupWindow();
    }

    private void init() {

        // 初始化MobSDK
        MobSDK.submitPolicyGrantResult(true);
        FlyVerify.submitPolicyGrantResult(true);
        YdConfig.getInstance().init(getApplicationContext(), Constant.APPID + "");

        // 初始化友盟SDK (需用户同意隐私政策后调用)
        UMConfigure.init(getApplicationContext(), UMConfigure.DEVICE_TYPE_PHONE,
                getString(R.string.um_key));

        PersonalSPHelper.init(getApplicationContext());

        List<String> shareListener = new ArrayList<>();
        shareListener.add("微信好友");
        shareListener.add("QQ");
        ShareBottomDialog.setSharedPlatform(shareListener);
        initHeadline();
        initPersonalHome();

        IMooc.init(getApplicationContext(), AppClient.appId + "", AppClient.appName);
        IMooc.setAdAppId(AppClient.adAppId + "");
        IMooc.setYdsdkTemplateKey(
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_CSJ,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_YLH,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_KS,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_BD,
                null);


        IMoocDBManager.init(AppClient.context);
        initSecVerify();
        BasicFavorInfoHelper.init(AppClient.context);
        GlobalPlayManager.INSTANCE.preparePlayer(AppClient.context);
        initYouDao();

        if (GlobalHome.Companion.isLogin()) {

            UserDao userDao = UserDao.INSTANCE;
            LoginResponse loginResponse = userDao.getLoginResponse();
            GlobalHome.Companion.inflateLoginInfo(loginResponse);
        }
    }

    private void initHeadline() {

        IHeadline.init(this, AppClient.appId + "", AppClient.appName, true);
        IHeadline.setAdAppId(AppClient.adAppId + "");
        IHeadline.setYdsdkTemplateKey(
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_CSJ,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_YLH,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_KS,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_BD,
                null);

        IHeadline.setEnableSmallVideoTalk(true);
        IHeadline.setEnableShare(true);
        IHeadline.setEnableGoStore(true);
        HeadlineInfoHelper.init(this);
        HLDBManager.init(this);
        PrivacyInfoHelper.init(this);
        DLManager.init(this, 5);
        BasicFavorDBManager.init(this);
        BasicDLDBManager.init(this);
        PrivacyInfoHelper.getInstance().putApproved(true);
        //一言难尽的arr互相嵌套，徒增无用功
        ShareExecutor.getInstance().setRealExecutor(new MobShareExecutor());
    }

    private void initPersonalHome() {

        IUniversityPicker.init(getApplicationContext());
        String appId = AppClient.appId + "";
        String appName = getString(R.string.app_name);
        PersonalHome.init(getApplicationContext(), appId, appName);
        PersonalHome.setIsCompress(true);
        PersonalHome.setEnableEditNickname(true);
        PersonalHome.setAppInfo(appId, appName);
        PersonalHome.setCategoryType(AppClient.appName);
        PersonalHome.setMainPath(MainActivity.class.getSimpleName());
    }

    private void initSecVerify() {

        FlyVerify.setTimeOut(8000);
        FlyVerify.preVerify(new PreVerifyCallback() {
            @Override
            public void onComplete(Void unused) {

            }

            @Override
            public void onFailure(VerifyException e) {

            }
        });
    }

    private void initYouDao() {


        //取消申请读取应用列表权限
        YouDaoAd.getNativeDownloadOptions().setConfirmDialogEnabled(true);
        YouDaoAd.getYouDaoOptions().setCanObtainAndroidId(false);
        YouDaoAd.getYouDaoOptions().setAppListEnabled(false);
        YouDaoAd.getYouDaoOptions().setPositionEnabled(false);
        YouDaoAd.getYouDaoOptions().setSdkDownloadApkEnabled(true);
        YouDaoAd.getYouDaoOptions().setDeviceParamsEnabled(false);
        YouDaoAd.getYouDaoOptions().setWifiEnabled(false);

        YouDaoHelper youDaoHelper = new YouDaoHelper(new YouDaoHelper.AppIdsUpdater() {
            @Override
            public void onIdsValid(String ids) {

                OAIDHelper.getInstance().setOAID(ids);
            }
        });
        YoudaoSDK.init(getApplicationContext());
        OAIDHelper.getInstance().init(getApplicationContext());

        youDaoHelper.getDeviceIds(getApplicationContext(), true, false, false);

    }


    /**
     * 处理状态栏和虚拟返回键
     */
    private void dealWindow() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {

            WindowInsetsController windowInsetsController = getWindow().getInsetsController();
            windowInsetsController.hide(WindowInsets.Type.systemBars());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isAdCLick) {//点击了就直接跳转mainactivity

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {

            countDownTimer.cancel();
        }
    }

    /**
     * 计时器
     */
    CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {


            if (dataDTO == null) {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }
    };


    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(AppClient.context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getAdEntryAllComplete(AdEntryBean adEntryBean) {

        dataDTO = adEntryBean.getData();
        String type = dataDTO.getType();
        if (type.equals("web")) {

            dealAdWeb();
        } else if (type.startsWith("ads")) {

            dealAds(type);
        }
    }


    /**
     * 获取开屏广告
     *
     * @param type
     */
    private void dealAds(String type) {

        String adKey = null;
        if (type.equals(Constant.AD_ADS1)) {

            adKey = BuildConfig.SPLASH_AD_KEY_BZ;
        } else if (type.equals(Constant.AD_ADS2)) {

            adKey = BuildConfig.SPLASH_AD_KEY_CSJ;
        } else if (type.equals(Constant.AD_ADS3)) {

            adKey = BuildConfig.SPLASH_AD_KEY_BD;
        } else if (type.equals(Constant.AD_ADS4)) {

            adKey = BuildConfig.SPLASH_AD_KEY_YLH;
        } else if (type.equals(Constant.AD_ADS5)) {

            adKey = BuildConfig.SPLASH_AD_KEY_KS;
        }
        if (adKey != null) {

            mSplashAd = new YdSpread.Builder(SplashActivity.this)
                    .setKey(adKey)
                    .setContainer(binding.splashFlContent)
                    .setSpreadListener(this)
                    .setCountdownSeconds(4)
                    .setSkipViewVisibility(true)
                    .build();

            mSplashAd.requestSpread();
            android.util.Log.d("adadad", "name:" + adKey);
        } else {//没有key的时候使用默认的广告

            dealAdWeb();
        }
    }

    /**
     * 处理web
     */
    private void dealAdWeb() {

        binding.splashIvBottom.setVisibility(View.VISIBLE);
        binding.splashFlContent.removeAllViews();
        View adView = LayoutInflater.from(SplashActivity.this).inflate(R.layout.splash_web, null);
        ImageView sw_iv_pic = adView.findViewById(R.id.sw_iv_pic);
        TextView sw_tv_jump = adView.findViewById(R.id.sw_tv_jump);
        binding.splashFlContent.addView(adView);

        Glide.with(adView.getContext())
                .load("http://dev.iyuba.cn/" + dataDTO.getStartuppic())
                .into(sw_iv_pic);
        sw_tv_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (webTimer != null) {

                    webTimer.cancel();
                }

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
        adView.setOnClickListener(view -> {

            if (dataDTO == null) {
                return;
            }
            if (dataDTO.getType().equals("web")) {

                if (!dataDTO.getStartuppicUrl().trim().equals("")) {

                    Intent intent = new Intent(SplashActivity.this, UseInstructionsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("out_web_page", dataDTO.getStartuppicUrl());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        webTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

                sw_tv_jump.setText("跳转(" + (l / 1000) + "s)");
            }

            @Override
            public void onFinish() {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }.start();
    }

    @Override
    public void onAdDisplay() {

        binding.splashIvBottom.setVisibility(View.VISIBLE);
        Log.d("adadad", "onAdDisplay");
    }

    @Override
    public void onAdClose() {

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onAdClick(String s) {

        isAdCLick = true;
        Log.d("adadad", "onAdClick");
    }

    @Override
    public void onAdFailed(YdError ydError) {

        if (!isRequestTitle) {

            isRequestTitle = true;
            dealAds(dataDTO.getTitle());
        } else {

            dealAdWeb();
        }
        Log.d("adadad", "onAdFailed");
    }
}