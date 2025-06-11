package com.iyuba.toelflistening.utils.net

import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.lang.reflect.Type

/**
苏州爱语吧科技有限公司
 */
class JsonOrXmlConverterFactory: Converter.Factory() {
    private var jsonFactory: Converter.Factory
    private var xmlFactory: Converter.Factory

    init {
        val gson = GsonBuilder().serializeNulls().create()
        jsonFactory = GsonConverterFactory.create(gson)
        xmlFactory = SimpleXmlConverterFactory.createNonStrict()
    }

    companion object {
        fun create() = JsonOrXmlConverterFactory()
    }

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        for (annotation in annotations) {
            if (annotation is ResponseConverter) {
                if (annotation.format == ConverterFormat.JSON) {
                    return jsonFactory.responseBodyConverter(type, annotations, retrofit)
                } else if (annotation.format == ConverterFormat.XML) {
                    return xmlFactory.responseBodyConverter(type, annotations, retrofit)
                }
            }
        }
        return jsonFactory.responseBodyConverter(type, annotations, retrofit)
    }
}