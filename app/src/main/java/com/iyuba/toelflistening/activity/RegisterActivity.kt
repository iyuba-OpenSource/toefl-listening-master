package com.iyuba.toelflistening.activity

import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import cn.smssdk.EventHandler
import cn.smssdk.SMSSDK
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.bean.LoginBean
import com.iyuba.toelflistening.bean.RegisterBean
import com.iyuba.toelflistening.databinding.ActivityRegisterBinding
import com.iyuba.toelflistening.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class RegisterActivity : BaseActivity<ActivityRegisterBinding>(),View.OnClickListener {
    private val bean=RegisterBean()
    private val login=LoginBean(protocol = "11002")
    private lateinit var eventHandler: EventHandler
    private var count by Delegates.notNull<Int>()
    private var flag =false
    private val timing=0
    private val submitSuccess=1
    private val getSuccess=2
    private val error=3
    private val handler=Handler(Looper.myLooper()!!){
        when(it.what){
            submitSuccess->{
                flag=true
                "提交验证码成功".showToast()
                dismissLoad()
            }
            getSuccess-> "获取验证码成功".showToast()
            error-> {
                "获取验证码失败".showToast()
                dismissLoad()
            }
            timing->{
                val text="重新发送($count S)"
                binding.sendVerifyCode.text=text
                if (count==0){
                    binding.sendVerifyCode.isEnabled=true
                    binding.sendVerifyCode.text=resources.getString(R.string.send_verify_code)
                }
            }
        }
        true
    }
    override fun ActivityRegisterBinding.initBinding() {
        setTitleText("注册")
        siteSuperLink()
        binding.registerBean= bean
        binding.loginBean=login
        binding.registerBt.setOnClickListener (this@RegisterActivity)
        binding.sendVerifyCode.setOnClickListener (this@RegisterActivity)
        eventHandler = object : EventHandler() {
            override fun afterEvent(event: Int, result: Int, data: Any) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    when (event) {
                        SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE -> handler.sendEmptyMessage(submitSuccess)
                        SMSSDK.EVENT_GET_VERIFICATION_CODE -> handler.sendEmptyMessage(getSuccess)
                    }
                }else{
                    handler.sendEmptyMessage(error)
                }
            }
        }
        SMSSDK.registerEventHandler(eventHandler)
        binding.phoneInputCode.editText?.addTextChangedListener {
            val content=it.toString()
            if (content.length==6){
                SMSSDK.submitVerificationCode("86",bean.phone,bean.verifyCode)
            }
        }
    }

    private fun siteSuperLink(){
        val content=resources.getString(R.string.read_agree)
        val builder=SpannableStringBuilder()
        builder.append(content)
        val start=content.indexOf("《")
        builder.setSpan(object : ClickableSpan (){
            override fun onClick(p0: View) {
                skipWeb(AppClient.privacy)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color=ContextCompat.getColor(this@RegisterActivity,R.color.main)
                ds.isUnderlineText=true
            }

        },start,start+8,0)
        binding.readAgree.movementMethod=LinkMovementMethod.getInstance()
        binding.readAgree.setText(builder,TextView.BufferType.SPANNABLE)
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.send_verify_code->sendVerifyCode()
            R.id.register_bt->registerNew()
        }
    }
    private fun registerNew(){
        if (bean.checkPhone()){
            "手机号不正确".showToast()
            return
        }
        if (!bean.check){
            "请先同意用户隐私协议".showToast()
            return
        }
        if (!flag){
            "验证码验证失败".showToast()
            return
        }
        binding.usernameInputRegister.showUserNameEmpty(flag=login.isUserNameEmpty()) {
            binding.passwordInputRegister.showPassWordEmpty(login.isPasswordEmpty()) {
                lifecycleScope.launch {
                    userAction.register(login,bean.phone).catch {
                        it.judgeType().showToast()
                    }.collect {
                        if (it.isSuccess()) {
                            "注册成功".showToast()
                            userAction.saveLoginResponse(it).collect {}
                            finish()
                            return@collect
                        }
                        when (it.result) {
                            "112" -> "用户名已存在"
                            "113" -> "邮箱已被注册"
                            "114" -> "用户名长度错误"
                            "115" -> "手机号已被注册"
                            else -> it.message
                        }.showToast()

                    }
                }
            }
        }
    }
    private fun sendVerifyCode(){
        if (bean.checkPhone()){
            "手机号不正确".showToast()
            return
        }
        if (!bean.check){
            "请先同意用户隐私协议".showToast()
            return
        }
        lifecycleScope.launch {
            userAction.getRegisterStatus(bean.phone).catch {
                it.judgeType().showToast()
            }.collect {
                if (it.isNotRegistered()) {
                    flag = false
                    SMSSDK.getVerificationCode("86", bean.phone)
                    binding.sendVerifyCode.isEnabled = false
                    //重新发送倒计时
                    count = 60
                    while (count > 0) {
                        count--
                        delay(1000)
                        handler.sendEmptyMessage(timing)
                    }
                } else {
                    "手机号已经注册过".showToast()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}