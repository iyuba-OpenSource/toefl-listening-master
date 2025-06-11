package com.iyuba.toelflistening.activity

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.adapter.WordSentenceAdapter
import com.iyuba.toelflistening.bean.CancelWordEvent
import com.iyuba.toelflistening.bean.LocalCollect
import com.iyuba.toelflistening.databinding.ActivityShowWordBinding
import com.iyuba.toelflistening.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import kotlin.properties.Delegates

class ShowWordActivity : BaseActivity<ActivityShowWordBinding>(), View.OnClickListener {
    private lateinit var player: MediaPlayer
    private var isDelete by Delegates.notNull<Boolean>()
    private lateinit var wordDefinition: String
    private lateinit var adapter: WordSentenceAdapter
    private val collectSuccess=0
    private val collectFail=1
    private val showDialog=2
    private val dismissDialog=3
    private val handler=Handler(Looper.myLooper()!!){
        when(it.what){
            collectSuccess->{
                changeStar(isDelete)

                if (isDelete) {
                    "收藏成功"
                } else {
                    EventBus.getDefault().post(CancelWordEvent())
                    "取消收藏成功"
                }.showToast()
            }
            collectFail->{
                "收藏失败\n${it.obj}".showToast()

            }
            showDialog->{

            }
            dismissDialog->{

            }
            5->{changeStar(isDelete)}
            6->{changeStar(false)}
        }
        true
    }
    override fun ActivityShowWordBinding.initBinding() {
        wordDefinition = intent.getStringExtra(ExtraKeyFactory.definitionWord).toString()
        isDelete=intent.getBooleanExtra(ExtraKeyFactory.listWord,false)
        binding.wordPlay.setOnClickListener(this@ShowWordActivity)
        binding.collect.setOnClickListener(this@ShowWordActivity)
        setTitleText("查词")
        adapter= WordSentenceAdapter()
        binding.wordSentenceList.adapter=adapter
        if (isDelete){
            changeStar(isDelete)
        }else{
            lifecycleScope.launch(Dispatchers.IO) {
                toeflViewModel.selectCollectByWord(wordDefinition).catch {
                    it.judgeType().showToast()
                }.collect {
                    val code = if (it.isNotEmpty()) {
                        isDelete = it[0].isCollect
                        5
                    } else { 6 }
                    handler.sendEmptyMessage(code)
                }
            }
        }
        lifecycleScope.launch {
            toeflViewModel.requestPickWord(wordDefinition)
                .catch {
                    it.judgeType().showToast()
                }.collect {
                    binding.word = it
                    changeTitle(it.key)
                    if (it.audio.isNotEmpty()) {
                        player = MediaPlayer()
                        player.setDataSource(it.audio)
                        player.prepareAsync()
                    }
                    adapter.changeData(it.sent)
                }
        }
    }

    private fun changeStar(flag: Boolean) {
        if (flag) {
            binding.collect.setBackgroundResource(R.drawable.star)
        } else {
            binding.collect.setBackgroundResource(R.drawable.un_star)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.standard_left -> finish()
            R.id.word_play -> judgePlayer { player.start() }
            R.id.collect -> changeCollectStatus()
        }
    }
    private fun changeCollectStatus(){
        if (!GlobalHome.isLogin()){
            showGoLoginDialog()
            return
        }
        if (disconnectNet()){
            return
        }
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                handler.sendObtained { what=showDialog }
                val netResult = toeflViewModel.changeCollectStatus(wordDefinition.changeEncode(), isDelete).first()
                if (netResult.result == 1) {
                    val localResult = toeflViewModel.selectCollectByWord(netResult.word).first()
                    if (localResult.isNotEmpty()) {
                        toeflViewModel.updateWord(localResult[0].reverseCollect()).first()
                        //换状态
                    } else {
                        //插入
                        toeflViewModel.insertWord(LocalCollect(netResult.word, !isDelete)).first()
                    }
                    isDelete = !isDelete
                    handler.sendEmptyMessage(0)
                } else {
                    handler.sendObtained { what=collectFail }
                }
            }
        } catch (e: Exception) {
            handler.sendObtained {
                what=collectFail
                obj=e.judgeType()
            }
        }
    }
    private fun judgePlayer(method: () -> Unit){
        if (::player.isInitialized){
            method()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        judgePlayer {
            if (player.isPlaying) player.stop()
            player.release()
        }
    }
    private fun Handler.sendObtained(block: Message.() -> Unit){
        val msg=obtainMessage()
        block.invoke(msg)
        sendMessage(msg)
    }
}