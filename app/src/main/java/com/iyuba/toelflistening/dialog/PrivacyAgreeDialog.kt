package com.iyuba.toelflistening.dialog

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.utils.OnAgreePrivacyListener

/**
苏州爱语吧科技有限公司
@Date:  2022/9/28
@Author:  han rong cheng
 */
class PrivacyAgreeDialog(context: Context, listener: OnAgreePrivacyListener): AlertDialog.Builder(context) {
    init {
        val dialog= with(create()){
            setCancelable(false)
            show()
            this
        }
        dialog.window?.apply {
            setContentView(R.layout.dialog_initmate)
            setGravity(Gravity.CENTER)
            val tvContent = findViewById<TextView>(R.id.tv_content)
            val tvCancel = findViewById<TextView>(R.id.tv_not_agree)
            val tvAgree = findViewById<TextView>(R.id.tv_agree)
            val str = context.resources.getString(R.string.protocol_policy_long_long)
            val ssb = SpannableStringBuilder()
            ssb.append(str)
            val start = str.indexOf("《") //第一个出现的位置
            ssb.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    listener.seekProtocol()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(context,R.color.main)
                    ds.isUnderlineText = false
                }
            }, start, start + 6, 0)
            val end = str.lastIndexOf("《")
            ssb.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    listener.seekPrivacy()
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(context,R.color.main)
                    ds.isUnderlineText = false
                }
            }, end, end + 6, 0)
            tvContent.movementMethod = LinkMovementMethod.getInstance()
            tvContent.setText(ssb, TextView.BufferType.SPANNABLE)

            // 不同意用户协议和隐私政策
            tvCancel.setOnClickListener {
                listener.noAgree()
                dialog.dismiss()
            }

            // 同意用户协议和隐私政策
            tvAgree.setOnClickListener {
                // 初始化各种
                listener.agree()
                dialog.dismiss()
            }
        }
    }
}