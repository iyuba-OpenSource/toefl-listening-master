package com.iyuba.toelflistening.bean

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.utils.GlobalHome
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable
import java.math.BigDecimal


/**
苏州爱语吧科技有限公司
 */
data class ToeflItem(
    val downloadState: Int,
    val id: Int,
    val isDownload: Boolean,
    val isFree: Boolean,
    val isVip: Boolean,
    val name: String,
    val productID: String,
    val progress: String,
    val version: Int
) {
    fun realImg() = "http://staticvip.iyuba.cn/images/toefl/tpo/tpo${id}_passage1_1.jpg"
}

data class ToeflResponse(val size: Int, val data: List<ToeflItem>)

//---------------------------------------


data class QuestionItem(
    val cnExplain: Boolean,
    val cnText: Boolean,
    val enExplain: Boolean,
    val entext: Boolean,
    val favorite: Boolean,
    val handle: String = "",
    val jpExplain: Boolean,
    val jptext: Boolean = false,
    val packName: String = "",
    val partType: String = "",
    val quesNum: String = "",
    val rightNum: String = "",
    val sound: String = "",
    val soundTime: String = "",
    val studyTime: String = "",
    val testTime: String = "",
    val testType: String = "",
    val titleName: String = "",
    val titleNum: String = "",
    val vip: Boolean = false,
    var realImg: String = ""
) : Serializable

data class QuestionResponse(
    val titleSum: Int,
    var titleList: List<QuestionItem>
)

//---------------------------------------

data class SettingItem(val icon: Int, val item: String) {
    var temporaryQQ = ""
}

//---------------------------------------

data class QuestionDetailResponse(val itemNum: Int, val itemList: List<QuestionDetailItem>)

@Entity
data class QuestionDetailItem(
    var groupType: String = "",
    var position: Int = 0,
    var total: Int = 0,


    var sound: String = "",
    var partType: String = "",
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun isEmpty() = (groupType.isEmpty() || total == 0 || sound.isEmpty() || partType.isEmpty())

    @Ignore
    val titleIntro = mutableListOf<TitleIntroItem>()

    @Ignore
    val answer = mutableListOf<AnswerItem>()

    @Ignore
    var textList = listOf<TextItem>()

    @Ignore
    val explain = mutableListOf<ExplainItem>()
}

