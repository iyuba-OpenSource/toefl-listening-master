package com.iyuba.toelflistening

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import com.iyuba.toelflistening.java.model.NetWorkManager
import com.umeng.commonsdk.UMConfigure
import org.litepal.LitePal
import timber.log.Timber

/**
苏州爱语吧科技有限公司
 */

class AppClient : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        //1爱语吧  2上海画笙   3爱语言
        private const val companyType = 1
        private const val appEncode = "%E6%89%98%E7%A6%8F%E5%90%AC%E5%8A%9B"
        const val privacy =
            "https://ai.iyuba.cn/api/protocolpri.jsp?apptype=$appEncode&company=$companyType"
        const val termsUse =
            "https://ai.iyuba.cn/api/protocoluse.jsp?apptype=$appEncode&company=$companyType"
        const val appName = "toefl"
        const val appId = 220

        const val adAppId = 2201

        const val WX_APPKEY = "wxd88cfd20b7f86ae0"

        //不显示微课的时间
        const val EXAMINE_TIME = "1181118406000"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            Timber.plant(Timber.DebugTree())
        }
        UMConfigure.preInit(this, "", "")

        LitePal.initialize(this)
        NetWorkManager.getInstance().init()
    }

    /**
     * 华为-张超男
     * 小米-李英杰
     * vivo-姜亚楠
     * OPPO/应用宝-孟裴瑞
     * 魅族-刘隽萌
     * 百度-张蕊
     * 360-黎佳林
     * 联想-李正红
     * 三星-施穆苏
     * */
}