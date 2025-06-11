package com.iyuba.toelflistening.dao.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iyuba.toelflistening.bean.ReDoBean

/**
苏州爱语吧科技有限公司
@Date:  2023/1/31
@Author:  han rong cheng
 */
@Dao
interface ReDoRecordDao {
    @Query("select * from ReDoBean where testNumber =:number")
    fun selectByNumber(number:String):List<ReDoBean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRedo(item:ReDoBean)

    @Query("update ReDoBean set reDid=:flag where testNumber=:number")
    fun updateRedoStatus(number:String,flag:Boolean)
}