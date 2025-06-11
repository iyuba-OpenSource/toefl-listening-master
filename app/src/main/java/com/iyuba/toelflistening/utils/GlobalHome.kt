package com.iyuba.toelflistening.utils

import com.iyuba.module.user.IyuUserManager
import com.iyuba.toelflistening.bean.EvaluationSentenceDataItem
import com.iyuba.toelflistening.bean.LoginResponse
import com.iyuba.toelflistening.bean.RankResponse

/**
苏州爱语吧科技有限公司
 */
class GlobalHome {
    companion object {
        var videoUrl = ""
        var showEvaluationHint = true
        var userInfo = LoginResponse()
        val evaluationMap = mutableMapOf<Int, List<EvaluationSentenceDataItem>>()

        var titleNum = 0
        var rankResponse = RankResponse(data = mutableListOf())
        var exerciseResultBack = true
        fun inflateLoginInfo(info: LoginResponse) {
            userInfo = info
            IyuUserManager.getInstance().currentUser = info.convertOtherUser()
        }

        fun isLogin() = userInfo.isSuccess()

        fun getUid(): Int {

            return userInfo.uid;
        }

        fun isVip(): Boolean {

            if (userInfo.vipStatus != 0) {

                var expireTime = userInfo.expireTime.toString();
                if (userInfo.expireTime.toString().length == 10) {

                    expireTime += "000"
                }
                return expireTime.toLong() >= System.currentTimeMillis()
            } else {

                return false;
            }
        }

        fun clearUserInfo() {
            IyuUserManager.getInstance().logout()
            userInfo = LoginResponse()
        }

        val topicRankUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getTopicRanking.jsp"
        val testRankUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getTestRanking.jsp"
        val studyRankUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getStudyRanking.jsp"


        var rankTopicResponse = RankResponse(data = emptyList())

        var wordPagingEmpty = false

        const val groupId = 9933

        var wordPageCounts = 0
    }
}