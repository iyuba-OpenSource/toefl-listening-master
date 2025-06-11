package com.iyuba.toelflistening.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.Repository
import com.iyuba.toelflistening.bean.AnswerItem
import com.iyuba.toelflistening.bean.CorrectSoundResponse
import com.iyuba.toelflistening.bean.EvaluationSentenceDataItem
import com.iyuba.toelflistening.bean.EvaluationSentenceResponse
import com.iyuba.toelflistening.bean.ExerciseRecord
import com.iyuba.toelflistening.bean.ExplainItem
import com.iyuba.toelflistening.bean.LikeEvaluation
import com.iyuba.toelflistening.bean.LocalCollect
import com.iyuba.toelflistening.bean.MergeResponse
import com.iyuba.toelflistening.bean.PdfResponse
import com.iyuba.toelflistening.bean.PickWord
import com.iyuba.toelflistening.bean.QuestionDetailItem
import com.iyuba.toelflistening.bean.QuestionItem
import com.iyuba.toelflistening.bean.RankInfoResponse
import com.iyuba.toelflistening.bean.ReDoBean
import com.iyuba.toelflistening.bean.ReleaseResponse
import com.iyuba.toelflistening.bean.StrangenessWord
import com.iyuba.toelflistening.bean.StudyRecordResponse
import com.iyuba.toelflistening.bean.TestRecordItem
import com.iyuba.toelflistening.bean.TextItem
import com.iyuba.toelflistening.bean.TitleIntroItem
import com.iyuba.toelflistening.bean.WordStatus
import com.iyuba.toelflistening.dao.paging.DataSource
import com.iyuba.toelflistening.dao.paging.RankPaging
import com.iyuba.toelflistening.utils.GlobalHome
import com.iyuba.toelflistening.utils.OtherUtils
import com.iyuba.toelflistening.utils.changeEncode
import com.iyuba.toelflistening.utils.clearSelf
import com.iyuba.toelflistening.utils.downLoadVideo
import com.iyuba.toelflistening.utils.getRecordTime
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.net.FlowResult
import com.iyuba.toelflistening.utils.nowTime
import com.iyuba.toelflistening.utils.operateStrangeWord
import com.iyuba.toelflistening.utils.putUserId
import com.iyuba.toelflistening.utils.showToast
import com.iyuba.toelflistening.utils.signDate
import com.iyuba.toelflistening.utils.timeStampDate
import com.iyuba.toelflistening.utils.toMd5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File


/**
苏州爱语吧科技有限公司
 */
class ToeflViewModel : BaseViewModel() {

    private val pagingUrl = "http://daxue.${OtherUtils.iyuba_cn}/ecollege/getTopicRanking.jsp"
    private val releaseUrl = "http://voa.${OtherUtils.iyuba_cn}/voa/"

    suspend fun requestToeflList() = Repository.requestToeflList().catch { }

    suspend fun getAdEntryAll(flag: Int, appid: String, uid: String) =
        Repository.getAdEntryAll(flag, appid, uid)
            .catch {
                it.judgeType().showToast()
            }

    suspend fun requestQuestList(id: String) = Repository.requestQuestList(id).catch { }
        .map {
            val list = it.titleList
            if (list.isNotEmpty()) {
                for (i in list.indices) {
                    list[i].realImg = "http://staticvip.iyuba.cn/images/toefl/tpo/" + if (i < 3) {
                        "${id.lowercase()}_passage1_${i + 1}"
                    } else {
                        "${id.lowercase()}_passage2_${i - 2}"
                    } + ".jpg"
                }
                list
            } else {
                it.titleList
            }
        }.catch {//需要声明catch 以防止在没有网络的情况下，崩溃

            Timber.d("异常");
        }

