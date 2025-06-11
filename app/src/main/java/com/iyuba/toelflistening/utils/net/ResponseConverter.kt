package com.iyuba.toelflistening.utils.net

import com.iyuba.toelflistening.utils.net.ConverterFormat

/**
苏州爱语吧科技有限公司
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ResponseConverter(val format: ConverterFormat = ConverterFormat.JSON)

