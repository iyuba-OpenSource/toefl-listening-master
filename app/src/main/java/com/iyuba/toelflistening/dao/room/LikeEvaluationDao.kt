package com.iyuba.toelflistening.dao.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iyuba.toelflistening.bean.LikeEvaluation

/**
苏州爱语吧科技有限公司
 */
@Dao
interface LikeEvaluationDao {
    @Insert
    fun insertSimple(item: LikeEvaluation)

    @Query("select * from LikeEvaluation where userId=:userId and itemId=:itemId")
    fun selectSimple(userId:Int,itemId:Int):List<LikeEvaluation>
}