    fun saveFirstLogin(isFirstLogin: Boolean) = Repository.saveFirstLogin(isFirstLogin)
        .catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }

    fun isFirstLogin() = Repository.isFirstLogin().catch {//需要声明catch 以防止在没有网络的情况下，崩溃
        timber.log.Timber.d("异常");
    }

    suspend fun changeCollectStatus(word: String, mod: Boolean): Flow<WordStatus> {
        val action = if (mod) "delete" else "insert"
        dataMap.clearSelf().apply {
            put("userId", GlobalHome.userInfo.uid.toString())
            put("groupName", "Iyuba")
            put("word", word)
            put("mod", action)
        }
        return Repository.changeCollectStatus("${wordUrl}updateWord.jsp", dataMap)
            .catch {//需要声明catch 以防止在没有网络的情况下，崩溃
                Timber.d("异常")
            }
    }

    //activity与fragment传递数据
    val transferQuestionItem = MutableLiveData<QuestionItem>()
    fun transferQuestion(item: QuestionItem) {
        transferQuestionItem.postValue(item)
    }

    val transferTitleIntroItem = MutableLiveData<TitleIntroItem>()
    fun transferTitleIntroItem(item: TitleIntroItem) {
        transferTitleIntroItem.postValue(item)
    }

    val transferTextList = MutableLiveData<List<TextItem>>()
    fun transferTextList(item: List<TextItem>) {
        transferTextList.postValue(item)
    }

    val transferMainVideo = MutableLiveData<String>()
    fun transferMainVideo(url: String) {
        transferMainVideo.postValue(url)
    }

    val transferAnswer = MutableLiveData<List<AnswerItem>>()
    fun transferAnswer(list: List<AnswerItem>) {
        transferAnswer.postValue(list)
    }

    val transferExplain = MutableLiveData<List<ExplainItem>>()
    fun transferExplain(list: List<ExplainItem>) {
        transferExplain.postValue(list)
    }

    val releaseSimpleResponse = MutableLiveData<Unit>()

    fun transferReleaseSimple() {
        releaseSimpleResponse.postValue(Unit)
    }

    val transferEvaluationListResult = MutableLiveData<List<EvaluationSentenceDataItem>>()
    fun transferEvaluationList(list: List<EvaluationSentenceDataItem>) {
        transferEvaluationListResult.value = (list)
    }

    fun insertWord(collect: LocalCollect) = Repository.insertWord(collect).catch { }
    fun updateWord(newCollect: LocalCollect) =
        Repository.updateWord(newCollect.isCollect, newCollect.word).catch {

        }

    fun selectCollectByWord(word: String) = Repository.selectCollectByWord(word).catch { }

    suspend fun requestQuestDetail(id: String) = Repository.requestQuestDetail(id).catch { }
    suspend fun requestPickWord(word: String): Flow<PickWord> {
        dataMap.clearSelf().apply {
            put("q", word)
            put("uid", GlobalHome.userInfo.uid.toString())
            put("appId", AppClient.appId.toString())
            put("TestMode", "1")
        }
        return Repository.pickWord("${wordUrl}apiWord.jsp", dataMap)
            .catch {//需要声明catch 以防止在没有网络的情况下，崩溃
                Timber.d("异常")
            }
    }

    fun requestStrangenessWord() = Pager(
        config = PagingConfig(
            pageSize = 8,
            initialLoadSize = 16
        ),
        pagingSourceFactory = { DataSource() }
    ).flow

    suspend fun evaluationSentence(
        item: TextItem,
        fileName: String,
        wordId: String = "0"
    ): Flow<EvaluationSentenceResponse> {

        val url = "http://${OtherUtils.i_user_speech}test/ai/"
        val file = File(fileName)
        val body = RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file)
        val flg = (if (wordId == "0") "0" else "2")
        val index = item.senIndex
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("type", AppClient.appName)
            .addFormDataPart("userId", GlobalHome.userInfo.uid.toString())
            .addFormDataPart("newsId", item.titleNum)//titleNum
            .addFormDataPart("paraId", index)//senIndex
            .addFormDataPart("IdIndex", index)//senIndex
            .addFormDataPart("sentence", item.sentence)
            .addFormDataPart("file", file.name, body)
            .addFormDataPart("wordId", wordId)
            .addFormDataPart("flg", flg)
            .addFormDataPart("appId", AppClient.appId.toString())
            .build()
        return Repository.evaluationSentence(url, builder).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    suspend fun releaseSimple(bean: TextItem): Flow<ReleaseResponse> {
        val uid = GlobalHome.userInfo.uid.toString()
        val username = GlobalHome.userInfo.nickname
        val index = bean.senIndex
        dataMap.clearSelf().apply {
            put("topic", AppClient.appName)
            put("platform", "android")
            put("protocol", "60002")
            put("format", "json")
            put("userid", uid)
            put("voaid", bean.titleNum)
            put("username", username)
            put("shuoshuotype", "2")
            put("paraid", index)
            put("idIndex", index)
            put("score", bean.fraction)
            put("content", bean.selfVideoUrl)
        }
        return Repository.releaseSimple(releaseSimple, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    fun selectByType(type: String, position: Int) = Repository.selectByType(type, position)
    fun insertSimple(bean: List<QuestionDetailItem>) = Repository.insertSimple(bean)

    fun updateEvaluationSentenceItemStatus(item: TextItem) =
        Repository.updateEvaluationSentenceItemStatus(
            GlobalHome.userInfo.uid, item.titleNum.toInt(), item
        ).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }

    fun insertSentence(sentenceResult: List<TextItem>) =
        Repository.insertSentence(sentenceResult).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }

    fun selectSentenceList(userId: Int, titleNum: String) =
        Repository.selectSentenceList(userId, titleNum).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }

    fun selectSimpleEvaluation(idIndex: Int = 1) = Repository.selectSimpleEvaluation(
        GlobalHome.titleNum.toString(),
        idIndex,
        GlobalHome.userInfo.uid.toString()
    ).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
        Timber.d("异常")
    }

    fun insertEvaluation(resultList: List<EvaluationSentenceDataItem>) =
        Repository.insertEvaluation(resultList)

    fun selectEvaluationList(userId: Int, groupNum: Int) =
        Repository.selectEvaluationList(userId, groupNum).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }

    fun selectEvaluationByKey(onlyKey: String) =
        Repository.selectEvaluationByKey(onlyKey).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }

    fun deleteSentenceDataItemByKey(onlyKey: String) =
        Repository.deleteSentenceDataItemByKey(onlyKey)

    fun updateEvaluationChildStatus(score: Float, onlyKey: String, index: Int) =
        Repository.updateEvaluationChildStatus(score, onlyKey, index)

    suspend fun getRankDataUser() = Repository.getRankData(pagingUrl)

    fun getRankData() = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20
        ), pagingSourceFactory = { RankPaging(pagingUrl) }
    ).flow

    suspend fun getWorksByUserId(userId: Int): Flow<RankInfoResponse> {
        val dateFormat = signDate()
        val sign = "${userId}getWorksByUserId${dateFormat}".toMd5()
        dataMap.clearSelf().apply {
            put("uid", userId.toString())
            put("topic", AppClient.appName)
            put("topicId", GlobalHome.titleNum.toString())
            put("sign", sign)
            put("shuoshuoType", "2,4")
        }
        return Repository.getWorksByUserId(releaseUrl + "getWorksByUserId.jsp", dataMap)
            .catch {//需要声明catch 以防止在没有网络的情况下，崩溃
                Timber.d("异常")
            }
    }

    suspend fun likeEvaluation(id: Int): Flow<ReleaseResponse> {
        dataMap.clearSelf().apply {
            put("id", id.toString())
            put("protocol", "61001")
        }
        return Repository.likeEvaluation(releaseSimple, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    fun insertSimpleLikeEvaluation(item: LikeEvaluation) =
        Repository.insertSimpleLikeEvaluation(item).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }

    fun selectSimpleLikeEvaluation(itemId: Int) =
        Repository.selectSimpleLikeEvaluation(GlobalHome.userInfo.uid, itemId)
            .catch {//需要声明catch 以防止在没有网络的情况下，崩溃
                Timber.d("异常")
            }

    val submitListenRecord = MutableSharedFlow<FlowResult<StudyRecordResponse>>()

    fun submitStudyRecord(
        startTime: String,
        isEnd: Boolean,
        testWords: String,
        testNumber: String,
        titleNum: String
    ) {
        val endFlag = if (isEnd) "1" else "0"
        val score = "0"
        val testMode = "1"
        val userAnswer = ""
        val sign = GlobalHome.userInfo.uid.toString() + startTime + System.currentTimeMillis()
            .timeStampDate("yyyy-MM-dd").toMd5()
        dataMap.apply {
            clear()
            putFormat()
            putPlatform()
            putAppName("appName")
            put("Lesson", AppClient.appName.changeEncode())
            putAppId("appId")
            put("BeginTime", startTime.changeEncode())
            put("EndTime", getRecordTime().changeEncode())
            put("EndFlg", endFlag)
            put("LessonId", titleNum)
            put("TestNumber", testNumber)
            put("TestWords", testWords)
            put("TestMode", testMode)
            put("UserAnswer", userAnswer)
            put("Score", score)
            putDeviceId()
            putUserId("uid")
            put("sign", sign)
        }
        viewModelScope.launch {
            Repository.submitStudyRecord(submitRecordUrl, dataMap).onStart {
                submitListenRecord.emit(FlowResult.Loading())
            }.catch {
                submitListenRecord.emit(FlowResult.Error(it))
            }.collect {
                submitListenRecord.emit(FlowResult.Success(it))
            }
        }
    }

    private val submitExerciseResult = MutableSharedFlow<FlowResult<StudyRecordResponse>>()
    val lastSubmitExerciseResult = submitExerciseResult.asSharedFlow()

    fun submitExerciseRecord(list: List<ExerciseRecord>) {
        val array = JSONArray()
        list.forEach {
            array.put(it.toJsonObject())
        }
        val obj = JSONObject().put("datalist", array)
        val sign = "${GlobalHome.userInfo.uid}iyubaTest${nowTime()}".toMd5()
        dataMap.apply {
            clear()
            putFormat()
            putUserId("uid")
            putAppId("appId")
            putDeviceId()
            putAppName("appName")
            put("sign", sign)
            put("jsonStr", obj.toString().changeEncode())
        }
        viewModelScope.launch {
            Repository.submitExerciseRecord(exerciseUrl, dataMap).onStart {
                submitExerciseResult.emit(FlowResult.Loading())
            }.catch {
                submitExerciseResult.emit(FlowResult.Error(it))
            }.flowOn(Dispatchers.IO)
                .collect {
                    submitExerciseResult.emit(FlowResult.Success(it))
                }
        }
    }

    val downError = MutableSharedFlow<FlowResult<Boolean>>()


    /**
     * 当前用List<Deferred<T>>遍历await()的方法较直接await()、withContext(Dispatchers.IO)、直接下载速度最快
     * */
    fun downloadVideo(pair: Pair<String, File>) {
        viewModelScope.launch {
            flow {
                Repository.downloadVideo(pair.first)
                    .apply {
                        val result = if (contentLength() > 0) {
                            try {
                                pair.second.downLoadVideo(this)
                                true
                            } catch (e: Exception) {
                                false
                            }
                        } else {
                            false
                        }
                        emit(result)
                    }
            }.onStart {
                downError.emit(FlowResult.Loading())
            }.catch {
                emit(false)
                downError.emit(FlowResult.Error(it))
            }.flowOn(Dispatchers.IO)
                .collect {
                    downError.emit(FlowResult.Success(it))
                }
        }
    }

    suspend fun releaseMerge(
        score: String,
        content: String,
        voaId: Int
    ): Flow<Result<ReleaseResponse>> {
        dataMap.apply {
            clear()
            putAppName("topic")
            putPlatform()
            putFormat()
            putProtocol("60003")
            putUserId("userid")
            put("voaid", voaId.toString())
            put("score", score)
            put("content", content)
            put("shuoshuotype", "4")
        }
        return Repository.releaseMerge(releaseSimple, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    suspend fun mergeVideos(audios: String): Flow<Result<MergeResponse>> {
        dataMap.apply {
            clear()
            put("audios", audios)
            putAppName()
        }
        return Repository.mergeVideos(mergeUrl, dataMap).catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    /**
     * --------------------------------------------------------------------------------------------------------------------
     * */

    val testRecordFlow = MutableSharedFlow<FlowResult<List<TestRecordItem>>>(10, 10)
//    val lastTestRecord=testRecordFlow.asSharedFlow()

    fun requestTestRecord(lessonId: String) {
        reDoFlag = true
        val sign = with(StringBuilder()) {
            append(GlobalHome.userInfo.uid)
            append(signDate())
            toString().toMd5()
        }
        dataMap.apply {
            clear()
            putAppId("appId")
            putUserId("uid")
            put("TestMode", "10")
            put("sign", sign)
            putFormat()
            put("Pageth", "1")
            put("NumPerPage", "1000")
        }
        viewModelScope.launch {
            Repository.requestTestRecord(testRecordUrl, dataMap).onStart {
                testRecordFlow.emit(FlowResult.Loading())
            }.map {
                it.data.filter { item ->
                    (item.LessonId == lessonId)
                }
            }.catch {
                testRecordFlow.emit(FlowResult.Error(it))
                Timber.tag("testRecord").d("Error")
            }.collect {
                testRecordFlow.emit(FlowResult.Success(it))
                Timber.tag("testRecord").d("Success")
            }
        }
    }

    /**
     * --------------------------------------------------------------------------------------------------------------------
     * */
    fun selectByNumber(number: String) = Repository.selectByNumber(number).map {
        if (it.isEmpty()) {
            false
        } else {
            it.first().reDid
        }
    }.catch {//需要声明catch 以防止在没有网络的情况下，崩溃
        Timber.d("异常")
    }

    private var reDoFlag = false
    fun getReDidFlag() = reDoFlag

    fun operateRedo(number: String, flag: Boolean) {
        viewModelScope.launch {
            Repository.selectByNumber(number).map {
                if (it.isNotEmpty()) {
                    Repository.updateRedoStatus(number, flag)
                } else {
                    Repository.insertRedo(ReDoBean(number, flag))
                }
            }.catch {//需要声明catch 以防止在没有网络的情况下，崩溃
                Timber.d("异常")
            }.first()
        }
    }

    /**
     * --------------------------------------------------------------------------------------------------------------------
     * 获取生词本pdf
     * */
    private val strangePdf = MutableSharedFlow<FlowResult<PdfResponse>>()
    val lastStrangePdf = strangePdf.asSharedFlow()
    private fun requestStrangeResponse(): Flow<StrangenessWord> {
        dataMap.operateStrangeWord()
        val reUrl = "http://word.iyuba.cn/words/wordListService.jsp"
        return flow {
            emit(
                Repository.requestStrangenessWord(
                    reUrl,
                    dataMap
                )
            )
        }.catch {//需要声明catch 以防止在没有网络的情况下，崩溃
            Timber.d("异常")
        }
    }

    /**
     * 当Paging3的Response的List为Empty时，catch的标准是什么？？？
     * */
    fun requestStrangePdf() {
        val hint = "请先收藏生词"
        viewModelScope.launch {
            requestStrangeResponse().flatMapMerge {
                if (it.row.isEmpty()) {
                    flow { emit(PdfResponse(-1, hint)) }
                } else {
                    dataMap.operateStrangeWord(GlobalHome.wordPageCounts)
                    Repository.requestStrangePdf(wordPDFUrl, dataMap)
                }
            }.onStart {
                strangePdf.emit(FlowResult.Loading())
            }.catch {
                strangePdf.emit(FlowResult.Error(Throwable(hint)))
            }.collect {
                strangePdf.emit(FlowResult.Success(it))
            }
        }
    }

    /**
     * ----------------------------------------------------------------------
     * */
    private val evalWord = MutableSharedFlow<FlowResult<EvaluationSentenceResponse>>()
    val lastEvalWord = evalWord.asSharedFlow()
    fun evaluationWord(item: TextItem, fileName: String, wordId: String) {
        viewModelScope.launch {
            evaluationSentence(item, fileName, wordId).onStart {
                evalWord.emit(FlowResult.Loading())
            }.catch {
                evalWord.emit(FlowResult.Error(it))
            }.collect {
                evalWord.emit(FlowResult.Success(it))
            }
        }
    }

    /**
     * ----------------------------------------------------------------------
     * 因为由冷流转换为热流的写法，所以只能依靠传Boolean然后emit(Boolean)来实现
     * */
    private val correctWord = MutableSharedFlow<FlowResult<Pair<CorrectSoundResponse, Boolean>>>()
    val lastCorrectWord = correctWord.asSharedFlow()
    fun correctSound(word: String, item: EvaluationSentenceDataItem, otherOperate: Boolean) {
        dataMap.apply {
            clear()
            put("q", word)
            put("user_pron", item.user_pron.changeEncode())
            put("ori_pron", item.pron.changeEncode())
        }
        viewModelScope.launch {
            Repository.correctSound(correctSoundUrl, dataMap).catch {
                correctWord.emit(FlowResult.Error(it))
            }.collect {
                correctWord.emit(FlowResult.Success(Pair(it, otherOperate)))
            }
        }
    }

}