package com.example.android.multiplicationgame

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.android.multiplicationgame.view.scores.HighScore
import com.example.android.multiplicationgame.view.scores.HighScoreDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [HighScore::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun highScoreDao(): HighScoreDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Mult_game_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.highScoreDao())
                }
            }
        }

        suspend fun populateDatabase(highScoreDao: HighScoreDao) {
            highScoreDao.deleteAll()

            highScoreDao.apply {
                insert(HighScore(rank = 1, player = "Adam", score = 355))
                insert(HighScore(rank = 2, player = "Adam", score = 234))
                insert(HighScore(rank = 3, player = "Kuba", score = 224))
                insert(HighScore(rank = 4, player = "Tata", score = 33))
                insert(HighScore(rank = 5, player = "Ata", score = 21))
            }
        }
    }

}