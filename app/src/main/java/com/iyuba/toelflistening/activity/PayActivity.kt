package com.iyuba.toelflistening.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.alipay.sdk.app.PayTask
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.databinding.ActivityPayBinding
import com.iyuba.toelflistening.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class PayActivity : BaseActivity<ActivityPayBinding>() {
    private lateinit var price: String
    private lateinit var amount: String

    /**
     * 是否为购买爱语币
     * */
    private var buyAiDollar = false

    override fun ActivityPayBinding.initBinding() {
        setTitleText("支付")
        price = intent?.getStringExtra(ExtraKeyFactory.payPrice).toString()
//        price= "0.01"
        binding.payChinaDollar = price + "元"
        binding.welcome = GlobalHome.userInfo.nickname

        binding.payTvAgreement.setOnClickListener(View.OnClickListener {

            var url =
                "http://iuserspeech.iyuba.cn:9001/api/vipServiceProtocol.jsp?company=1&type=app"

            val intent = Intent(this@PayActivity, UseInstructionsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("out_web_page",url)
            intent.putExtras(bundle)
            startActivity(intent)

        })

        val payType = intent.getStringExtra(ExtraKeyFactory.payType).toString()
        buyAiDollar = intent.getBooleanExtra(ExtraKeyFactory.buyType, false)
        binding.order = payType
        amount = with(payType) {
            if (buyAiDollar) {
                val buy = indexOf("买") + 1
                val love = indexOf("爱")
                substring(buy, love)
            } else {
                val startIndex = indexOf("员") + 1
                val endIndex = indexOf("个")
                substring(startIndex, endIndex)
            }
        }
        diyText()
        var markTime = 0L
        verifyPay.setOnClickListener {
            if (System.currentTimeMillis() - markTime > 3000) {
                markTime = System.currentTimeMillis()
                if (!isAliPayInstalled()) {
                    "请安装支付宝！".showDialog(this@PayActivity, method = {})
                    return@setOnClickListener
                }
                "是否去支付？".showDialog(this@PayActivity, "去支付") { alipay() }
            } else {
                "您点击的太快了。。。".showToast()
            }
        }
    }

    private fun diyText() {
        val zfbDesc = resources.getString(R.string.zfb_desc)
        val span = SpannableString(zfbDesc)
        val fore = ForegroundColorSpan(ContextCompat.getColor(this, R.color.text_gray))
        span.setSpan(fore, zfbDesc.indexOf("\n"), zfbDesc.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        binding.zfbPay.text = span
    }

    @OptIn(FlowPreview::class)
    private fun alipay() {
        val cate = if (buyAiDollar) {
            "爱语币"
        } else {
            binding.order.toString().let { it.substring(0, it.indexOf("员") + 1) }
        }
        val body = if (cate == "黄金会员") {
            val appName = getString(R.string.app_name)
            "${appName}-花费${price}元购买${appName}${cate}"
        } else {
            "花费${price}元购买${cate}"
        }
        lifecycleScope.launch {
            userAction.requestPayVip(price, amount, getProductId(cate), cate, body)
                .flatMapConcat {
                    val alipay = PayTask(this@PayActivity)
                    val ailiPayResult = alipay.payV2(it.alipayTradeStr, true).toString()
                    userAction.payVip(ailiPayResult)
                }.flowOn(Dispatchers.IO)
                .catch {
                    it.judgeType().showToast()
                }
                .collect {
                    if (it.isSuccess()) {
                        //先查本地，然后刷新
                        "开通成功！若未生效重新登陆即可。".showToast()
                        refreshUserInfo()
                        finish()
                    } else {
                        "开通失败".showToast()
                    }
                }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun isAliPayInstalled(): Boolean {
        val uri = Uri.parse("alipays://platformapi/startApp")
        val i = Intent(Intent.ACTION_VIEW, uri)
        val componentName = i.resolveActivity(packageManager)
        return componentName != null
    }

    private fun getProductId(subject: String) = when (subject) {
        "本应用会员" -> "10"
        "全站会员" -> "0"
        "黄金会员" -> "21"
        //爱语币为1
        else -> "1"
    }
}