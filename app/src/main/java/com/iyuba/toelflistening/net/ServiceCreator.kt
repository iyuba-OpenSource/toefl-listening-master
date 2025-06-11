package com.iyuba.toelflistening.net

import com.iyuba.toelflistening.utils.net.JsonOrXmlConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit


object ServiceCreator {
    private const val BASE_URL = "http://m.iyuba.cn/ncet/"
    private const val timeOut = 3L



    private val client = OkHttpClient.Builder()
        .callTimeout(timeOut, TimeUnit.SECONDS)
        .readTimeout(timeOut, TimeUnit.SECONDS)
        .writeTimeout(timeOut, TimeUnit.SECONDS)
/*        .addInterceptor { chain ->
            val request = chain.request()
            chain.proceed(request)
        }*/
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(JsonOrXmlConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build()

    fun <T> create(service: Class<T>): T = retrofit.create(service)
    inline fun <reified T> create(): T = create(T::class.java)
}