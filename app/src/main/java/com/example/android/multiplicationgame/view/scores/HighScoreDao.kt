package com.example.android.multiplicationgame.view.scores

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HighScoreDao {

    @Query("SELECT * FROM HighScore ORDER BY rank")
    fun getAll(): LiveData<List<HighScore>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(highScore: HighScore)

    @Query("DELETE FROM HighScore")
    fun deleteAll()

}