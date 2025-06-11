package com.iyuba.toelflistening.dao.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iyuba.toelflistening.bean.LocalCollect
import kotlinx.coroutines.flow.Flow

/**
苏州爱语吧科技有限公司
 */
@Dao
interface CollectDao {
    @Insert
    fun insertWord(collect: LocalCollect):Long

    @Query("update LocalCollect set isCollect =:isCollect where word=:word")
    fun updateWord(isCollect:Boolean,word:String):Int

    @Query("select * from LocalCollect where word =:word")
    fun selectCollectByWord(word:String):Flow<List<LocalCollect>>

    @Query("select * from LocalCollect")
    fun selectAllCollect():List<LocalCollect>
}