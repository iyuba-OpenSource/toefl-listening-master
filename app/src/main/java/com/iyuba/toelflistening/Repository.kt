package com.iyuba.toelflistening


import com.iyuba.toelflistening.bean.EvaluationSentenceDataItem
import com.iyuba.toelflistening.bean.LikeEvaluation
import com.iyuba.toelflistening.bean.LocalCollect
import com.iyuba.toelflistening.bean.LoginResponse
import com.iyuba.toelflistening.bean.QuestionDetailItem
import com.iyuba.toelflistening.bean.RankResponse
import com.iyuba.toelflistening.bean.ReDoBean
import com.iyuba.toelflistening.bean.SelfResponse
import com.iyuba.toelflistening.bean.TextItem
import com.iyuba.toelflistening.dao.AppDatabase
import com.iyuba.toelflistening.dao.UserDao
import com.iyuba.toelflistening.net.AdService
import com.iyuba.toelflistening.net.ServiceCreator
import com.iyuba.toelflistening.net.ToeflService
import com.iyuba.toelflistening.net.UserActionService
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.timeStampDate
import com.iyuba.toelflistening.utils.toMd5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.Date


object Repository {
    private val toeflService = ServiceCreator.create<ToeflService>()
    private val userActionService = ServiceCreator.create<UserActionService>()
    private val dao = AppDatabase.getDatabase(AppClient.context).collectDao()
    private val adService = ServiceCreator.create<AdService>()
    private val questionGroupDao = AppDatabase.getDatabase(AppClient.context).questionGroupDao()
    private val localSentenceDao = AppDatabase.getDatabase(AppClient.context).localSentenceDao()
    private val evaluationItemDao = AppDatabase.getDatabase(AppClient.context).evaluationItemDao()
    private val likeEvaluationDao = AppDatabase.getDatabase(AppClient.context).likeEvaluationDao()
    private val reDoDao = AppDatabase.getDatabase(AppClient.context).reDoDao()

    /**
     * ToeflService
     * */
    suspend fun requestToeflList() =
        flow { emit(toeflService.requestToeflList()) }.flowOn(Dispatchers.IO)

    suspend fun getAdEntryAll(flag:Int,appid:String,uid:String) =
        flow { emit(toeflService.requestAdEntryAll("http://dev.iyuba.cn/getAdEntryAll.jsp",flag,appid,uid)) }.flowOn(Dispatchers.IO)

    suspend fun requestQuestList(id: String) =
        flow { emit(toeflService.requestQuestList(id)) }.flowOn(Dispatchers.IO)

    suspend fun requestQuestDetail(id: String) =
        flow { emit(toeflService.requestQuestDetail(id)) }.flowOn(Dispatchers.IO)

