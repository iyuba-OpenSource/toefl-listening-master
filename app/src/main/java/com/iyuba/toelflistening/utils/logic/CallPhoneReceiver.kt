package com.iyuba.toelflistening.utils.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.iyuba.toelflistening.bean.CallPhoneEvent
import org.greenrobot.eventbus.EventBus

/**
苏州爱语吧科技有限公司
@Date:  2022/8/26
@Author:  han rong cheng

 三星平台：【缺陷】接听电话时，听力练习持续播报
 */
class CallPhoneReceiver :BroadcastReceiver() {
    override fun onReceive(context: Context?, i: Intent?) {
        val action="android.intent.action.PHONE_STATE"
        if (i?.action==action){
            val state= i.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state==TelephonyManager.EXTRA_STATE_RINGING){
                //来电
                EventBus.getDefault().post(CallPhoneEvent())
            }
        }
    }
}