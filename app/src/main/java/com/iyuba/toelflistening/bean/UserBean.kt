package com.iyuba.toelflistening.bean

import androidx.annotation.DrawableRes
import com.iyuba.module.user.User
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.utils.*
import personal.iyuba.personalhomelibrary.PersonalHome
import java.io.Serializable
import java.math.BigDecimal
import java.text.DecimalFormat

/**
苏州爱语吧科技有限公司
 */
data class LoginBean(
    var username: String = "",
    var password: String = "",
    val token: String = "",
    val format: String = "json",
    var protocol: String = "11001",
    var isAuto: Boolean = false
) {
    fun getSign() = (protocol + username + password.toMd5() + "iyubaV2").toMd5()
    fun isUserNameEmpty() = ((username.isEmpty()) || username.length <3)
    fun isPasswordEmpty() = ((password.isEmpty()) || password.length !in (6..15))
}

//---------------------------------------------------------------
data class LoginResponse(
    val Amount: String = "",
    val mobile: String = "",
    var message: String = "",
    val result: String = "",
    var uid: Int = -1,
    val isteacher: String = "",
    var expireTime: Long = 0,
    val money: String = "",
    val credits: Int = 0,
    val jiFen: Int = 0,
    var nickname: String = "",
    var vipStatus: Int = -1,
    var imgSrc: String = "",
    val email: String = "",
    var username: String = "登录/注册",
    var self: SelfResponse= SelfResponse()
) {
    fun isNotRegistered() = (result != "101")
    fun isEmpty() = (nickname.isEmpty())
    fun isSuccess() = (message == "success")
    fun getSign() = ("10012" + uid + "iyubaV2").toMd5()
    fun isVip()=(getUserType()!=self.generalUser)
    fun getUserType() = if (self.isVip()) {
        self.getUserType()
    }else if (isEmpty()||!self.judgeStatus(vipStatus)){
        self.generalUser
    }else {
        val time = "${expireTime}000".toLong()
        if (System.currentTimeMillis() > time) {
            self.generalUser
        } else {
            "会员到期:${time.timeStampDate()}"
        }
    }

    fun convertOtherUser()= with(User()){
        this.uid=this@LoginResponse.uid
        this.name=this@LoginResponse.username
        this.nickname=this@LoginResponse.nickname
        this.email=this@LoginResponse.email
        this.imgUrl=this@LoginResponse.imgSrc
        this.mobile=this@LoginResponse.mobile
        this.credit=this@LoginResponse.credits
        this.vipStatus=this@LoginResponse.vipStatus.toString()
        this.vipExpireTime=this@LoginResponse.expireTime
        this.iyubiAmount=this@LoginResponse.Amount.safeToInt()
        this.money=this@LoginResponse.money.safeToInt()
        this
    }

    fun initPersonalHome(){
        PersonalHome.setSaveUserinfo(uid,username,vipStatus.toString())
    }

}

//---------------------------------------------------------------
data class RegisterBean(
    var phone: String = "",
    var verifyCode: String = "",
    var check: Boolean = false
) {
    fun isNotEmpty() = (phone.isNotEmpty() && verifyCode.isNotEmpty())
    fun checkPhone() = (phone.length != 11)
}

//---------------------------------------------------------------
data class VipBean(val id: Int, val price: Int, val description: String) {
    fun realDescription() = "$description:￥$price"
}
//---------------------------------------------------------------
data class RequestPayResponse(
    val alipayTradeStr:String,
    val result:Int,
    val message:String
){
    fun isSuccess()=(result==200&&message=="Success"&&alipayTradeStr.isNotEmpty())
}
//---------------------------------------------------------------
data class PayResponse(val msg:String, val code:Int){
    fun isSuccess()=(msg=="Success"&&code==200)
}
//---------------------------------------------------------------
data class SelfResponse(
    val albums:String="",
    val gender:String="",
    val distance:String="",
    val blogs:String="",
    val middle_url:String="",
    val contribute:String="",
    val shengwang:String="",
    val bio:String="",
    val posts:String="",
    val relation:String="",
    val result:String="",
    val isteacher:String="",
    val credits:String="",
    val nickname:String="",
    val email:String="",
    val views:String="",
    val amount:String="",
    val follower:String="",
    val mobile:String="",
    val allThumbUp:String="",
    val icoins:String="",
    val message:String="",
    val friends:String="",
    val doings:String="",
    val expireTime:String="",
    val money:String="",
    val following:String="",
    val sharings:String="",
    var vipStatus:Int=-1,
    val username:String="",
    val generalUser:String="普通用户"
){
    fun realMiddleUrl()="http://static1.iyuba.cn/uc_server/$middle_url"
    fun isVip()=(getUserType()!=generalUser)
    fun judgeStatus(vip:Int)=(vip>0)
    fun getUserType() = if (judgeStatus(vipStatus)) {
        val time = "${expireTime}000".toLong()
        if (System.currentTimeMillis() > time) {
            generalUser
        } else {
            "会员到期:${time.timeStampDate()}"
        }
    } else {
        generalUser
    }
}
//---------------------------------------------------------------
data class LogoutUserResponse(val result: String, val message: String) {
    fun isSuccess() = (result == "101" && message == "success")
}
//---------------------------------------------------------------

