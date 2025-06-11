package com.iyuba.toelflistening.activity

import android.graphics.Bitmap
import androidx.lifecycle.lifecycleScope
import cn.sharesdk.framework.Platform
import cn.sharesdk.framework.PlatformActionListener
import cn.sharesdk.onekeyshare.OnekeyShare
import cn.sharesdk.wechat.moments.WechatMoments
import coil.load
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.bean.SignResponse
import com.iyuba.toelflistening.databinding.ActivitySignBinding
import com.iyuba.toelflistening.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.*

class SignActivity : BaseActivity<ActivitySignBinding>(), PlatformActionListener {
    private lateinit var oks: OnekeyShare
    override fun ActivitySignBinding.initBinding() {
        item=GlobalHome.userInfo
        val day= Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH)
        val back="http://${OtherUtils.staticStr}${OtherUtils.iyuba_cn}/images/mobile/${day}.jpg"
        signBack.load(back)
        (intent.getSerializableExtra(ExtraKeyFactory.signResult) as SignResponse).apply {
            todayWords.text=getTodayWords()
            studyDays.text=getStudyDays()
            overPercent.text=getOverPercent()
            signQr.loadQRCode(qrIconUrl)
        }
        sign.setOnClickListener { startSign() }
        lifecycleScope.launch {
            userAction.shareWechatMomentsResult.collect{result->
                result.onLoading {
                    showLoad()
                }.onError {
                    dismissLoad()
                    it.judgeType().showToast()
                }.onSuccess {
                    dismissLoad()
                    refreshUserInfo()
                    it.getDialogDesc().showPositiveDialog(this@SignActivity){finish()}
                }
            }
        }
    }

    private fun startSign(){
        lifecycleScope.launch {
            flow {
                binding.sign.visibilityState(true)
                //莫名其妙的延时？？？
                delay(50)
                emit(0)
            }.onStart {
                showLoad()
            }.collect{
                binding.signLayout.captureView(window){
                    startShareWechatMoments(it)
                }
            }
        }
    }


    private fun startShareWechatMoments(bitmap: Bitmap){
        if (!::oks.isInitialized){
            oks= with(OnekeyShare()){
                disableSSOWhenAuthorize()
                setPlatform(WechatMoments.NAME)
                val titleText="我在${getString(R.string.app_name)}完成了打卡"
                text=titleText
                title=titleText
                setSilent(true)
                callback=this@SignActivity
                this
            }
        }
        oks.apply {
            setImageData(bitmap)
            show(this@SignActivity)
        }
    }

    override fun onComplete(p0: Platform?, p1: Int, p2: HashMap<String, Any>?) {
        dismissLoad()
        userAction.shareWechatMoments()
        binding.sign.visibilityState(false)
    }

    override fun onError(p0: Platform?, p1: Int, p2: Throwable?) {

        binding.sign.visibilityState(false)
    }

    override fun onCancel(p0: Platform?, p1: Int) {
        binding.sign.visibilityState(false)
    }

}