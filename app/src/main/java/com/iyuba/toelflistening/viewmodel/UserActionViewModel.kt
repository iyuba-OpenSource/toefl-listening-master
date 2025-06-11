package com.iyuba.toelflistening.viewmodel

import android.util.Base64
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import cn.fly.verify.datatype.VerifyResult
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.Repository
import com.iyuba.toelflistening.bean.AddScoreResponse
import com.iyuba.toelflistening.bean.CustomerServiceItem
import com.iyuba.toelflistening.bean.GroupRankItem
import com.iyuba.toelflistening.bean.GroupRankResponse
import com.iyuba.toelflistening.bean.LoginBean
import com.iyuba.toelflistening.bean.LoginResponse
import com.iyuba.toelflistening.bean.LogoutUserResponse
import com.iyuba.toelflistening.bean.QqResponse
import com.iyuba.toelflistening.bean.RankItem
import com.iyuba.toelflistening.bean.RankResponse
import com.iyuba.toelflistening.bean.RequestPayResponse
import com.iyuba.toelflistening.bean.SecondVerifyResponse
import com.iyuba.toelflistening.bean.SelfResponse
import com.iyuba.toelflistening.bean.SignResponse
import com.iyuba.toelflistening.bean.UploadPhotoResponse
import com.iyuba.toelflistening.dao.paging.RankStudyPaging
import com.iyuba.toelflistening.dao.paging.RankTopicPaging
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.OtherUtils
import com.iyuba.toelflistening.utils.changeEncode
import com.iyuba.toelflistening.utils.clearSelf
import com.iyuba.toelflistening.utils.net.FlowResult
import com.iyuba.toelflistening.utils.nowTime
import com.iyuba.toelflistening.utils.putUserId
import com.iyuba.toelflistening.utils.signDate
import com.iyuba.toelflistening.utils.toMd5
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber

/**
苏州爱语吧科技有限公司
 */
