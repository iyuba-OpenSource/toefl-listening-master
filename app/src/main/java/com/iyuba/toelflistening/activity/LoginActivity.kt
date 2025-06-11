package com.iyuba.toelflistening.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import cn.fly.verify.FlyVerify
import cn.fly.verify.VerifyCallback
import cn.fly.verify.common.exception.VerifyException
import cn.fly.verify.datatype.UiSettings
import cn.fly.verify.datatype.VerifyResult
import cn.smssdk.gui.CommonDialog
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.bean.LoginBean
import com.iyuba.toelflistening.databinding.ActivityLoginBinding
import com.iyuba.toelflistening.utils.ExtraKeyFactory
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.isDataEnabled
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.showPassWordEmpty
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.showUserNameEmpty
import com.iyuba.toelflistening.utils.skipWeb
import com.iyuba.toelflistening.utils.startActivity
import com.iyuba.toelflistening.utils.visibilityState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity<ActivityLoginBinding>(), View.OnClickListener {

    private val loginBean = LoginBean()
    private lateinit var dialog: Dialog
    private lateinit var timeOutJob: Job

    private var isSec: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var bundle = intent.extras;
        if (bundle != null) {

            isSec = bundle.getBoolean("isSec", true)//是否执行秒验
            if (isSec) {

                binding.loginLlContent.visibility = View.GONE
            } else {

                binding.loginLlContent.visibility = View.VISIBLE
            }
        }

        if (isSec) {

            startSecondVerify()
        }
    }


    override fun ActivityLoginBinding.initBinding() {
        setTitleText("登录")
        loginBt.setOnClickListener(this@LoginActivity)
        register.setOnClickListener(this@LoginActivity)
        forgotPassword.setOnClickListener(this@LoginActivity)
        loginItem = loginBean
        forgotPassword.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        register.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        dialog = CommonDialog.ProgressDialog(this@LoginActivity)
        val secondTestEnable = (FlyVerify.isVerifySupport() && hasSimCard() && isDataEnabled())
        changeChildViewStatus(secondTestEnable)
        if (!secondTestEnable) {
            return
        }
//        timeOutJob=lifecycleScope.launch {
//            flow {
//                kotlinx.coroutines.delay(8000)
//                emit(0)
//            }.collect{
//                if (dialog.isShowing){
//                    dialog.dismiss()
//                }
//                changeChildViewStatus(false)
//            }
//        }

        if (dialog.isShowing) {
            dialog.dismiss()
        }
        changeChildViewStatus(false)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.login_bt -> login()
            R.id.register -> {
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }

            R.id.forgot_password -> {
                val forgotPassword = "http://m.iyuba.cn/m_login/inputPhonefp.jsp"
                skipWeb(forgotPassword)
            }
        }
    }

    private fun login() {

        binding.usernameInputLogin.showUserNameEmpty(flag = loginBean.isUserNameEmpty()) {
            binding.passwordInputLogin.showPassWordEmpty(loginBean.isPasswordEmpty()) {
                lifecycleScope.launch {
                    userAction.login(loginBean).catch {
                        it.judgeType().showToast()
                    }.collect {
                        if (!it.isEmpty()) {
                            GlobalHome.inflateLoginInfo(it)
                            refreshUserInfo()
                            "登录成功".showToast()
                            userAction.saveLoginResponse(it).first()
                            finish()
                        } else {
                            "登录失败，请检查用户名密码".showToast()
                        }
                    }
                }
            }
        }
    }

    private fun hasSimCard(): Boolean {
        //判断是否包含SIM卡
        val telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (telMgr.simState) {
            TelephonyManager.SIM_STATE_ABSENT -> false; // 没有SIM卡
            TelephonyManager.SIM_STATE_UNKNOWN -> false
            else -> true
        }
    }

    private fun changeChildViewStatus(flag: Boolean) {
        if (binding.root !is LinearLayout) {
            return
        }
        val layout = binding.root as LinearLayout
        for (i in (1 until layout.childCount)) {
            layout.getChildAt(i).visibilityState(flag)
        }
    }

    private fun startSecondVerify() {
        dialog.show()
        var clickOtherLogin = false
        var clickLogin = false
        FlyVerify.OtherOAuthPageCallBack {
            it.pageOpenCallback {

                dialog.dismiss()
                // 授权页面打开回调
            }
            it.loginBtnClickedCallback {
                // 点击登录按钮回调
                clickLogin = true
                dialog.show()
            }
            it.pageCloseCallback {
                // 授权页面关闭回调
              /*  if (!clickOtherLogin && !clickLogin) {
                    finish()
                }*/
            }
        }
        val builder = UiSettings.Builder()
        builder.setSwitchAccText("其他方式登录")
        builder.setLoginBtnTextSize(14)
            .setLoginBtnImgId(R.drawable.login_btn_img)
            .setAgreementColorId(R.color.purple_500)

        builder.setImmersiveTheme(true)
        builder.setImmersiveStatusTextColorBlack(true)
        builder.setCusAgreementNameId1("《用户协议》")
        val url_Agreement: String = AppClient.termsUse
        builder.setCusAgreementUrl1(url_Agreement)
        builder.setCusAgreementNameId2("《隐私协议》")
        val url_PrivacyPolicy: String = AppClient.privacy
        builder.setCusAgreementUrl2(url_PrivacyPolicy)

        FlyVerify.setUiSettings(builder.build())

        FlyVerify.verify(object : VerifyCallback() {
            override fun onComplete(p0: VerifyResult?) {

                if (p0 != null) {
                    notificationService(p0)
                }
            }

            override fun onFailure(p0: VerifyException?) {

                if (p0?.code != 6119167) {
                    p0?.judgeType()?.showToast()
                }
                changeChildViewStatus(false)
            }

            override fun onOtherLogin() {

              /*  var bundle = Bundle();
                bundle.putBoolean("isSec", false)

                var intent = Intent(this@LoginActivity, LoginActivity::class.java);
                intent.putExtras(bundle)
                startActivity(intent)*/
                binding.loginLlContent.visibility = View.VISIBLE
            }

            override fun onUserCanceled() {

                finish()
            }
        })
    }

    private fun notificationService(verify: VerifyResult) {
        lifecycleScope.launch {
            userAction.secondVerify(verify).catch {
                it.judgeType().showToast()
                dialog.dismiss()
                changeChildViewStatus(false)
            }.collect {
                dialog.dismiss()
                when {
                    it.isNoResisterOrError() -> {
                        //注册
                        startActivity<FastRegisterActivity> {
                            putExtra(ExtraKeyFactory.fastRegisterPhone, it.res.phone)
                        }
                        finish()
                    }

                    it.goHandLogin() -> {
                        changeChildViewStatus(false)
                    }

                    it.isLogin == 1 -> {
                        it.userinfo.apply {
                            GlobalHome.inflateLoginInfo(this)
                            userAction.saveLoginResponse(this).first()
                        }
                        finish()
                    }

                    else -> {
                        "登录失败".showToast()
                    }
                }
            }
        }
    }
}