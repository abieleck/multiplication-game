package com.example.android.multiplicationgame.view.scores

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class HighScoreRepository(private val highScoreDao: HighScoreDao) {

    val allHighScores: LiveData<List<HighScore>> = highScoreDao.getAll()

    @WorkerThread
    suspend fun insert(highScore: HighScore) {
        highScoreDao.insert(highScore)
    }
}