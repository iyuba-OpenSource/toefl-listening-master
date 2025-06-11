package com.iyuba.toelflistening.utils

import android.widget.ImageView
import com.iyuba.toelflistening.bean.RankInfoItem
import com.iyuba.toelflistening.bean.TextItem
import com.iyuba.toelflistening.utils.view.RoundProgressBar
import com.iyuba.toelflistening.utils.view.SelectableTextView
import java.util.*

/**
苏州爱语吧科技有限公司
 */
abstract class OnWordClickListener {
    private var lastClickTime = 0L
    fun onClick(word: String) {
        val current = Calendar.getInstance().timeInMillis
        if (current - lastClickTime > 1000L) {
            lastClickTime = current
            onNoDoubleClick(word)
        }
    }

    protected abstract fun onNoDoubleClick(str: String)
}

interface SearchWordListener {
    fun searchListener(word:String,view: SelectableTextView)
}

interface StrangeListener{
    fun wordDetailed(word:String)
    fun playVideo(url:String)
}

interface OnEvaluationListener{
    fun showOperate(position:Int)
    fun openMike(position:Int,img: ImageView)
    fun playSelf(position:Int,img: RoundProgressBar)
    fun releaseSimple(bean: TextItem)
    fun searchWord(word:String,view: SelectableTextView,evalSuccess:Boolean,bean: TextItem)
}

interface OnEvaluationInfoOperateListener {
    fun playVideo(url:String)
    fun likeItem(item: RankInfoItem)
}

interface OnLoadDialogListener {
    fun showLoad()
    fun dismissLoad()
}

/**
 * 同意隐私时的监听
 * */
interface OnAgreePrivacyListener {
    fun seekPrivacy()
    fun seekProtocol()
    fun agree()
    fun noAgree()
}