package com.iyuba.toelflistening.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.lifecycleScope
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.adapter.DataRecycleViewAdapter
import com.iyuba.toelflistening.bean.CancelWordEvent
import com.iyuba.toelflistening.databinding.ActivityStrangeWordBinding
import com.iyuba.toelflistening.utils.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class StrangeWordActivity :BaseActivity<ActivityStrangeWordBinding>(), StrangeListener{
    private lateinit var adapter: DataRecycleViewAdapter
    private lateinit var player: MediaPlayer
    override fun ActivityStrangeWordBinding.initBinding() {
        setTitleText("生词本")
        installsRight(R.drawable.pdf){
            toeflViewModel.requestStrangePdf()
        }
        requestPaging()
        lifecycleScope.launch {
            toeflViewModel.lastStrangePdf.collect{result->
                result.onSuccess {
                    if (it.result.findIndexSuccess()){
                        val url=it.filePath
                        val clipBoardManager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData= ClipData.newRawUri("concept_pdf", Uri.parse(url))
                        clipBoardManager.setPrimaryClip(clipData)
                        "${url}链接已复制".showPrivacyDialog(
                            this@StrangeWordActivity,
                            "PDF链接生成成功", "下载", getString(R.string.cancel), {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }, {})
                    }else{
                        it.filePath.showToast()
                    }
                    dismissLoad()
                }.onError {
                    dismissLoad()
                    it.judgeType().showToast()
                }.onLoading {
                    showLoad()
                }
            }
        }
    }

    private fun requestPaging(){
        adapter = with(DataRecycleViewAdapter()){
            listener = this@StrangeWordActivity
            this
        }
        binding.strangeList.adapter = adapter
        lifecycleScope.launch {
            toeflViewModel.requestStrangenessWord()
                .onStart {
                    showLoad()
                }.catch {
                    it.judgeType().showToast()
                    dismissLoad()
                }.collect {
                    changeViewStatus(GlobalHome.wordPagingEmpty)
                    dismissLoad()
                    adapter.submitData(it)
                }
        }
    }

    private fun changeViewStatus(flag:Boolean){
        binding.apply {
            strangeList.visibilityState(flag)
            wordEmptyHint.visibilityState(!flag)
        }
    }

    override fun wordDetailed(word: String) {
        startShowWordActivity(word, true)
    }

    override fun playVideo(url: String) {
        if (url.isEmpty()) {
            return
        }
        if (!::player.isInitialized) {
            player = MediaPlayer()
        }
        player.apply {
            setOnPreparedListener {
                start()
            }
            reset()
            setDataSource(url)
            prepareAsync()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: CancelWordEvent){
        requestPaging()
    }

    override fun initEventBus():Boolean =true

    override fun onPause() {
        super.onPause()
        if (!::player.isInitialized){
            return
        }
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!::player.isInitialized){
            return
        }
        runCatching {
            player.apply {
                stop()
                reset()
                release()
            }
        }
    }

}