class UserActionViewModel : BaseViewModel() {
    private val reUrl = "http://api.iyuba.com.cn/v2/api.iyuba"
    private val dollarAfterUrl = "http://vip.iyuba.cn/notifyAliNew.jsp"
    private val format = "json"
    private val platform = "android"
    suspend fun login(login: LoginBean): Flow<LoginResponse> {
        dataMap.clearSelf().apply {
            put("username", login.username.changeEncode())
            put("password", login.password.toMd5())
            put("app", AppClient.appName)
            put("token", login.token)
            put("format", format)
            put("appid", AppClient.appId.toString())
            put("protocol", "11001")
            put("sign", login.getSign())
        }
        return Repository.login(reUrl, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    suspend fun getRegisterStatus(phone: String): Flow<LoginResponse> {
        dataMap.clearSelf().apply {
            put("protocol", "10009")
            put("username", phone)
            put("format", format)
        }
        return Repository.getRegisterStatus(reUrl, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    suspend fun secondVerify(verify: VerifyResult): Flow<SecondVerifyResponse> {
        val secondVerifyUrl = "http://api.iyuba.com.cn/v2/api.iyuba"
        dataMap.clearSelf().apply {
            put("protocol", "10010")
            put("appId", AppClient.appId.toString())
            put("appkey", "1bda93269b8c8")
            put("opToken", verify.opToken.changeEncode())
            put("operator", verify.operator.changeEncode())
            put("token", verify.token.changeEncode())
        }
        return Repository.secondVerify(secondVerifyUrl, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    suspend fun fastRegister(phone: String, item: LoginBean): Flow<LoginResponse> {
        dataMap.clearSelf().apply {
            put("app", AppClient.appName)
            put("appid", AppClient.appId.toString())
            put("format", format)
            put("mobile", phone)
            put("password", item.password.toMd5())
            put("platform", platform)
            put("protocol", item.protocol)
            put("sign", item.getSign())
            put("username", item.username.changeEncode())
        }
        return Repository.register(reUrl, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    suspend fun register(login: LoginBean, phone: String): Flow<LoginResponse> {
        dataMap.clearSelf().apply {
            put("protocol", login.protocol)
            put("username", login.username.changeEncode())
            put("format", format)
            put("appid", AppClient.appId.toString())
            put("app", AppClient.appName)
            put("sign", login.getSign())
            put("mobile", phone)
            put("password", login.password.toMd5())
        }
        return Repository.register(reUrl, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    suspend fun requestPayVip(
        widTotalFee: String,
        amount: String,
        productId: String,
        cate: String,
        widBody: String
    ): Flow<RequestPayResponse> {
        val uid = GlobalHome.userInfo.uid.toString()
        val code = "${uid}iyuba${nowTime()}".toMd5()
        dataMap.clearSelf().apply {
            put("app_id", AppClient.appId.toString())
            put("userId", uid)
            put("code", code)
            put("WIDtotal_fee", widTotalFee)
            put("amount", amount)
            put("product_id", productId)
            put("WIDbody", widBody)
            put("WIDsubject", cate.changeEncode())
        }
        return Repository.requestPayVip(payUrl + "alipay.jsp", dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }


    suspend fun payVip(data: String) = Repository.payVip(dollarAfterUrl, data).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
        Timber.d("异常")
    }


    suspend fun refreshSelf(id: String): Flow<SelfResponse> {
        val protocol = "20001"
        dataMap.clearSelf().apply {
            put("platform", platform)
            put("format", format)
            put("protocol", protocol)
            put("id", id)
            put("myid", id)
            put("appid", AppClient.appId.toString())
            put("sign", "${protocol}${id}iyubaV2".toMd5())
        }
        return Repository.refreshSelf(reUrl, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    suspend fun logoutUser(login: LoginBean): Flow<LogoutUserResponse> {
        val protocol = "11005"
        login.protocol = protocol
        dataMap.clearSelf().apply {
            put("username", login.username.changeEncode())
            put("password", login.password.toMd5())
            put("format", format)
            put("protocol", protocol)
            put("sign", login.getSign())
        }
        return Repository.logoutUser(reUrl, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    suspend fun uploadUserHeadPhoto(part: MultipartBody.Part): Flow<UploadPhotoResponse> {
        val url = "http://api.${OtherUtils.iyuba_com}/v2/avatar?uid=${GlobalHome.userInfo.uid}"
        return Repository.uploadUserHeadPhoto(url, part).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    val signResult = MutableSharedFlow<FlowResult<SignResponse>>()
    fun signToday() {
        dataMap.apply {
            clear()
            putUserId("uid")
            put("day", getDayDistance().toString())
            put("flg", "1")
        }
        viewModelScope.launch {
            Repository.signEveryDay(signUrl, dataMap).onStart {
                signResult.emit(FlowResult.Loading())
            }.catch {
                signResult.emit(FlowResult.Error(it))
            }.collect {
                signResult.emit(FlowResult.Success(it))
            }
        }
    }

    val shareWechatMomentsResult = MutableSharedFlow<FlowResult<AddScoreResponse>>()

    fun shareWechatMoments() {
        val dateString = signDate()
        val flag = Base64.encodeToString(dateString.toByteArray(), Base64.NO_WRAP)
        dataMap.apply {
            clear()
            put("srid", "81")
            putUserId("uid")
            putAppId()
            put("mobile", "1")
            put("flag", flag)
        }
        viewModelScope.launch {
            Repository.shareAddScore(shareContentUrl, dataMap).onStart {
                shareWechatMomentsResult.emit(FlowResult.Loading())
            }.catch {
                shareWechatMomentsResult.emit(FlowResult.Error(it))
            }.collect {
                shareWechatMomentsResult.emit(FlowResult.Success(it))
            }
        }
    }

    fun changeLocalHead(url: String) = Repository.changeLocalHead(url).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
        Timber.d("异常")
    }
    fun updateNickName(nickName: String) = Repository.updateNickName(nickName).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
        Timber.d("异常")
    }
    fun getLoginInfo() = Repository.getLoginInfo().catch {//需要声明catch 以防止在没有网络的情况下，崩溃
        Timber.d("异常")
    }
    fun exitLogin() = Repository.exitLogin().catch {//需要声明catch 以防止在没有网络的情况下，崩溃
        Timber.d("异常")
    }
    fun saveLoginResponse(login: LoginResponse) = Repository.saveLoginResponse(login).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
        Timber.d("异常")
    }
    fun saveSelf(self: SelfResponse) = Repository.saveSelf(self).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
        Timber.d("异常")
    }


    private fun getRankingByType(flag: Boolean) = Pager(
        config = PagingConfig(
            pageSize = 30,
            initialLoadSize = 15
        ),
        pagingSourceFactory = { RankTopicPaging(flag) }
    ).flow

    private val topicRankResult = MutableSharedFlow<FlowResult<PagingData<RankItem>>>()
    private val testRankResult = MutableSharedFlow<FlowResult<PagingData<RankItem>>>()

    private val topicErrorRankResult = MutableSharedFlow<RankResponse>()
    private val testErrorRankResult = MutableSharedFlow<RankResponse>()

    fun judgeTopicErrorIn(flag: Boolean) = if (flag) topicErrorRankResult else testErrorRankResult
    fun judgeTopicRankIn(flag: Boolean) = if (flag) topicRankResult else testRankResult


    fun loadTopicRank(flag: Boolean) {
        viewModelScope.launch {
            val date = signDate()
            val errorFlow = judgeTopicErrorIn(flag)
            val rankFlow = judgeTopicRankIn(flag)
            getTopicRanking(date = date, flag = flag).flatMapConcat {
                if (it.data.isEmpty()) {
                    errorFlow.emit(it)
                    rankFlow.emit(FlowResult.Error(Throwable()))
                }
                getRankingByType(flag)
            }.catch {
                rankFlow.emit(FlowResult.Error(it))
            }.collect {
                rankFlow.emit(FlowResult.Success(it))
            }
        }
    }

    private fun getTopicRanking(
        start: String = "0",
        total: String = "30",
        date: String,
        flag: Boolean
    ): Flow<RankResponse> {
        val topicId = "0"
        val sign = with(StringBuilder()) {
            append(GlobalHome.userInfo.uid)
            append(AppClient.appName)
            append(topicId)
            append(start)
            append(total)
            append(date)
            toString().toMd5()
        }
        dataMap.apply {
            clear()
            putUserId("uid")
            put("type", "D")
            put("start", start)
            put("total", total)
            put("sign", sign)
            put("topic", AppClient.appName)
            put("topicid", topicId)
            put("shuoshuotype", "4")
        }
        val url = if (flag) GlobalHome.topicRankUrl else GlobalHome.testRankUrl
        return Repository.getTopicRankingCount(url, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    private fun getStudyRankingByType(flag: Boolean) = Pager(
        config = PagingConfig(
            pageSize = 30,
            initialLoadSize = 30
        ),
        pagingSourceFactory = { RankStudyPaging(flag) }
    ).flow

    private val studyErrorRankResult = MutableSharedFlow<GroupRankResponse>()
    private val listenErrorRankResult = MutableSharedFlow<GroupRankResponse>()
    private val studyRankResult = MutableSharedFlow<FlowResult<PagingData<GroupRankItem>>>()
    private val listenRankResult = MutableSharedFlow<FlowResult<PagingData<GroupRankItem>>>()

    fun judgeRankFlow(flag: Boolean) = (if (flag) studyRankResult else listenRankResult)
    fun judgeErrorFlow(flag: Boolean) = (if (flag) studyErrorRankResult else listenErrorRankResult)


    fun loadStudyListenRank(flag: Boolean) {
        viewModelScope.launch {
            val rankFlow = judgeRankFlow(flag)
            val errorFlow = judgeErrorFlow(flag)
            val date = signDate()
            getStudyListenRanking(date = date, flag = flag).flatMapConcat {
                if (it.data.isEmpty()) {
                    errorFlow.emit(it)
                    rankFlow.emit(FlowResult.Error(Throwable()))
                }
                getStudyRankingByType(flag)
            }.collect {
                rankFlow.emit(FlowResult.Success(it))
            }
        }
    }

    /**
     * 获取(学习||听力)排行
     * */
    private fun getStudyListenRanking(
        start: String = "0",
        total: String = "30",
        date: String,
        flag: Boolean = false
    ): Flow<GroupRankResponse> {
        val type = "D"
        val sign = with(StringBuilder()) {
            append(GlobalHome.userInfo.uid)
            append(type)
            append(start)
            append(total)
            append(date)
            toString().toMd5()
        }
        val mode = (if (flag) "all" else "listening")
        dataMap.apply {
            clear()
            putUserId("uid")
            put("type", type)
            put("start", start)
            put("total", total)
            put("sign", sign)
            put("mode", mode)
        }
        return Repository.getStudyListenRankingCount(GlobalHome.studyRankUrl, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    val rankIndexFlow = MutableStateFlow(0)

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------
     * */
    val initResult = MutableSharedFlow<Pair<Boolean, Boolean>>(10, 10)

    fun initSomeThing() {
        //第一个为是否同意隐私，第二个为是否记载广告
        viewModelScope.launch {
            Repository.isFirstLogin().flatMapMerge { agree ->
                Repository.getLoginInfo().catch {//需要声明catch 以防止在没有网络的情况下，崩溃
                    Timber.d("异常")
                }.flatMapConcat {
                    GlobalHome.inflateLoginInfo(it)
                    flow { emit(Pair(agree, agree || it.isVip())) }
                }
            }.catch {//需要声明catch 以防止在没有网络的情况下，崩溃
                Timber.d("异常")
            }.collect {
                initResult.emit(it)
            }
        }
    }

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------
     * */
    private val qqResponse = MutableSharedFlow<FlowResult<QqResponse>>()
    val lastQqResponse = qqResponse.asSharedFlow()
    fun requestQQGroup() {
        dataMap.apply {
            clear()
            put("type", deviceName)
            putUserId()
            putAppId("appId")
        }
        viewModelScope.launch {
            Repository.requestQQGroup(qqUrl, dataMap).catch {
                qqResponse.emit(FlowResult.Error(it))
            }.collect {
                qqResponse.emit(FlowResult.Success(it))
            }
        }
    }

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------
     * */
    private val customerResponse = MutableSharedFlow<FlowResult<CustomerServiceItem>>()
    val lastCustomer = customerResponse.asSharedFlow()
    fun requestCustomerService() {
        viewModelScope.launch {
            Repository.requestCustomerService(customerUrl).catch {
                customerResponse.emit(FlowResult.Error(it))
            }.collect {
                val data = it.data
                if (data.isNotEmpty()) {
                    customerResponse.emit(FlowResult.Success(data.first()))
                } else {
                    customerResponse.emit(FlowResult.Error(Throwable()))
                }
            }
        }
    }
}