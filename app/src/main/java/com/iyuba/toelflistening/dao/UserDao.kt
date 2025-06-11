package com.iyuba.toelflistening.dao

import android.content.Context
import com.google.gson.Gson
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.bean.LoginResponse
import com.iyuba.toelflistening.bean.SelfResponse

/**
苏州爱语吧科技有限公司
 */
object UserDao {
    private const val loginResponse = "loginResponse"
    private val defaultLogin = Gson().toJson(LoginResponse())
    private const val firstLogin = "firstLogin"
    private val sharedPreferences = AppClient.context.getSharedPreferences(AppClient.appName, Context.MODE_PRIVATE)

    fun isFirstLogin(): Boolean {
        return sharedPreferences.getBoolean(firstLogin, true)
    }

    fun saveFirstLogin(isFirstLogin: Boolean) :Boolean{
        val edit = sharedPreferences.edit()
        edit.putBoolean(firstLogin, isFirstLogin)
        return edit.commit()
    }

    fun getLoginResponse(): LoginResponse {
        val login = sharedPreferences.getString(loginResponse, defaultLogin)
        return Gson().fromJson(login, LoginResponse::class.java)
    }

    fun saveLoginResponse(login: LoginResponse):Boolean {
        val json = Gson().toJson(login)
        val edit = sharedPreferences.edit()
        edit.putString(loginResponse, json)
        edit.apply()
        return true
    }
    fun exitLogin() =saveLoginResponse(LoginResponse())
    fun saveSelf(self: SelfResponse):Boolean{
        val result=getLoginResponse()
        result.self=self
        return saveLoginResponse(result)
    }
    fun updateUserHead(newHead:String):Boolean{
        val log = getLoginResponse()
        log.imgSrc=newHead
        return saveLoginResponse(log)
    }

    fun updateNickName(newName:String):Boolean{
        val log = getLoginResponse()
        log.nickname=newName
        return saveLoginResponse(log)
    }
}