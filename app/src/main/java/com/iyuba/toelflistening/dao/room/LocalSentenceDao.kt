package com.iyuba.toelflistening.dao.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iyuba.toelflistening.bean.TextItem

@Dao
interface LocalSentenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSentence(sentenceResult:List<TextItem>)

    @Query("select * from TextItem where userId=:userId and titleNum=:titleNum")
    fun selectSentenceList(userId:Int,titleNum:String):List<TextItem>

    @Query("update TextItem set onlyKay=:onlyKey,success=:success,fraction=:fraction,selfVideoUrl=:url where userId=:userId and titleNum=:voaId and senIndex=:index")
    fun updateEvaluationSentenceItemStatus(userId: Int,voaId: Int,index:Int,onlyKey:String,success:Boolean,fraction:String,url:String)

    @Query("select * from TextItem where titleNum=:voaId and senIndex=:idIndex and userId=:userId")
    fun selectSimpleEvaluation(voaId:String,idIndex:Int,userId:String):List<TextItem>
}