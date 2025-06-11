package com.iyuba.toelflistening.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.utils.OtherUtils
import java.util.Calendar
import java.util.Locale

/**
苏州爱语吧科技有限公司
@Date:  2022/9/20
@Author:  han rong cheng
 */
abstract class BaseViewModel : ViewModel() {
    protected val dataMap = mutableMapOf<String, String>()
    protected val deviceName = Build.MANUFACTURER.trim().lowercase(Locale.getDefault())

    protected fun MutableMap<String, String>.putFormat() = put("format", "json")
    protected fun MutableMap<String, String>.putPlatform() = put("platform", "android")
    protected fun MutableMap<String, String>.putAppId(key: String = "appid") =
        put(key, AppClient.appId.toString())

    protected fun MutableMap<String, String>.putProtocol(value: String) = put("protocol", value)
    protected fun MutableMap<String, String>.putAppName(key: String = "type") =
        put(key, AppClient.appName)

    protected fun MutableMap<String, String>.putDeviceId(key: String = "DeviceId") =
        put(key, deviceName)

    //ConceptViewModel
    protected val wordUrl = "http://word.${OtherUtils.iyuba_cn}/words/"
    protected val customerUrl = "http://${OtherUtils.i_user_speech}japanapi/getJpQQ.jsp"
    protected val qqUrl = "http://m.${OtherUtils.iyuba_cn}/m_login/getQQGroup.jsp"
    protected val pdfUrl = "http://app.${OtherUtils.iyuba_cn}/iyuba/getConceptPdfFile.jsp"
    protected val shareContentUrl = "http://api.${OtherUtils.iyuba_cn}/credits/updateScore.jsp"
    protected val submitRecordUrl =
        "http://daxue.${OtherUtils.iyuba_cn}/ecollege/updateStudyRecordNew.jsp"
    protected val wordPDFUrl = "http://${OtherUtils.i_user_speech}management/getWordToPDF.jsp"

    //EvaluationViewModel
    protected val evaluationUrl = "http://${OtherUtils.i_user_speech}test/ai/"
    protected val pagingEvalUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getTopicRanking.jsp"
    protected val releaseEvalUrl = "http://voa.${OtherUtils.iyuba_cn}/voa/"
    protected val releaseSimple = "http://voa.${OtherUtils.iyuba_cn}/voa/UnicomApi"
    protected val correctSoundUrl = "http://word.${OtherUtils.iyuba_cn}/words/apiWordAi.jsp"
    protected val mergeUrl = "http://${OtherUtils.i_user_speech}test/merge/"

    //UserActionViewModel
    protected val payUrl = "http://vip.${OtherUtils.iyuba_cn}/"
    protected val operateUserUrl = "http://api.${OtherUtils.iyuba_com}/v2/api.iyuba"
    protected val secondVerifyUrl = "http://api.${OtherUtils.iyuba_com}/v2/api.iyuba"
    protected val signUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getMyTime.jsp"
    protected val exerciseUrl =
        "http://daxue.${OtherUtils.iyuba_cn}/ecollege/updateTestRecordNew.jsp"
    protected val testRecordUrl =
        "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getTestRecordDetail.jsp"
    protected val topicRankUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getTopicRanking.jsp"
    protected val studyRankUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getStudyRanking.jsp"
    protected val adUrl = "http://dev.${OtherUtils.iyuba_cn}/getAdEntryAll.jsp"


    fun getDayDistance(): Long {
        val old = Calendar.getInstance(Locale.CHINA)
        old.set(1970, 0, 1, 0, 0, 0)
        val now = Calendar.getInstance(Locale.CHINA)
        val intervalMilli = now.timeInMillis - old.timeInMillis
        return intervalMilli / (24 * 60 * 60 * 1000)
    }
}