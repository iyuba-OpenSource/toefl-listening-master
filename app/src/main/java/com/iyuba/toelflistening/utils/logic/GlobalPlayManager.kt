package com.iyuba.toelflistening.utils.logic

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.iyuba.toelflistening.utils.changeTimeTriple
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
苏州爱语吧科技有限公司
@Date:  2022/12/14
@Author:  han rong cheng
控制全局的音频播放
 */
object GlobalPlayManager {
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private lateinit var player: ExoPlayer
    //是否有必要写成HashMap<自定义类型,播放地址>()呢？HashMap不能放重复key
    private val pathQueue = mutableListOf<Triple<VoiceStatus,String,String>>()
    private lateinit var backPair:Pair<VoiceStatus,String>
    private var backPlayProgress=0L
    private val statusFlow= MutableSharedFlow<ExoStatus>()
    val statusOutFlow= statusFlow.asSharedFlow()
    //currentState? currentUrlPath
    fun preparePlayer(context: Context) {
        if (isNotInit()){
            player = with(ExoPlayer.Builder(context).build()){
                addListener(exoListener)
                this
            }
        }
    }
    const val STATE_PLAYING=5
    const val STATE_NOT_PLAYING=6

    /**
     * 播放器是否被初始化
     * */
    private fun isNotInit()= (!GlobalPlayManager::player.isInitialized)

    /**
     * 立即播放
     * */
    fun addUrl(pair: Pair<VoiceStatus,String>){
        if (isNotInit() ||pair.second.isEmpty()){
            return
        }
        //此处先判断backPair是否为VoiceStatus.BACK_PLAY,如果是的话，保存pair，否的话不做操作
        if (pair.first== VoiceStatus.BACK_PLAY){
            if (GlobalPlayManager::backPair.isInitialized){
                backPlayProgress = getCurrentPosition()
            }
            backPair =pair
        }
        pathQueue.add(pair.changeTimeTriple())
        pathQueue.forEach {
            Timber.tag("pathQueue_item").d("addUrl: _____________%s", it)
        }
        backPair =pair
        player.apply {
            setMediaItem(MediaItem.fromUri(pair.second))
            prepare()
            play()
        }
    }

    /**
     * 触发onDestroy生命周期时
     * */
    fun executeDestroy(){
        if (isNotInit()){
            return
        }
        player.stop()
    }

    /**
     * 播放器当前进度
     * */
    fun getCurrentPosition():Long= (if (isNotInit()) 0 else player.currentPosition)

    fun seekTo(position:Long){
        if (isNotInit()){
            return
        }
        player.seekTo(position)
    }

    fun isEnd() = if (isNotInit()) {
        true
    } else {
        with(player) {
            (duration - currentPosition) < 100
        }
    }

    fun isPlaying()= (if (isNotInit()) false else player.isPlaying)

    /**
     * 既没playing，又没completed不就是pause?
     * */
    fun isPause() =(if (isNotInit()) false else !(isPlaying() || isEnd()))


    fun pause(){
        if (isNotInit()){
            return
        }
        player.pause()
    }

    fun getDuration() =(if (isNotInit()) 0L else player.duration)

    fun start(){
        if (isNotInit()){
            return
        }
        player.play()
    }

    fun getCurrentType() = with(pathQueue) {
        if (isNotEmpty()) {
            this[0].first
        } else {
            VoiceStatus.ERROR
        }
    }

    private fun emitState(stateCode:Int){
        uiScope.launch {
            statusFlow.emit(ExoStatus(stateCode))
        }
    }

    /**
     *  ExoPlayer的不同状态
     * */
    data class ExoStatus(val code:Int){
        /**
         * MediaPlayer的OnCompleted
         * */
        inline fun onEnded(method:(state: VoiceStatus)->Unit): ExoStatus {
            if (code==Player.STATE_ENDED){
                method.invoke(getCurrentType())
            }
            return this
        }
        inline fun onIdle(method:()->Unit): ExoStatus {
            if (code==Player.STATE_IDLE){
                method.invoke()
            }
            return this
        }
        inline fun onBuffering(method:()->Unit): ExoStatus {
            if (code==Player.STATE_BUFFERING){
                method.invoke()
            }
            return this
        }
        /**
         * MediaPlayer的OnPrepared
         * */
        inline fun onReady(method:(state: VoiceStatus)->Unit): ExoStatus {
            if (code==Player.STATE_READY){
                method.invoke(getCurrentType())
            }
            return this
        }

        /**
         * 主要用来控制切换图片
         * */
        inline fun onIsPlaying(method:(pair:Pair<VoiceStatus,Boolean>)->Unit): ExoStatus {
            if (code== STATE_PLAYING){
                method.invoke(Pair(getCurrentType(),true))
            }
            return this
        }

        /**
         * 主要用来控制切换图片
         * */
        inline fun onNotPlaying(method:(pair:Pair<VoiceStatus,Boolean>)->Unit): ExoStatus {
            if (code== STATE_NOT_PLAYING){
                method.invoke(Pair(getCurrentType(),false))
            }
            return this
        }
    }

    private val exoListener=object:Player.Listener{
        override fun onPlaybackStateChanged(playbackState: Int) {
            emitState(playbackState)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            val playState=(if (isPlaying) STATE_PLAYING else STATE_NOT_PLAYING)
            emitState(playState)
        }
    }
}