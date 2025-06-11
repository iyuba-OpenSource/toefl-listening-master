package com.iyuba.toelflistening.activity

import androidx.lifecycle.lifecycleScope
import cn.fly.verify.FlyVerify
import cn.fly.verify.PreVerifyCallback
import cn.fly.verify.common.exception.VerifyException
import coil.load
import com.iyuba.dlex.bizs.DLManager
import com.iyuba.headlinelibrary.IHeadline
import com.iyuba.headlinelibrary.data.local.HeadlineInfoHelper
import com.iyuba.headlinelibrary.data.local.db.HLDBManager
import com.iyuba.imooclib.data.local.IMoocDBManager
import com.iyuba.module.dl.BasicDLDBManager
import com.iyuba.module.favor.data.local.BasicFavorDBManager
import com.iyuba.module.favor.data.local.BasicFavorInfoHelper
import com.iyuba.module.privacy.PrivacyInfoHelper
import com.iyuba.share.ShareExecutor
import com.iyuba.share.mob.MobShareExecutor
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.BuildConfig
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.databinding.ActivityWelcomeBinding
import com.iyuba.toelflistening.dialog.PrivacyAgreeDialog
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.OnAgreePrivacyListener
import com.iyuba.toelflistening.utils.OtherUtils
import com.iyuba.toelflistening.utils.YouDaoHelper
import com.iyuba.toelflistening.utils.clickSkipWeb
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.loadYouDao
import com.iyuba.toelflistening.utils.logic.GlobalPlayManager
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.skipWeb
import com.iyuba.toelflistening.utils.startActivity
import com.iyuba.toelflistening.utils.visibilityState
import com.iyuba.widget.unipicker.IUniversityPicker
import com.mob.MobSDK
import com.umeng.commonsdk.UMConfigure
import com.youdao.sdk.common.OAIDHelper
import com.youdao.sdk.common.YoudaoSDK
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import personal.iyuba.personalhomelibrary.PersonalHome
import personal.iyuba.personalhomelibrary.helper.PersonalSPHelper
import personal.iyuba.personalhomelibrary.ui.widget.dialog.ShareBottomDialog

class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {
    private lateinit var fiveJob: Job
    private var agreeFlag = false

    override fun ActivityWelcomeBinding.initBinding() {
        userAction.initSomeThing()
        clickSkipWeb(binding.adBack) {
            if (::fiveJob.isInitialized) {
                fiveJob.cancel()
            }
        }
        lifecycleScope.launch {
            userAction.initResult.collect {
                if (it.first) {

                    skipTv.visibilityState(true)
                    agreeFlag = true
                    PrivacyAgreeDialog(this@WelcomeActivity, privacyListener)
                } else {

                    PersonalSPHelper.init(this@WelcomeActivity)
                    ShareBottomDialog.setSharedPlatform(arrayListOf("微信好友", "QQ"))
                    initHeadline()
                    initPersonalHome()
                    IMoocDBManager.init(this@WelcomeActivity)
                    initSecVerify()
                    BasicFavorInfoHelper.init(this@WelcomeActivity)
                    GlobalPlayManager.preparePlayer(this@WelcomeActivity)
                    initYouDao()
                }
                if (!it.second && System.currentTimeMillis() > BuildConfig.AD_TIME) {
                    adModel.requestAdType()
                } else if (!it.first) {
                    startMain()
                }
                if (GlobalHome.isLogin()) {
                    refreshUserInfo()
                }
            }
        }
        lifecycleScope.launch {
            adModel.lastAdResult.collect { result ->
                result.onSuccess {
                    if (it.type == "youdao") {
                        loadYouDao(binding.adBack, error = { loadAdError() })
                    } else {
                        loadAdError(it.startuppic)
                    }
                }.onError {
                    it.judgeType().showToast()
                }
            }
        }
        skipTv.setOnClickListener {
            if (::fiveJob.isInitialized) {
                fiveJob.cancel()
            }
            startMain()
        }
        /*        initHeadline()
                initPersonalHome()
                IMoocDBManager.init(this@WelcomeActivity)
                initSecVerify()
                BasicFavorInfoHelper.init(this@WelcomeActivity)
                GlobalPlayManager.preparePlayer(this@WelcomeActivity)*/
    }

