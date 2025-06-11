package com.iyuba.toelflistening.dao

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.iyuba.toelflistening.bean.*
import com.iyuba.toelflistening.dao.room.*

/**
苏州爱语吧科技有限公司
 */
@Database(
    version = 4,
    entities = [
        LocalCollect::class,
        QuestionDetailItem::class,
        TextItem::class,
        EvaluationSentenceDataItem::class,
        LikeEvaluation::class,
        ReDoBean::class
               ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun collectDao(): CollectDao
    abstract fun questionGroupDao(): QuestionGroupDao
    abstract fun localSentenceDao(): LocalSentenceDao
    abstract fun evaluationItemDao(): EvaluationItemDao
    abstract fun likeEvaluationDao(): LikeEvaluationDao
    abstract fun reDoDao(): ReDoRecordDao
    companion object{
        private var instance: AppDatabase?=null
        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {return it}
            return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java,"app_database")
                .allowMainThreadQueries()
                .build()
                .apply {
                    instance =this
                }
        }
    }
}