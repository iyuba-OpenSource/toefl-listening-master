package com.iyuba.toelflistening.net

import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.bean.*
import okhttp3.MultipartBody
import retrofit2.http.*

/**
苏州爱语吧科技有限公司
 */
interface UserActionService {
    //登录
    @GET
    suspend fun login(
        @Url url: String,
        @QueryMap map:Map<String,String>
    ): LoginResponse

    //判断手机号是否已经注册
    @GET
    suspend fun getRegisterStatus(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ):LoginResponse

    //注册用户
    @GET
    suspend fun register(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ):LoginResponse

    //请求支付
    @GET
    suspend fun requestPayVip(
        @Url url:String,
        @QueryMap map:Map<String,String>
    ): RequestPayResponse

    //支付
    @GET
    suspend fun payVip(
        @Url url:String,
        @Query("data") data:String
    ): PayResponse

    //刷新用户信息
    @GET
    suspend fun refreshSelf(
        @Url url:String,
        @QueryMap map: Map<String, String>
    ): SelfResponse

    //注销
    @GET
    suspend fun logoutUser(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): LogoutUserResponse

    //更改用户头像
    @Multipart
    @POST
    suspend fun uploadPhoto(
        @Url url: String,
        @Part part: MultipartBody.Part
    ): UploadPhotoResponse

    //秒验登录
    @GET
    suspend fun secondVerify(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):SecondVerifyResponse

    //秒验注册
    suspend fun fastRegister(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):LoginResponse


    /**
     * 学习打卡
     * */
    @GET
    suspend fun signEveryDay(
        @Url url: String,
        @QueryMap map:Map<String,String>
    ):SignResponse



    /**
     * 打卡分享
     * */
    @GET
    suspend fun shareAddScore(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):AddScoreResponse

    /**
     * 口语排行榜
     * */
    @GET
    suspend fun getTopicRanking(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):RankResponse

    /**
     * 学习&&听力排行榜
     * */
    @GET
    suspend fun getStudyListenRanking(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):GroupRankResponse

    /**
     * 获取QQ群
     * */
    @GET
    suspend fun requestQQGroup(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ):QqResponse

    //请求客服QQ
    @GET
    suspend fun requestCustomerService(
        @Url url: String,
        @Query("appid") appid: Int=AppClient.appId,
    ):CustomerResponse
}