data class UploadPhotoResponse(
    val status: Int,
    val jiFen: Int,
    val middleUrl: String="",
    val smallUrl: String="",
    val bigUrl: String="",
    val message: String=""
) {
    fun isSuccess() = (middleUrl.isNotEmpty() && smallUrl.isNotEmpty() && bigUrl.isNotEmpty())
}

//---------------------------------------------------------------

data class SecondVerifyResponse(
    val isLogin:Int,
    val userinfo:LoginResponse,
    val res:SecondVerifyChild
){
    /**
     * 进入快捷注册
     * */
    fun isNoResisterOrError() = (isLogin == 0 && res != null)
    /**
     * 手动登录
     * */
    fun goHandLogin() = (isLogin == 0 && (userinfo == null || res == null))

}
data class SecondVerifyChild(val valid:Boolean, var phone:String, val isValid:String)

//---------------------------------------------------------------

data class AllVipItem(
    val desc:String,
    @DrawableRes
    val resource:Int
)

data class BuyCurrency(
    val price: Float,
    @DrawableRes
    val image: Int,
    val iyuCount:Int
){
    val orderInfo get() = "购买${iyuCount}爱语币"
}


data class SignResponse(
    val ranking: String,
    val result: String,
    val sentence: String,
    val shareId: String,
    val totalDays: String,
    val totalDaysTime: String,
    val totalTime: String,
    val totalUser: String,
    val totalWord: String,
    val totalWords: String
): Serializable {

    val qrIconUrl get()="http://app.${OtherUtils.iyuba_cn}/android/androidDetail.jsp?u=${GlobalHome.userInfo.uid}&id=${AppClient.appId}&f=${shareId}"

    fun getOverPercent(): String = "超越了" + if (totalUser.isNotEmpty() && ranking.isNotEmpty()) {
        val carry = 1 - ranking.toDouble() / totalUser.toDouble()
        DecimalFormat("0.00").format(carry).replace("0.", "")
    } else {
        "0"
    } + "%的同学"

    fun getTodayWords()="今日学习单词${totalWord}个"
    fun getStudyDays()="已累计学习${totalDays}天"
}


data class AddScoreResponse(
    //此次获得积分
    val addcredit: String,
    //连续打卡天数
    val days: String,
    //此次得到红包的数量 ,单位是分
    val money: String,
    //200算成功,
    val result: String,
    //当money>0返回的是总金额,money=0返回的是总积分;
    val totalcredit: String
){
    fun getDialogDesc() = if (result == "200") {
            "打卡成功，您已连续打卡${days}天，" + if (money.safeToFloat() > 0) {
                "获得${money.safeToFloat().changeString()}元，总计${totalcredit.safeToFloat().changeString()}元，满十元可在\"爱语吧\"公众号提现"
            } else {
                "获得${addcredit}积分，总积分:${totalcredit}"
            }
        } else {
            "今日已打卡，重复打卡不能再次获取红包或积分哦！"
        }
}

data class GroupRankResponse(
    val myid: Int,
    val myimgSrc: String,
    val myranking: Int,
    val result: Int,
    val totalTime:Int,
    val totalWord:Int,
    val totalEssay:Int,
    val myname:String,
    val message:String,
    val data:List<GroupRankItem>
){
    fun realCount()="文章数：${totalEssay} \n单词数：${totalWord}"

    /**
     * 保留两位小数
     * */
    fun hourTime() :String{
        val a=((totalTime).toDouble() / 60 / 60)
        val b=BigDecimal(a)
        val c=b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
        return c.toString()+"小时"
    }
}

data class GroupRankItem(
    val uid: Int,
    val totalTime: Int,
    val totalWord: Int,
    val name: String,
    val ranking: Int,
    val sort: Int,
    val totalEssay: Int,
    val imgSrc: String
){
    fun realCount()="文章数：${totalEssay} \n单词数：${totalWord}"

    /**
     * 保留两位小数
     * */
    fun hourTime() :String{
        val a=((totalTime).toDouble() / 60 / 60)
        val b=BigDecimal(a)
        val c=b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
        return c.toString()+"小时"
    }
}

data class MergeResponse(
    var URL: String="",
    var message: String="",
    var result: String=""
    //某些天马行空的接口真是让人诟病
){
    fun isSuccess()=message.contains("success")
}

data class QqResponse(val message:String,val QQ:String,val key:String)

data class CustomerResponse(
    val data: List<CustomerServiceItem>,
    val result: Int
)
data class CustomerServiceItem(
    val editor: Int=0,
    val manager: Int=0,
    val technician: Int=0
)