    /**
     * 秒验预取号设置在1000-10000之内
     * */
    private fun initSecVerify() {
        FlyVerify.setTimeOut(8000)
        FlyVerify.preVerify(object : PreVerifyCallback() {
            override fun onComplete(p0: Void?) {

            }

            override fun onFailure(p0: VerifyException?) {

            }
        })
    }

    private fun initPersonalHome() {
        IUniversityPicker.init(this)
        val appId = AppClient.appId.toString()
        val appName = getString(R.string.app_name)
        PersonalHome.init(this, appId, appName)
        PersonalHome.setIsCompress(true)
        PersonalHome.setEnableEditNickname(true)
        PersonalHome.setAppInfo(appId, appName)
        PersonalHome.setCategoryType(AppClient.appName)
        PersonalHome.setMainPath(javaClass.name)
    }

    private fun loadAdError(endUrl: String = "upload/1512959242535.png") {
        val url = with(StringBuilder()) {
            append(OtherUtils.splashHead)
            append(endUrl)
            toString()
        }
        binding.apply {
            adBack.load(url)
            skipTv.visibilityState(false)
        }
        if (!::fiveJob.isInitialized) {
            fiveJob = countdownFive()
        }
    }

    private fun initHeadline() {
        IHeadline.init(this, AppClient.appId.toString(), AppClient.appName, true)
        IHeadline.setEnableShare(true)
        IHeadline.setEnableGoStore(true)
        HeadlineInfoHelper.init(this)
        HLDBManager.init(this)
        PrivacyInfoHelper.init(this)
        DLManager.init(this, 5)
        BasicFavorDBManager.init(this)
        BasicDLDBManager.init(this)
        PrivacyInfoHelper.getInstance().putApproved(true)
        //一言难尽的arr互相嵌套，徒增无用功
        ShareExecutor.getInstance().realExecutor = MobShareExecutor()
    }

    private fun initYouDao() {
        YouDaoHelper {
            OAIDHelper.getInstance().oaid = it
        }
        YoudaoSDK.init(this)
    }

    private val privacyListener = object : OnAgreePrivacyListener {
        override fun seekPrivacy() {
            skipWeb(AppClient.termsUse)
        }

        override fun seekProtocol() {
            skipWeb(AppClient.privacy)
        }

        override fun agree() {


            // 初始化MobSDK
            MobSDK.submitPolicyGrantResult(true)

            // 初始化友盟SDK (需用户同意隐私政策后调用)
            UMConfigure.init(
                this@WelcomeActivity,
                UMConfigure.DEVICE_TYPE_PHONE,
                resources.getString(R.string.um_key)
            )

            PersonalSPHelper.init(this@WelcomeActivity)
            ShareBottomDialog.setSharedPlatform(arrayListOf("微信好友", "QQ"))
            initHeadline()
            initPersonalHome()
            IMoocDBManager.init(this@WelcomeActivity)
            initSecVerify()
            BasicFavorInfoHelper.init(this@WelcomeActivity)
            GlobalPlayManager.preparePlayer(this@WelcomeActivity)
            initYouDao()

            lifecycleScope.launch {
                toeflViewModel.saveFirstLogin(false).first()
            }
            startMain()
            finish()
        }

        override fun noAgree() {
            finish()
        }
    }

    /**
     * 倒计时5S
     * */
    private fun countdownFive() = lifecycleScope.launch {
        flow {
            val range = 5 downTo 1
            for (i in range) {
                emit(i)
                delay(1000)
            }
        }.onCompletion {
            if (!agreeFlag) {
                startMain()
            }
        }.collect {
            binding.skipTv.text = with(StringBuilder()) {
                append(getString(R.string.skip))
                append("(")
                append(it)
                append("S")
                append(")")
                toString()
            }
        }
    }

    private fun startMain() {
        startActivity<MainActivity> { }
    }
}