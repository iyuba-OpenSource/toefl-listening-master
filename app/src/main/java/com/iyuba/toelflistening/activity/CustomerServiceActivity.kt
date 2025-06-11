package com.iyuba.toelflistening.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.adapter.SettingAdapter
import com.iyuba.toelflistening.bean.SettingItem
import com.iyuba.toelflistening.databinding.ActivityCustomerServiceBinding
import com.iyuba.toelflistening.utils.addDefaultDecoration
import com.iyuba.toelflistening.utils.judgeType
import com.iyuba.toelflistening.utils.showToast
import kotlinx.coroutines.launch

class CustomerServiceActivity : BaseActivity<ActivityCustomerServiceBinding>() {
    private val phoneNumber="4008881905"
    private val list= mutableListOf<SettingItem>()
    override fun ActivityCustomerServiceBinding.initBinding() {
        setTitleText("联系客服")
        val setAdapter= with(SettingAdapter()){
            actionListener=clickListener
            this
        }
        customerList.apply {
            addDefaultDecoration()
            adapter=setAdapter
        }
        val phoneItem= with(SettingItem(R.drawable.phone,"客服电话：$phoneNumber")){
            temporaryQQ=phoneNumber
            this
        }
        list.apply{
            add(phoneItem)
            add(SettingItem(R.drawable.customer_service,"QQ客服"))
        }
        userAction.requestQQGroup()
        lifecycleScope.launch {
            userAction.lastQqResponse.collect{result->
                result.onSuccess {
                    val item= with(SettingItem(R.drawable.qq,"QQ群：${it.QQ}")){
                        temporaryQQ=it.key
                        this
                    }
                    list.add(item)
                    setAdapter.changeData(list)
                }.onError {
                    it.judgeType()?.showToast()
                }
            }
        }
        lifecycleScope.launch {
            userAction.lastCustomer.collect{result->
                result.onSuccess {
                    val numArray= arrayOf(it.editor,it.manager,it.technician)
                    val array= arrayOf(
                        "编辑QQ：${numArray[0]}",
                        "投诉QQ:${numArray[1]}",
                        "技术QQ：${numArray[2]}"
                    )
                    AlertDialog.Builder(this@CustomerServiceActivity)
                        .setTitle("提示")
                        .setItems(array) { _, position ->
                            startQQ(numArray[position])
                        }
                        .setPositiveButton("确定", null)
                        .show()
                }.onError {
                    it.judgeType()?.showToast()
                }
            }
        }
    }

    private val clickListener=Consumer<Int>{
        when(it){
            0 -> startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
            1 -> userAction.requestCustomerService()
            2 -> if (!joinQQGroup(list[it].temporaryQQ)) { "请安装QQ或检查QQ安装版本".showToast() }
        }
    }

    private fun joinQQGroup(key: String) = try {
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val intent = with(Intent()) {
            val qqKey = resources.getString(R.string.qq_key)
            data = Uri.parse(qqKey + key)
            this
        }
        startActivity(intent)
        true
    } catch (e: Exception) {
        // 未安装手Q或安装的版本不支持
        false
    }
    private fun startQQ(qq: Int) {
        try {
            val url = "mqqwpa://im/chat?chat_type=wpa&uin=$qq"
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(i)
        } catch (e: Exception) {
            "请安装QQ或检查QQ安装版本".showToast()
        }
    }


}