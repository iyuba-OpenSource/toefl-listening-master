package com.iyuba.toelflistening.utils

//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody.Companion.asRequestBody
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import cn.fly.verify.common.exception.VerifyException
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.onekeyshare.OnekeyShare
import coil.load
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.activity.EvaluationInfoActivity
import com.iyuba.toelflistening.activity.MemberCentreActivity
import com.iyuba.toelflistening.activity.PayActivity
import com.iyuba.toelflistening.activity.ShowWordActivity
import com.iyuba.toelflistening.activity.UseInstructionsActivity
import com.iyuba.toelflistening.bean.EvaluationSentenceDataItem
import com.iyuba.toelflistening.bean.TextItem
import com.iyuba.toelflistening.java.actiity.login.WxLoginActivity
import com.iyuba.toelflistening.utils.logic.VoiceStatus
import com.youdao.sdk.nativeads.ImageService
import com.youdao.sdk.nativeads.NativeErrorCode
import com.youdao.sdk.nativeads.NativeResponse
import com.youdao.sdk.nativeads.RequestParameters
import com.youdao.sdk.nativeads.YouDaoNative
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.URLEncoder
import java.net.UnknownHostException
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.KProperty

/**
苏州爱语吧科技有限公司
 */
fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(AppClient.context, this, duration).show()
}

fun String.changeEncode(): String = URLEncoder.encode(this, "utf-8")

fun String.showDialog(
    context: Context,
    positive: String = context.getString(R.string.confirm),
    method: () -> Unit
) {
    showPrivacyDialog(context, "提示", positive, context.getString(R.string.cancel), method, null)
}


fun String.showDialogEval(
    context: Context,
    positive: String = context.getString(R.string.confirm),
    negative: String = "取消",
    negativeMethod: () -> Unit,
    method: () -> Unit
) {
    showPrivacyDialog(
        context,
        "提示",
        positive,
        context.getString(R.string.cancel),
        method,
        negativeMethod
    )
}

fun Activity.showGoLoginDialog() {
    "您还未登录，是否跳转登录界面？".showDialog(this, "跳转登录") {
        startActivity(Intent(this, WxLoginActivity::class.java))
    }
}

fun String.showPrivacyDialog(
    context: Context,
    title: String,
    positive: String,
    negative: String,
    positiveMethod: () -> Unit,
    negativeMethod: (() -> Unit)?
) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(this)
        .setPositiveButton(positive) { _, _ ->
            positiveMethod()
        }
        .setNegativeButton(negative) { _, _ ->
            negativeMethod?.let {
                negativeMethod()
            }
        }
        .show()
}

fun RadioGroup.addRadioButton(context: Context, answerText: String, enable: Boolean = true) {
    val array = answerText.split(OtherUtils.delimiter)
    for (i in array.indices) {
        val showText = OtherUtils.selectArray[i] + "." + array[i]
        this.addSingleRadioButton(context, showText, i, enable = enable)
    }
}

