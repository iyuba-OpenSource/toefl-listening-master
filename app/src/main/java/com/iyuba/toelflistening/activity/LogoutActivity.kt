package com.iyuba.toelflistening.activity

import android.graphics.Paint
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.bean.LoginBean
import com.iyuba.toelflistening.databinding.ActivityLogoutBinding
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.skipWeb
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LogoutActivity : BaseActivity<ActivityLogoutBinding>(),View.OnClickListener {
    private val log=LoginBean()
    override fun ActivityLogoutBinding.initBinding() {
        this.loginBean= log
        GlobalHome.userInfo.nickname.apply {
            log.username=this
            binding.nowUserName= "当前用户名为$this"
        }
        binding.verifyPassword.setOnClickListener(this@LogoutActivity)
        binding.forgotModifyUsername.setOnClickListener(this@LogoutActivity)
        binding.forgotModifyUsername.paint.flags = Paint.UNDERLINE_TEXT_FLAG
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.forgot_modify_username -> {
                val forgotPassword="http://m.iyuba.cn/m_login/inputPhonefp.jsp"
                skipWeb(forgotPassword)
            }
            R.id.verify_password -> logOutUser()
        }
    }
    private fun logOutUser(){
        lifecycleScope.launch {
            userAction.logoutUser(log).catch {
                it.judgeType().showToast()
            }.collect {
                if (it.isSuccess()) {
                    "注销成功".showToast()
                    GlobalHome.clearUserInfo()
                    userAction.exitLogin().collect {}
                    finish()
                } else {
                    "注销失败".showToast()
                }
            }
        }
    }

}