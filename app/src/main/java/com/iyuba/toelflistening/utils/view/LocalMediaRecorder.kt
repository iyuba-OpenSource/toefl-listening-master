package com.iyuba.toelflistening.utils.view

import android.media.MediaRecorder
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
苏州爱语吧科技有限公司
@Date:  2022/12/8
@Author:  han rong cheng
 */
class LocalMediaRecorder: MediaRecorder() {
    private var duration=0L
    private lateinit var job:Job

    companion object{
        const val timeMillis=10L
    }
    private var recording=false

    fun isRecording()=recording

    fun getCurrentPosition()=duration

    fun startPrepare(filePath:String, scope: LifecycleCoroutineScope, otherOperate: () -> Unit={}){
        setAudioSource(AudioSource.MIC)
        setOutputFormat(OutputFormat.MPEG_4)
        setAudioEncoder(AudioEncoder.AMR_NB)
        setOutputFile(filePath)
        prepare()
        start()
        recording=true
        otherOperate.invoke()
        job=scope.launch {
            flow {
                while (true){
                    emit(duration)
                    kotlinx.coroutines.delay(timeMillis)
                }
            }.collect{
                duration+=timeMillis
            }
        }
    }

    fun stopRecord(){
        recording=false
        setOnErrorListener(null)
        setOnInfoListener(null)
        setPreviewDisplay(null)
        stop()
        reset()
        duration=0
        if (::job.isInitialized){
            job.cancel()
        }
    }
}