fun RadioGroup.addSingleRadioButton(
    context: Context,
    text: String,
    id: Int,
    margin: Int = 10,
    enable: Boolean = true
) {

    val line = View(context)
    line.setBackgroundColor(Color.BLACK)
    val span = SpannableString(text)
    if (margin == 20) {
        val fore = ForegroundColorSpan(ContextCompat.getColor(context, R.color.yellow))
        span.setSpan(fore, text.indexOf(":"), text.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    } else {
        val absolute = AbsoluteSizeSpan(22, true)
        span.setSpan(absolute, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
    val radioButton = with(RadioButton(context)) {
        this.isClickable = enable
        this.id = id
        this.text = span
        this.standardTextSize()
        this
    }
    val params = RadioGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    val lineParams = RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
    params.setMargins(margin)
    this.addView(radioButton, params)
    this.addView(line, lineParams)
}

fun String.toMd5(): String {
    val hash = MessageDigest.getInstance("MD5").digest(this.toByteArray())
    return with(StringBuilder()) {
        hash.forEach {
            val i = it.toInt() and (0xFF)
            var temp = Integer.toHexString(i)
            if (temp.length == 1) {
                temp = "0$temp"
            }
            this.append(temp)
        }
        this.toString()
    }
}

fun Context.skipWeb(url: String) {
    startActivity<UseInstructionsActivity> {
        putExtra(ExtraKeyFactory.outWebPage, url)
    }
}

fun TextInputLayout.showUserNameEmpty(
    hint: String = "用户名长度为3~15个字符",
    flag: Boolean,
    method: () -> Unit
) {
    this.error = if (flag) {
        hint
    } else {
        method()
        ""
    }
}

fun TextInputLayout.showPassWordEmpty(flag: Boolean, method: () -> Unit) {
    this.showUserNameEmpty("密码为6~15个字符", flag, method)
}

fun Activity.startShowWordActivity(s: String, star: Boolean = false) {
    startActivity<ShowWordActivity> {
        putExtra(ExtraKeyFactory.definitionWord, s)
        putExtra(ExtraKeyFactory.listWord, star)
    }
}

fun Long.timeStampDate(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val format = SimpleDateFormat(pattern, Locale.CHINA)
    return format.format(Date(this))
}

fun nowTime() = System.currentTimeMillis().timeStampDate("yyyy-MM-dd")

fun AppCompatActivity.judgeVip(features: String, isVip: () -> Unit) {
    if (!GlobalHome.isLogin()) {
        showGoLoginDialog()
        return
    }
    val info = GlobalHome.userInfo
    if (info.isVip()) {
        isVip()
    } else {
        "会员可以免费$features，要去开通会员吗？".showDialog(this) {
            startActivity(Intent(this, MemberCentreActivity::class.java))
        }
    }
}

inline fun <reified T : Activity> Activity.judgeLogin() {
    if (!GlobalHome.isLogin()) {
        showGoLoginDialog()
        return
    }
    startActivity(Intent(this, T::class.java))
}

fun Map<String, Boolean>.checkPermission(successOperate: () -> Unit) {
    var count = 0
    this.values.forEach {
        if (it) count++
    }
    if (count == this.size) {
        successOperate()
    } else {
        "申请权限被拒绝".showToast()
    }
}

fun ActivityResultLauncher<Array<String>>.checkObtainPermission(
    permission: Array<String>,
    obtain: () -> Unit
) {
    val check = ContextCompat.checkSelfPermission(AppClient.context, permission[0])
    if (check == PackageManager.PERMISSION_GRANTED) {
        obtain()
    } else {
        launch(permission)
    }
}

fun Context.netEnabled(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}

fun Map<Int, List<EvaluationSentenceDataItem>>.showSpannable(onlyKey: String): SpannableStringBuilder {
    val sumList = mutableListOf<EvaluationSentenceDataItem>()
    this.values.forEach { sumList.addAll(it) }
    val finalList = sumList.filter { it.onlyKay == onlyKey }
    val build = SpannableStringBuilder()
    for (i in finalList.indices) {
        val item = finalList[i]
        val itemWord = item.content + " "
        build.append(itemWord)
        val green = Color.rgb(39, 124, 60)
        val colorSpan = when {
            item.score >= 4 -> ForegroundColorSpan(green)
            item.score < 3 -> ForegroundColorSpan(Color.RED)
            else -> ForegroundColorSpan(Color.BLACK)
        }
        if (i == 0) {
            build.setSpan(colorSpan, 0, itemWord.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        } else {
            val start = build.length - itemWord.length
            val end = start + itemWord.length
            build.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        }
    }
    return build
}

fun Throwable.judgeType() = when (this) {
    is NullPointerException -> "空指针异常"
    is UnknownHostException,
    is ConnectException,
    is HttpException -> "请检查网络状态"

    is IllegalStateException -> "非法状态异常"
    is InterruptedIOException -> "网络超时"
    is VerifyException -> "手机号一键登录异常"
    else -> "未知异常\n${this}"
}

fun String.changeVideoUrl() = "http://${OtherUtils.i_user_speech}voa/$this"

fun MutableMap<String, String>.clearSelf(): MutableMap<String, String> {
    if (size > 0) {
        clear()
    }
    return this
}

fun LinearLayoutCompat.addCheckItem(context: Context, answerText: String, enable: Boolean = true) {
    val array = answerText.split(OtherUtils.delimiter)
    for (i in array.indices) {
        val line = with(View(context)) {
            setBackgroundColor(Color.BLACK)
            this
        }
        val showText = OtherUtils.selectArray[i] + "." + array[i]
        val span = SpannableString(showText)
        val absolute = AbsoluteSizeSpan(22, true)
        span.setSpan(absolute, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        val check = with(CheckBox(context)) {
            text = span
            standardTextSize()
            isClickable = enable
            this
        }
        val params = RadioGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val lineParams = RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
        addView(check, params)
        addView(line, lineParams)
    }
}

fun LinearLayoutCompat.getCheckedItem(flag: Boolean = false): String {
    if (childCount > 0) {
        val builder = StringBuilder()
        val checkList = getSingleCheck()
        for (i in checkList.indices) {
            if (checkList[i].isChecked) {
                if (flag) {
                    builder.append(OtherUtils.delimiter)
                }
                builder.append(OtherUtils.selectArray[i])
            }
        }
        return builder.toString()
    }
    return ""
}

fun LinearLayoutCompat.getSingleCheck(): List<CheckBox> {
    val list = ArrayList<View>(childCount)
    for (i in 0..childCount) {
        list.add(getChildAt(i))
    }
    return list.filterIsInstance<CheckBox>()
}

fun RadioGroup.getSingleRadio(flag: Boolean = false): List<RadioButton> {
    val list = ArrayList<View>(childCount)
    for (i in 0..childCount) {
        list.add(getChildAt(i))
    }
    return with(list.filterIsInstance<RadioButton>()) {
        (if (flag) this else filter { it.isChecked })
    }
}

fun TextView.standardTextSize() {
    textSize = 18f
}

fun View.visibilityState(flag: Boolean) {
    visibility = if (flag) View.GONE else View.VISIBLE
}

fun Context.disconnectNet() = if (netEnabled()) {
    false
} else {
    "网络未连接".showToast()
    true
}

fun ActivityResultLauncher<Array<String>>.checkObtainPermission(obtain: () -> Unit) {
    val permissionArray = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val index = permissionArray.size - 1
    val check = ContextCompat.checkSelfPermission(AppClient.context, permissionArray[index])
    if (check == PackageManager.PERMISSION_GRANTED) {
        obtain()
    } else {
        this.launch(permissionArray)
    }
}

fun Context.getBitmapFromUri(uri: Uri) = contentResolver.openFileDescriptor(uri, "r")?.use {
    //use标准函数
    BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
}

suspend fun Context.compressFile(file: File): MultipartBody.Part {
    var lastFile = Compressor.compress(this, file, Dispatchers.IO)
    while (lastFile.length() > 500 * 1000) {
        //将图片压缩至500KB以内
        lastFile = Compressor.compress(this, lastFile, Dispatchers.IO)
    }
    val type = "image/*".toMediaTypeOrNull()
    val body = RequestBody.create(type, lastFile)
    return MultipartBody.Part.createFormData("path", lastFile.name, body)
}

fun String.safeToInt() = if (isEmpty()) 0 else toInt()
fun String.safeToLong() = if (isEmpty()) 0 else toLong()
fun String.safeToFloat() = if (isEmpty()) 0F else toFloat()
fun Float.changeString(): String = DecimalFormat("0.00").format(this * 0.01)

inline fun <reified T : AppCompatActivity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val intent = with(Intent(this, T::class.java)) {
        block()
        this
    }
    startActivity(intent)
}


fun Activity.startPayActivity(payType: String, payPrice: String, buyType: Boolean = false) {
    val intent = with(Intent(this, PayActivity::class.java)) {
        putExtra(ExtraKeyFactory.payType, payType)
        putExtra(ExtraKeyFactory.payPrice, payPrice)
        putExtra(ExtraKeyFactory.buyType, buyType)
        this
    }
    startActivity(intent)
    finish()
}

fun RecyclerView.addDefaultDecoration() {
    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
}

fun getRecordTime(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(Date())


fun List<TextItem>.findCurrSentence(player: MediaPlayer): String {
    for (i in indices) {
        val item = this[i]
        if (i == size - 1) {
            if (player.currentPosition / 1000 in (item.timing..player.duration / 1000)) {
                return item.senIndex
            }
        } else {
            if (player.currentPosition / 1000 in (item.timing..this[i + 1].timing)) {
                return item.senIndex
            }
        }
    }
    return "1"
}

fun List<TextItem>.getAllWords(): Int {
    var count = 0
    forEach {
        val sentenceArray = it.sentence.split(" ")
        count += sentenceArray.size
    }
    return count
}


fun MediaPlayer.getPercent() = (currentPosition.toFloat()) / (duration.toFloat())

fun MediaPlayer.isCompleted() = when {
    currentPosition > duration -> currentPosition - duration < 20
    currentPosition < duration -> duration - currentPosition < 20
    else -> true
}

fun ImageView.loadQRCode(url: String) {
    val w = 90
    val h = 90
    val map = mutableMapOf(EncodeHintType.CHARACTER_SET to "utf-8")
    val bitMatrix = QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, w, h, map)
    val pixels = IntArray(w * h)
    for (y in 0 until h) {
        for (x in 0 until w) {
            pixels[y * w + x] = if (bitMatrix.get(x, y)) {
                0xff000000
            } else {
                0xffffffff
            }.toInt()
        }
    }
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
    load(bitmap)
}


fun RelativeLayout.captureView(window: Window, bitmapCallback: (Bitmap) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val location = IntArray(2)
        getLocationInWindow(location)
        PixelCopy.request(window, location.toRect(width, height), bitmap, {
            if (it == PixelCopy.SUCCESS) {
                bitmapCallback.invoke(bitmap)
            }
        }, Handler(Looper.getMainLooper()!!))
    } else {
        val tBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(tBitmap)
        draw(canvas)
        canvas.setBitmap(null)
        bitmapCallback.invoke(tBitmap)
    }
}

private fun IntArray.toRect(width: Int, height: Int) =
    Rect(this[0], this[1], this[0] + width, this[1] + height)


fun String.showPositiveDialog(context: Context, positiveMethod: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle("提示")
        .setMessage(this)
        .setCancelable(false)
        .setPositiveButton(context.getString(R.string.confirm)) { _, _ ->
            positiveMethod()
        }
        .show()
}

fun String.spellVideoUrl() = "http://staticvip.iyuba.cn/sounds/toefl/${this}.mp3"


fun Context.getLocalPath(type: String): File {
    val local = externalCacheDir?.absolutePath
    val separator = File.separatorChar
    val path = with(StringBuilder()) {
        append(local)
        append(separator)
        append(OtherUtils.folder)
        append(separator)
        append(type)
        append(".mp3")
        toString()
    }
    return File(path)
}


/**
 *  mkdirs()可以建立多级文件夹， mkdir()只会建立一级的文件夹
 * */
fun File.downLoadVideo(body: ResponseBody): String {
    absolutePath.let { path ->
        val flag = OtherUtils.folder
        val dirName = path.substring(0 until (path.indexOf(flag) + flag.length))
        File(dirName).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    //下载时没有事务性操作
    if (exists()) {
        delete()
    } else {
        createNewFile()
    }
    //关闭外层流的同时，内层流也会自动的进行关闭。关于内层流的关闭，可以省略
    val input = body.byteStream()
    val out = FileOutputStream(this)
    BufferedInputStream(input).use { inBuff ->
        BufferedOutputStream(out).use { outBuff ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = inBuff.read(buffer)
            while (bytes >= 0 && NonCancellable.isActive) {
                outBuff.apply {
                    write(buffer, 0, bytes)
                    flush()
                }
                bytes = inBuff.read(buffer)
            }
            return System.currentTimeMillis().timeStampDate()
        }
    }
}


fun String.replaceOtherSpace(): String {
    val space = " "
    val lecture = "Lecture${space}"
    val conversation = "Conversation${space}"
    val botany = "Botany"
    return when {
        contains(lecture) -> replace(lecture, lecture.replace(space, ""))
        contains(conversation) -> replace(conversation, conversation.replace(space, ""))
        contains("Batony") -> replace("Batony", botany)
        else -> this
    }
}


/**
 * 判断移动网络是否开启
 * */
fun Context.isDataEnabled() = try {
    val manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val method = manager.javaClass.getDeclaredMethod("getDataEnabled")
    method.invoke(manager) as Boolean
} catch (e: Exception) {
    false
}

fun String.getSpaceCount() = toCharArray().filter { it.code == 32 || it.code == 160 }.size

fun MutableMap<String, String>.putUserId(key: String = "userId") =
    put(key, GlobalHome.userInfo.uid.toString())

fun Context.gotoEvaluationInfo(
    userId: Int = GlobalHome.userInfo.uid,
    userName: String = GlobalHome.userInfo.username,
    userHead: String = GlobalHome.userInfo.imgSrc
) {
    startActivity<EvaluationInfoActivity> {
        putExtra(ExtraKeyFactory.userId, userId)
        putExtra(ExtraKeyFactory.userName, userName)
        putExtra(ExtraKeyFactory.userHead, userHead)
    }
}

/**
 * first-->播放类型
 * second-->开始播放时间
 * third-->播放地址
 * */
fun Pair<VoiceStatus, String>.changeTimeTriple() = Triple(first, getRecordTime(), second)


fun Int.changeTimeToString(): String {
    val second = this / 1000
    val end = (second % 60)
    return if (end >= 10) {
        (second / 60).toString() + ":" + end
    } else {
        (second / 60).toString() + ":" + "0" + end
    }
}

fun Context.startShareWorld(
    shareTitle: String,
    shareUrl: String,
    callbackOut: PlatformActionListener,
    icon: String = ""
) = with(
    OnekeyShare()
) {
    disableSSOWhenAuthorize()
    setTitle(shareTitle)
    setTitleUrl(shareUrl)
    text = shareTitle
    if (icon.isEmpty()) {
        setImageData(BitmapFactory.decodeResource(resources, R.drawable.icon))
    } else {
        setImagePath(icon)
    }
    setUrl(shareUrl)
    setSite(resources.getString(R.string.app_name))
    setSiteUrl(shareUrl)
    callback = callbackOut
    this
}

/**
 * 当角标 (it!=-1) 时即为Success
 * */
fun Int.findIndexSuccess() = (this != -1)

fun signDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE).format(Date())

fun Any.ofMap() =
    this::class.takeIf {
        it.isData
    }?.members?.filterIsInstance<KProperty<Any>>()?.map { it.name to it.call(this) }?.toMap()

fun FragmentActivity.clickSkipWeb(img: ImageView, otherOperate: () -> Unit = {}) {
    img.setOnClickListener {
        otherOperate.invoke()
        skipWeb("http://app.${OtherUtils.iyuba_cn}")
    }
}

fun MutableMap<String, String>.operateStrangeWord(pageCounts: Int = 10) {
    clear()
    put("u", GlobalHome.userInfo.uid.toString())
    put("pageNumber", "1")
    put("pageCounts", pageCounts.toString())
}

fun Activity.loadYouDao(
    img: ImageView,
    splashFlag: Boolean = true,
    success: () -> Unit = {},
    error: () -> Unit
) {
    val key = if (splashFlag) {
        "9755487e03c2ff683be4e2d3218a2f2b"
    } else {
        "230d59b7c0a808d01b7041c2d127da95"
    }
    val native = YouDaoNative(this, key, object : YouDaoNative.YouDaoNativeNetworkListener {
        override fun onNativeLoad(response: NativeResponse) {
            val imageUrls = mutableListOf<String>()
            imageUrls.add(response.mainImageUrl)
            img.setOnClickListener { response.handleClick(img) }
            if (!isFinishing) {
                ImageService.get(
                    this@loadYouDao,
                    imageUrls,
                    object : ImageService.ImageServiceListener {
                        override fun onSuccess(bitmaps: MutableMap<String, Bitmap>) {
                            if (response.mainImageUrl.isNotEmpty()) {
                                bitmaps[response.mainImageUrl]?.let {
                                    img.load(it)
                                    success.invoke()
                                    response.recordImpression(img)
                                }
                            }
                        }

                        override fun onFail() {
                            error.invoke()
                        }
                    })
            }
        }

        override fun onNativeFail(p0: NativeErrorCode) {
            error.invoke()
        }
    })
    val parameters = RequestParameters.RequestParametersBuilder().build()
    native.makeRequest(parameters)
}



