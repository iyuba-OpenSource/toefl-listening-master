package com.iyuba.toelflistening.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.iyuba.toelflistening.databinding.ActivityUseInstructionsBinding
import com.iyuba.toelflistening.utils.ExtraKeyFactory

class UseInstructionsActivity : BaseActivity<ActivityUseInstructionsBinding> (){

    @SuppressLint("SetJavaScriptEnabled")
    override fun ActivityUseInstructionsBinding.initBinding() {
        outPage.webViewClient = webClient
        outPage.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            domStorageEnabled = true
        }
        intent?.getStringExtra(ExtraKeyFactory.outWebPage)?.let {
            outPage.loadUrl(it)
            outPage.settings.javaScriptEnabled=true
        }
    }

    private val webClient=object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return try {
                val url = request.url.toString()
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url)
                    true
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, request.url)
                    startActivity(intent)
                    true
                }
            } catch (e: Exception) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                false
            }
        }
    }
}