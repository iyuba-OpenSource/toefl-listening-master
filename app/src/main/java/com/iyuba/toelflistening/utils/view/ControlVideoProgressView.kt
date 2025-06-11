package com.iyuba.toelflistening.utils.view

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.iyuba.toelflistening.R
import com.iyuba.toelflistening.fragment.toelf.EvaluationFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class ControlVideoProgressView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {
    private val uiScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var controlVideo: RoundProgressBar
    private lateinit var player: MediaPlayer
    private var localStart by Delegates.notNull<Int>()
    private var localEnd by Delegates.notNull<Int>()
    private var isPause = false
    private lateinit var listenJob: Job

    private lateinit var videoProgressViewList: MutableList<ControlVideoProgressView>

    fun setControlVideoProgressView(videoProgressViewList: MutableList<ControlVideoProgressView>) {

        this.videoProgressViewList = videoProgressViewList
    }

    fun getMediaPlayer(): MediaPlayer {

        return player
    }

    private fun getPause(): Boolean {

        return isPause
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.control_video_progress, this)
        controlVideo = view.findViewById(R.id.control_video)
    }

    fun injectVideoUrl(url: String, start: Int, end: Float) {
        controlVideo.apply {
            inflateProgress(0)
            setBackgroundResource(R.drawable.play_evaluation_old)
            inflateMax(end.toInt() - start)
            setOnClickListener { videoClick(url) }
        }
        localStart = start
        localEnd = (end * 1000).toInt()
    }

    private fun initJob() = uiScope.launch {
        flow {
            while (true) {
                emit(0)
                delay(20)
            }
        }.collect {
            if (localEnd > 0) {
                post {
                    kotlin.runCatching {
                        timerDoTask()
                    }
                }
            }
        }
    }

    private val prepareListener = MediaPlayer.OnPreparedListener {
        player.start()
        player.seekTo(localStart * 1000)
        if (!::listenJob.isInitialized || listenJob.isCancelled) {
            listenJob = initJob()
            listenJob.start()
        }
    }


    private fun timerDoTask() {
        val end = (player.currentPosition - localEnd) > 1
        if (end) {
            player.pause()
            controlVideo.inflateProgress(0)
            controlVideo.setBackgroundResource(R.drawable.play_evaluation_old)
        } else {
            val progress = player.currentPosition - localStart * 1000
            controlVideo.inflateProgress(progress)
        }
    }

    fun stop() {
        if (::player.isInitialized && player.isPlaying) {
            player.pause()
            controlVideo.apply {
                inflateProgress(0)
                setBackgroundResource(R.drawable.play_evaluation_old)
                if (::listenJob.isInitialized) {
                    listenJob.cancel()
                }
            }
        }
    }

    fun pausePlayer() {
        if (::player.isInitialized && player.isPlaying) {
            player.pause()
        }
    }

    fun releasePlayer() {
        if (::player.isInitialized) {
            kotlin.runCatching {
                player.apply {
                    stop()
                    reset()
                    release()
                }
            }
        }
    }

    private fun videoClick(url: String) {


        if (EvaluationFragment.currentPosition != -1) {

            return
        }

        //当c为空时，正常运行；当c部不为空时，判断是否当前的view，是则正常执行，不是则忽略
        var c: ControlVideoProgressView? = null
        videoProgressViewList.also { videoProgressViewList ->

            videoProgressViewList.forEach {

                try {
                    if (it.player.isPlaying) {
                        c = it
                        return@also
                    }
                } catch (exception: UninitializedPropertyAccessException) {

                }
            }
        }

        if (c == null) {

            playAudio(url)
        } else if (c?.equals(this) == true) {

            playAudio(url)
        }
    }


    fun playAudio(url: String) {

        if (!::player.isInitialized) {
            player = with(MediaPlayer()) {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener(prepareListener)
                this
            }
            controlVideo.setBackgroundResource(R.drawable.pause_evaluation_old)
        } else if (!player.isPlaying) {

            if (!isPause) {
                player.apply {
                    reset()
                    setDataSource(url)
                    prepareAsync()
                    setOnPreparedListener(prepareListener)
                }
            } else {
                player.start()
                isPause = false
            }
            controlVideo.setBackgroundResource(R.drawable.pause_evaluation_old)
        } else {

            isPause = true
            pausePlayer()
            controlVideo.setBackgroundResource(R.drawable.play_evaluation_old)
        }
    }

}