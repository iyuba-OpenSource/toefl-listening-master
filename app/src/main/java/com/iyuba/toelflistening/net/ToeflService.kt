package com.iyuba.toelflistening.net

import com.iyuba.toelflistening.bean.CorrectSoundResponse
import com.iyuba.toelflistening.bean.EvaluationSentenceResponse
import com.iyuba.toelflistening.bean.MergeResponse
import com.iyuba.toelflistening.bean.PdfResponse
import com.iyuba.toelflistening.bean.PickWord
import com.iyuba.toelflistening.bean.QuestionDetailResponse
import com.iyuba.toelflistening.bean.QuestionResponse
import com.iyuba.toelflistening.bean.RankInfoResponse
import com.iyuba.toelflistening.bean.RankResponse
import com.iyuba.toelflistening.bean.ReleaseResponse
import com.iyuba.toelflistening.bean.SomeResponse
import com.iyuba.toelflistening.bean.StrangenessWord
import com.iyuba.toelflistening.bean.StudyRecordResponse
import com.iyuba.toelflistening.bean.TestRecordItem
import com.iyuba.toelflistening.bean.ToeflResponse
import com.iyuba.toelflistening.bean.WordStatus
import com.iyuba.toelflistening.java.model.bean.AdEntryBean
import com.iyuba.toelflistening.utils.net.ConverterFormat
import com.iyuba.toelflistening.utils.net.ResponseConverter
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
苏州爱语吧科技有限公司
 */
interface ToeflService {
    //首页列表
    @GET("getToeflTestList.jsp")
    suspend fun requestToeflList(): ToeflResponse


    /**
     *获取广告
     */
    @GET
    suspend fun requestAdEntryAll(
        @Url url: String,
        @Query("flag") flag: Int,
        @Query("appId") appid: String,
        @Query("uid") uid: String
    ): List<AdEntryBean>

    //试题列表
    @GET("getToeflTitleList.jsp")
    suspend fun requestQuestList(@Query("id") id: String): QuestionResponse

    @GET("getToeflTestDetail.jsp")
    suspend fun requestQuestDetail(@Query("id") id: String): QuestionDetailResponse

    //改变单词收藏状态
    @ResponseConverter(ConverterFormat.XML)
    @GET
    suspend fun changeCollectStatus(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): WordStatus

    //取词
    @ResponseConverter(ConverterFormat.XML)
    @GET
    suspend fun pickWord(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): PickWord

    //获取生词列表
    @ResponseConverter(ConverterFormat.XML)
    @GET
    suspend fun requestStrangenessWord(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): StrangenessWord

    //评测
    @POST
    suspend fun evaluationSentence(
        @Url url: String,
        @Body body: RequestBody
    ): EvaluationSentenceResponse

    //分享单个评测
    @GET
    suspend fun releaseSimple(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): ReleaseResponse

    //获取排行
    @GET
    suspend fun getRankData(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): RankResponse

    //排行具体信息
    @GET
    suspend fun getWorksByUserId(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): RankInfoResponse

    //点赞
    @GET
    suspend fun likeEvaluation(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): ReleaseResponse


    /**
     * 听力学习记录提交
     */
    @GET
    suspend fun submitStudyRecord(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): StudyRecordResponse

    /**
     * 提交做题记录
     * */
    @GET
    suspend fun submitExerciseRecord(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): StudyRecordResponse

    /**
     * 下载音频
     * */
    @GET
    @Streaming
    suspend fun downloadVideo(@Url url: String): ResponseBody

    //合成语音
    @GET
    suspend fun mergeVideos(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): MergeResponse

    //发布合成后的
    @GET
    suspend fun releaseMerge(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): ReleaseResponse


    @GET
    suspend fun requestTestRecord(
        @Url url: String,
        @QueryMap map: Map<String, String>,
    ): SomeResponse<TestRecordItem>

    @GET
    suspend fun requestStrangePdf(
        @Url url: String,
        @QueryMap map: Map<String, String>,
    ): PdfResponse

    /**
     * 纠音&&单词评测
     * */
    @GET
    suspend fun correctSound(
        @Url url: String,
        @QueryMap map: Map<String, String>
    ): CorrectSoundResponse
}