@Entity
data class TitleIntroItem(
    var position: Int = 0,
    var groupType: String = "",
    //标题简介
    val level: String = "",
    val titleCn: String = "",
    val titleEn: String = "",
    val titleName: String = "",
    val titleNum: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

@Entity
@Parcelize
data class AnswerItem(
    var groupType: String = "",
    var position: Int = 0,
    val answer: String = "",
    val answerNum: String = "",
    val answerText: String = "",
    val isSingle: String = "",
    val partType: String = "",
    val quesImage: String = "",
    val quesIndex: String = "",
    val quesText: String = "",
    val sound: String = "",
    val testType: String = "",
    val titleNum: String = ""
) : Parcelable {
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

@Parcelize
@Entity
data class ExplainItem(
    var position: Int = 0,
    var groupType: String = "",
    val explains: String = "",
    val partType: String = "",
    val quesIndex: String = "",
    val sound: String = "",
    val testType: String = "",
    val titleNum: String = ""
) : Parcelable {
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

@Entity
data class TextItem(
    var groupType: String,
    var position: Int,
    val partType: String,
    val senIndex: String,
    val sentence: String,
    val sound: String,
    val testType: String,
    val timing: Int,
    val titleName: String,
    val titleNum: String,
    //
    var success: Boolean = false,
    var showOperate: Boolean = false,
    var fraction: String = "",
    var onlyKay: String = "",
    var selfVideoUrl: String = "",
    var userId: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun inflateEmpty(type: String) {
        userId = GlobalHome.userInfo.uid
        groupType = type
        fraction = ""
        onlyKay = ""
        selfVideoUrl = ""
    }
}

//---------------------------------------
data class SentenceItem(val item: String, var flag: Boolean = false)

//---------------------------------------
data class WordInfo(val start: Int, val end: Int)

//---------------------------------------
@Root(name = "data", strict = false)
data class PickWord @JvmOverloads constructor(
    @field:Element(name = "result")
    var result: Int = 0,
    @field:Element(name = "key")
    var key: String = "",
    @field:Element(name = "audio", required = false)
    var audio: String = "",
    @field:Element(name = "pron", required = false)
    var pron: String = "",
    @field:Element(name = "proncode", required = false)
    var proncode: String = "",
    @field:Element(name = "def")
    var def: String = "",
    @field:ElementList(entry = "sent", required = true, inline = true)
    @param:ElementList(entry = "sent", required = true, inline = true)
    val sent: List<PickWordItem>,
) {
    fun realPron() = "[$pron]"
}

@Root(name = "sent")
data class PickWordItem @JvmOverloads constructor(
    @field:Element(name = "number")
    var number: Int = 0,
    @field:Element(name = "orig")
    var orig: String = "",
    @field:Element(name = "trans")
    var trans: String = ""
) {
    fun getRealOrig() = orig.replace("<em>", "'").replace("</em>", "'")
}

//---------------------------------------
@Root(name = "response", strict = false)
data class WordStatus @JvmOverloads constructor(
    @field:Element(name = "result", required = false)
    var result: Int = 1,
    @field:Element(name = "word", required = false)
    var word: String = ""
)

//---------------------------------------
@Entity
data class LocalCollect(var word: String = "", var isCollect: Boolean = false) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    fun reverseCollect(): LocalCollect {
        isCollect = !isCollect
        return this
    }
}

//---------------------------------------
@Root(name = "response", strict = false)
data class StrangenessWord @JvmOverloads constructor(
    @field:Element(name = "counts")
    var counts: Int = 0,
    @field:Element(name = "pageNumber")
    var pageNumber: Int = 0,
    @field:Element(name = "totalPage")
    var totalPage: Int = 0,
    @field:Element(name = "firstPage")
    var firstPage: Int = 0,
    @field:Element(name = "prevPage")
    var prevPage: Int = 0,
    @field:Element(name = "nextPage")
    var nextPage: Int = 0,
    @field:Element(name = "lastPage")
    var lastPage: Int = 0,
    @field:ElementList(entry = "row", inline = true, required = false)
    @param:ElementList(entry = "row", inline = true, required = false)
    val row: List<StrangenessWordItem>,
)

@Root(name = "row", strict = false)
data class StrangenessWordItem @JvmOverloads constructor(
    //可能为空值------------>required = false
    @field:Element(name = "Word")
    var Word: String = "",
    @field:Element(name = "createDate")
    var createDate: String = "",
    @field:Element(name = "Audio", required = false)
    var Audio: String = "",
    @field:Element(name = "Pron", required = false)
    var Pron: String = "",
    @field:Element(name = "Def")
    var Def: String = ""
) {
    fun realPron() = "[$Pron]"
}

data class EvaluationSentenceResponse(
    val result: Int,
    val message: String,
    val data: EvaluationSentenceData
)

data class EvaluationSentenceData(
    val sentence: String,
    val total_score: Float,
    val scores: Int,
    val URL: String,
    val filepath: String,
    val words: List<EvaluationSentenceDataItem>
) {
    val realScopes get() = "${scores}分"
}

@Entity
data class EvaluationSentenceDataItem(
    val content: String = "",
    var index: Int = 0,
    var groupNum: Int = 0,
    val score: Float = 0f,
    /**
     * 以下为新增字段，再次遇到     编译错误
     * New NOT NULL column 'user_pron2' added with no default value specified. Please specify the default value using @ColumnInfo.
     * 之前怎么解决的我居然忘了，，，
     * 解决方案居然是给新增字段“依次”加ColumnInfo注解
     * */
    @ColumnInfo(name = "delete")
    val delete: String = "",
    @ColumnInfo(name = "insert")
    val insert: String = "",
    @ColumnInfo(name = "pron")
    val pron: String = "",
    @ColumnInfo(name = "pron2")
    val pron2: String = "",
    @ColumnInfo(name = "substitute_orgi")
    var substitute_orgi: String = "",
    @ColumnInfo(name = "substitute_user")
    val substitute_user: String = "",
    @ColumnInfo(name = "user_pron")
    val user_pron: String = "",
    @ColumnInfo(name = "user_pron2")
    val user_pron2: String = "",
    //以下数据不是json数据，是本地逻辑需要
    var userId: Int = 0,
    var onlyKay: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    val realScope get() = "${score}分"
}

data class ReleaseResponse(
    val AddScore: Int = -1,
    val Message: String = "",
    val ResultCode: String = "",
    val ShuoshuoId: Int = -1
) {
    fun isNotEmpty() = (Message.isNotEmpty())
}

data class RankResponse(
    var message: String = "",
    var mycount: Int = 0,
    var myid: Int = -1,
    var myimgSrc: String = "",
    var myranking: Int = 0,
    var myscores: Int = 0,
    var result: Int = -1,
    var vip: String = "",
    val data: List<RankItem>,
    val noLogin: String = "未登录",
    var myname: String = noLogin,
    val totalTest: Int = 0,
    val totalRight: Int = 0,
    /**
     * 区别测试与口语
     * */
    var testFlag: Boolean = false
) {
    fun copyChange(response: RankResponse) {
        message = response.message
        mycount = response.mycount
        myid = response.myid
        myimgSrc = response.myimgSrc
        myranking = response.myranking
        myscores = response.myscores
        result = response.result
        vip = response.vip
        myname = response.myname
    }

    fun myRealCount() = if (testFlag) {
        "正确数：${totalRight} \n正确率：${averageScore}"
    } else {
        "句子数:$mycount 平均分:${if (mycount == 0) mycount else myscores / mycount}"
    }

    fun myRealScores() = (if (testFlag) "总题数：${totalTest}" else "${myscores}分")

    fun isNotLogin() = (myid == -1)
    fun clear() {
        myranking = 0
        myimgSrc = ""
        myname = noLogin
        mycount = 0
        myscores = 0
        myid = -1
    }

    private val averageScore
        get() =
            if (testFlag) {
                if (totalTest != 0) {
                    val a = (totalRight.toDouble() / totalTest.toDouble()) * 100
                    val b = BigDecimal(a)
                    val c = b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                    "$c%"
                } else {
                    totalTest.toString()
                }
            } else {
                "平均分:${myscores / mycount}"
            }
}

data class RankItem(
    val count: Int,
    val imgSrc: String,
    val name: String,
    val ranking: Int = 0,
    val scores: Int,
    val sort: Int,
    val uid: Int,
    val vip: String,
    val totalTest: Int,
    val totalRight: Int,


    ) {
    fun realCount() = if (scores == 0) {
        "正确数：${totalRight} \n正确率：${averageScore}"
    } else {
        "句子数:$count \n$averageScore"
    }

    fun realScores() = with(scores) {
        (if (this == 0) "总题数：${totalTest}" else "${this}分")
    }

    private val averageScore
        get() =
            if (scores == 0) {
                val a = (totalRight.toDouble() / totalTest.toDouble()) * 100
                val b = BigDecimal(a)
                val c = b.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                "$c%"
            } else {
                "平均分:${scores / count}"
            }

}


data class RankInfoItem(
    val CreateDate: String,
    val ShuoShuo: String,
    val TopicId: Int,
    val againstCount: Int,
    var agreeCount: Int,
    val id: Int,
    val idIndex: Int,
    val paraid: Int,
    val score: Int,
    val shuoshuotype: Int,
    var sentenceZh: String,
    var sentenceEn: String,
    var username: String,
    var headUrl: String
) {
    val realType: String get() = (if (shuoshuotype == 4) "合成" else "单句")
    val realScore: String get() = "${score}分"
}

data class RankInfoResponse(
    val count: Int,
    val message: String,
    val result: Boolean,
    val data: List<RankInfoItem>
)

@Entity
data class LikeEvaluation(val userId: Int, val itemId: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}


data class StudyRecordResponse(
    val result: String,
    @SerializedName(value = "jifen")
    val scores: String,
    val message: String,
)

@Parcelize
data class ExerciseRecord(
    //登录用户id
    val uid: Int = GlobalHome.userInfo.uid,
    //voaid
    var LessonId: String = "",
    //题号
    var TestNumber: String = "",
    //这道题做题开始时间
    var BeginTime: String = "",
    //用户的答案
    var UserAnswer: String = "",
    //正确答案
    var RightAnswer: String = "",
    //答对了1,答错了0
    var AnswerResut: String = "",
    //这道题做题结束时间
    var TestTime: String = "",
    val AppName: String = AppClient.appName,
    /**
     * 问题
     * */
    var title: String = "",
    /**
     * 解析
     * */
    var analysis: String = ""
) : Parcelable {
    fun toJsonObject() = with(JSONObject()) {
        put("uid", uid)
        put("LessonId", LessonId)
        put("TestNumber", TestNumber)
        put("BeginTime", BeginTime)
        put("UserAnswer", UserAnswer)
        put("RightAnswer", RightAnswer)
        put("AnswerResut", AnswerResut)
        put("TestTime", TestTime)
        put("AppName", AppName)
        this
    }
}

data class SomeResponse<T>(
    val result: Int,
    val message: String,
    val data: List<T>
)

data class TestRecordItem(
    val AppId: String,
    val AppName: String,
    val BeginTime: String,
    val LessonId: String,
    val RightAnswer: String,
    val Score: Int,
    val TestNumber: String,
    val TestTime: String,
    val TestWords: String,
    val UpdateTime: String,
    val UserAnswer: String,
    val testindex: String
)

/**
 * 本地存储重做标记
 * */
@Entity
data class ReDoBean(
    val testNumber: String,
    val reDid: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

data class PdfResponse(
    val result: Int,
    val filePath: String
)

data class CorrectSoundResponse(
    val audio: String = "",
    val def: String = "",
    val delete_id: List<List<Int>>,
    val insert_id: List<List<Int>>,
    val key: String,
    val match_idx: List<List<Int>>,
    val ori_pron: String = "",
    val pron: String,
    val proncode: String,
    val result: Int,
    val sent: List<PickWordItem>,
    val substitute_id: List<List<Int>>,
    val user_pron: String = ""
) {
    val realOri get() = "[$ori_pron]"
    val realUserPron get() = "[$user_pron]"
}

