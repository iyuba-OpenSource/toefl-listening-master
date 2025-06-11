package com.iyuba.toelflistening.viewmodel

import androidx.lifecycle.viewModelScope
import com.iyuba.toelflistening.AppClient
import com.iyuba.toelflistening.Repository
import com.iyuba.toelflistening.bean.AdItem
import com.iyuba.toelflistening.utils.net.FlowResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AdvertiseViewModel : BaseViewModel() {

    private val adResult = MutableSharedFlow<FlowResult<AdItem>>(10, 10)

    val lastAdResult = adResult.asSharedFlow()

    fun requestAdType(bannerFlag: Boolean = false) {
        val flag = (if (bannerFlag) "4" else "1")
        viewModelScope.launch {
            Repository.getLoginInfo().flatMapMerge {
                dataMap.apply {
                    clear()
                    put("uid", it.uid.toString())
                    putAppId("appId")
                    put("flag", flag)
                }

                var dataMap2 = mutableMapOf<String, String>()
                dataMap2.apply {
                    clear()
                    put("uid", it.uid.toString())
                    put("appId", AppClient.adAppId.toString())
                    put("flag", flag)
                }
                Repository.requestAdType(adUrl, dataMap2)
            }.onStart {
                adResult.emit(FlowResult.Loading())
            }.catch {
                adResult.emit(FlowResult.Error(it))
            }.collect {
                if (it.isEmpty()) {
                    adResult.emit(FlowResult.Error(Throwable()))
                } else {

                    val adResponse = it[0]
                    if (adResponse.result == 1) {

                        adResult.emit(FlowResult.Success(it[0].data))
                    } else {

                        adResult.emit(FlowResult.Error(Throwable()))
                    }
                }
            }
        }
    }

}