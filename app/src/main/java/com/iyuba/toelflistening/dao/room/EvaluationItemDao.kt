package com.iyuba.toelflistening.dao.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iyuba.toelflistening.bean.EvaluationSentenceDataItem

@Dao
interface EvaluationItemDao {
    @Insert
    fun insertEvaluation(resultList: List<EvaluationSentenceDataItem>)

    @Query("select * from EvaluationSentenceDataItem where userId=:userId and groupNum=:groupNum")
    fun selectEvaluationList(userId:Int,groupNum:Int):List<EvaluationSentenceDataItem>

    @Query("select * from EvaluationSentenceDataItem where onlyKay=:onlyKey")
    fun selectEvaluationByKey(onlyKey:String):List<EvaluationSentenceDataItem>

    @Query("delete from EvaluationSentenceDataItem where onlyKay=:onlyKey")
    fun deleteSentenceDataItemByKey(onlyKey:String)

    @Query("update EvaluationSentenceDataItem set score=:score where onlyKay=:onlyKey and `index`=:index")
    fun updateEvaluationChildStatus(score:Float,onlyKey: String,index:Int)
}