    suspend fun changeCollectStatus(url: String, map: Map<String, String>) =
        flow { emit(toeflService.changeCollectStatus(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun pickWord(url: String, map: Map<String, String>) =
        flow { emit(toeflService.pickWord(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun requestStrangenessWord(url: String, map: Map<String, String>) =
        toeflService.requestStrangenessWord(url, map)

    suspend fun getRankData(url: String, currentPage: Int = 0, pageSize: Int = 0): RankResponse {
        val userId = GlobalHome.userInfo.uid.toString()
        val topicId = GlobalHome.titleNum
        val topic = AppClient.appName
        val dateFormat = Date().time.timeStampDate("yyyy-MM-dd")
        val sign = (userId + topic + topicId + currentPage + pageSize + dateFormat).toMd5()
        val start = (currentPage * pageSize).toString()
        val map = mutableMapOf<String, String>().apply {
            put("topic", topic)
            put("topicid", topicId.toString())
            put("uid", userId)
            put("start", start)
            put("total", pageSize.toString())
            put("sign", sign)
            put("type", "D")
        }
        return toeflService.getRankData(url, map)
    }

    suspend fun getWorksByUserId(url: String, map: Map<String, String>) =
        flow { emit(toeflService.getWorksByUserId(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun likeEvaluation(url: String, map: Map<String, String>) =
        flow { emit(toeflService.likeEvaluation(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun evaluationSentence(url: String, body: RequestBody) =
        flow { emit(toeflService.evaluationSentence(url, body)) }.flowOn(Dispatchers.IO)

    suspend fun releaseSimple(url: String, map: Map<String, String>) =
        flow { emit(toeflService.releaseSimple(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun submitStudyRecord(url: String, map: Map<String, String>) =
        flow { emit(toeflService.submitStudyRecord(url, map)) }

    suspend fun submitExerciseRecord(url: String, map: Map<String, String>) =
        flow { emit(toeflService.submitExerciseRecord(url, map)) }

    suspend fun downloadVideo(url: String) = toeflService.downloadVideo(url)
    suspend fun mergeVideos(url: String, map: Map<String, String>) =
        flow { emit(kotlin.runCatching { toeflService.mergeVideos(url, map) }) }

    /**
     * UserActionService
     * */
    suspend fun login(url: String, map: Map<String, String>) =
        flow { emit(userActionService.login(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun uploadUserHeadPhoto(url: String, part: MultipartBody.Part) =
        flow { emit(userActionService.uploadPhoto(url, part)) }.flowOn(Dispatchers.IO)

    suspend fun getRegisterStatus(url: String, map: Map<String, String>) =
        flow { emit(userActionService.getRegisterStatus(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun secondVerify(url: String, map: Map<String, String>) =
        flow { emit(userActionService.secondVerify(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun fastRegister(url: String, map: Map<String, String>) =
        flow { emit(userActionService.fastRegister(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun register(url: String, map: Map<String, String>) =
        flow { emit(userActionService.register(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun requestPayVip(url: String, map: Map<String, String>) =
        flow { emit(userActionService.requestPayVip(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun payVip(url: String, data: String) =
        flow { emit(userActionService.payVip(url, data)) }.flowOn(Dispatchers.IO)

    suspend fun refreshSelf(url: String, map: Map<String, String>) =
        flow { emit(userActionService.refreshSelf(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun logoutUser(url: String, map: Map<String, String>) =
        flow { emit(userActionService.logoutUser(url, map)) }.flowOn(Dispatchers.IO)

    suspend fun signEveryDay(url: String, map: Map<String, String>) =
        flow { emit(userActionService.signEveryDay(url, map)) }

    suspend fun shareAddScore(url: String, map: Map<String, String>) =
        flow { emit(userActionService.shareAddScore(url, map)) }

    suspend fun getTopicRanking(url: String, map: Map<String, String>) =
        userActionService.getTopicRanking(url, map)

    fun getTopicRankingCount(url: String, map: Map<String, String>) =
        flow { emit(userActionService.getTopicRanking(url, map)) }

    suspend fun getStudyListenRanking(url: String, map: Map<String, String>) =
        userActionService.getStudyListenRanking(url, map)

    fun getStudyListenRankingCount(url: String, map: Map<String, String>) =
        flow { emit(userActionService.getStudyListenRanking(url, map)) }

    fun requestQQGroup(url: String, map: Map<String, String>) =
        flow { emit(userActionService.requestQQGroup(url, map)) }

    fun requestCustomerService(url: String) =
        flow { emit(userActionService.requestCustomerService(url)) }

    suspend fun releaseMerge(url: String, map: Map<String, String>) =
        flow { emit(runCatching { toeflService.releaseMerge(url, map) }) }

    suspend fun requestTestRecord(url: String, map: Map<String, String>) =
        flow { emit(toeflService.requestTestRecord(url, map)) }

    fun requestStrangePdf(url: String, map: Map<String, String>) =
        flow { emit(toeflService.requestStrangePdf(url, map)) }

    fun correctSound(url: String, map: Map<String, String>) =
        flow { emit(toeflService.correctSound(url, map)) }

    fun requestAdType(url: String, map: Map<String, String>) =
        flow { emit(adService.requestAdType(url, map)) }

    /**
     * SharedPreferences
     * */
    fun saveFirstLogin(isFirstLogin: Boolean) =
        flow { emit(UserDao.saveFirstLogin(isFirstLogin)) }.flowOn(Dispatchers.IO)

    fun isFirstLogin() = flow { emit(UserDao.isFirstLogin()) }.flowOn(Dispatchers.IO)
    fun getLoginInfo() = flow { emit(UserDao.getLoginResponse()) }.flowOn(Dispatchers.IO)
    fun exitLogin() = flow { emit(UserDao.exitLogin()) }.flowOn(Dispatchers.IO)
    fun saveSelf(self: SelfResponse) = flow { emit(UserDao.saveSelf(self)) }.flowOn(Dispatchers.IO)
    fun saveLoginResponse(login: LoginResponse) =
        flow { emit(UserDao.saveLoginResponse(login)) }.flowOn(Dispatchers.IO)

    fun changeLocalHead(url: String) =
        flow { emit(UserDao.updateUserHead(url)) }.flowOn(Dispatchers.IO)

    fun updateNickName(nickName: String) =
        flow { emit(UserDao.updateNickName(nickName)) }.flowOn(Dispatchers.IO)

    /***/
    fun insertWord(collect: LocalCollect) =
        flow { emit(dao.insertWord(collect).toString()) }.flowOn(Dispatchers.IO)

    fun updateWord(isCollect: Boolean, word: String) =
        flow { emit(dao.updateWord(isCollect, word).toString()) }.flowOn(Dispatchers.IO)

    fun selectCollectByWord(word: String) = dao.selectCollectByWord(word)

    fun selectByType(type: String, position: Int) =
        flow { emit(questionGroupDao.selectByType(type, position)) }.flowOn(Dispatchers.IO)

    fun insertSimple(bean: List<QuestionDetailItem>) =
        flow { emit(questionGroupDao.insertSimple(bean)) }.flowOn(Dispatchers.IO)

    fun insertSentence(sentenceResult: List<TextItem>) =
        flow { emit(localSentenceDao.insertSentence(sentenceResult)) }.flowOn(Dispatchers.IO)

    fun selectSentenceList(userId: Int, titleNum: String) =
        flow { emit(localSentenceDao.selectSentenceList(userId, titleNum)) }.flowOn(Dispatchers.IO)

    fun updateEvaluationSentenceItemStatus(userId: Int, voaId: Int, item: TextItem) = flow {
        emit(
            localSentenceDao.updateEvaluationSentenceItemStatus(
                userId,
                voaId,
                item.senIndex.toInt(),
                item.onlyKay,
                item.success,
                item.fraction,
                item.selfVideoUrl
            )
        )
    }.flowOn(Dispatchers.IO)

    fun selectSimpleEvaluation(voaId: String, idIndex: Int, userId: String) =
        flow { emit(localSentenceDao.selectSimpleEvaluation(voaId, idIndex, userId)[0]) }.flowOn(
            Dispatchers.IO
        )

    fun insertEvaluation(resultList: List<EvaluationSentenceDataItem>) =
        evaluationItemDao.insertEvaluation(resultList)

    fun selectEvaluationList(userId: Int, groupNum: Int) =
        flow { emit(evaluationItemDao.selectEvaluationList(userId, groupNum)) }

    fun selectEvaluationByKey(onlyKey: String) =
        flow { emit(evaluationItemDao.selectEvaluationByKey(onlyKey)) }

    fun deleteSentenceDataItemByKey(onlyKey: String) =
        evaluationItemDao.deleteSentenceDataItemByKey(onlyKey)

    fun updateEvaluationChildStatus(score: Float, onlyKey: String, index: Int) =
        evaluationItemDao.updateEvaluationChildStatus(score, onlyKey, index)

    fun insertSimpleLikeEvaluation(item: LikeEvaluation) =
        flow { emit(likeEvaluationDao.insertSimple(item)) }

    fun selectSimpleLikeEvaluation(userId: Int, itemId: Int) =
        flow { emit(likeEvaluationDao.selectSimple(userId, itemId)) }

    fun selectByNumber(number: String) = flow { emit(reDoDao.selectByNumber(number)) }
    fun insertRedo(item: ReDoBean) = reDoDao.insertRedo(item)
    fun updateRedoStatus(number: String, flag: Boolean) = reDoDao.updateRedoStatus(number, flag)
}