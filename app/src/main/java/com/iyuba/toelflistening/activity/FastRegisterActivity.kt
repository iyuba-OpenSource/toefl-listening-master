package com.iyuba.toelflistening.activity

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.bean.LoginBean
import com.iyuba.toelflistening.databinding.ActivityFastRegisterBinding
import com.iyuba.toelflistening.utils.ExtraKeyFactory
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.showPassWordEmpty
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.showUserNameEmpty
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.random.Random

class FastRegisterActivity : BaseActivity<ActivityFastRegisterBinding>() {
    private var phone=""

    override fun ActivityFastRegisterBinding.initBinding() {
        setTitleText("快捷注册")
        //19861978744
        phone=intent.getStringExtra(ExtraKeyFactory.fastRegisterPhone).toString()
        val username="user_${getRandomFour()}${phone.substring(7,phone.length)}"
        val password=phone.substring(5,phone.length)
        item= LoginBean(
            username = username,
            password = password,
            protocol = "11002"
        )
        defaultPassword.text= getDefaultNameOrPass(false,"手机号后六位")
        defaultUsername.text= getDefaultNameOrPass(true,username)
        submitRegister.setOnClickListener { submitRegister() }
    }

    private fun getDefaultNameOrPass(isName:Boolean,value:String):SpannableStringBuilder{
        val type=if (isName) R.string.default_username else R.string.default_password
        val content=resources.getString(type)+value
        val symbol="："
        val builder=SpannableStringBuilder(content)
        if (content.contains(symbol)){
            val symbolIndex=content.indexOf(symbol)
            val beforeSpan=ForegroundColorSpan(Color.BLACK)
            val afterSpan=ForegroundColorSpan(ContextCompat.getColor(this,R.color.modify_head))
            builder.apply {
                setSpan(beforeSpan,0,symbolIndex,Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                setSpan(afterSpan,symbolIndex+1,content.length,Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            }
        }
        return builder
    }

    private fun getRandomFour()= with(StringBuilder()){
        repeat(4) {
            val ran = Random.nextInt(9)
            append(ran)
        }
        toString()
    }

    private fun submitRegister(){
        binding.apply {
            fastNameLayout.showUserNameEmpty(flag = item!!.isUserNameEmpty()){
                fastWordLayout.showPassWordEmpty(item!!.isPasswordEmpty()){
                    lifecycleScope.launch {
                        userAction.fastRegister(phone,item!!).onStart {
                            showLoad()
                        }.catch {
                            dismissLoad()
                            it.judgeType()?.showToast()
                        }.collect{
                            dismissLoad()
                            when (it.result) {
                                "111" -> {
                                    GlobalHome.inflateLoginInfo(it)
                                    userAction.saveLoginResponse(it).collect()
                                    "注册成功".showToast()
                                    finish()
                                }
                                "112" -> "账号已存在".showToast()
                                else -> it.message.showToast()
                            }
                        }
                    }
                }
            }
        }
    }

}