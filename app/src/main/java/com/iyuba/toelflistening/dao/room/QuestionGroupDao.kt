package com.iyuba.toelflistening.dao.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iyuba.toelflistening.bean.QuestionDetailItem

/**
苏州爱语吧科技有限公司
 */
@Dao
interface QuestionGroupDao {

    @Query("select *from QuestionDetailItem where groupType=:type and position=:position")
    fun selectByType(type:String,position:Int):QuestionDetailItem

    @Insert
    fun insertSimple(list:List<QuestionDetailItem>)
}