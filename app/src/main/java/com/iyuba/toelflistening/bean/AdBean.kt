package com.iyuba.toelflistening.bean

data class AdResponse(
    val result:Int,
    val data:AdItem
)


data class AdItem(
    val id:String,
    val adId:String,
    val startuppic_StartDate:String,
    val startuppic_EndDate:String,
    val startuppic:String,
    val type:String,
    val startuppic_Url:String,
    val classNum:String,
    val title:String,
)