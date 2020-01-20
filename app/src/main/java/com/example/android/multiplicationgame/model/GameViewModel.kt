package com.example.android.multiplicationgame.model

import android.app.Application
import android.preference.PreferenceManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.android.multiplicationgame.AppDatabase
import com.example.android.multiplicationgame.BALL_COUNT
import com.example.android.multiplicationgame.model.GameState.BEFORE_START
import com.example.android.multiplicationgame.view.SELECTED_LEVEL_KEY
import com.example.android.multiplicationgame.view.scores.HighScore
import com.example.android.multiplicationgame.view.scores.HighScoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalStateException

private const val NO_LEVEL_SET = 0

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val highScoreRepository: HighScoreRepository
    val allHighScores: LiveData<List<HighScore>>

    init {
        val highScoreDao = AppDatabase.getDatabase(application, viewModelScope).highScoreDao()
        highScoreRepository = HighScoreRepository(highScoreDao)
        allHighScores = highScoreRepository.allHighScores
    }

    fun insert(highScore: HighScore) = viewModelScope.launch(Dispatchers.IO) {
        highScoreRepository.insert(highScore)
    }


/*    var ballAnimationTime = 0L
    var gameState = GameState.BEFORE_START
    val score: Int = 0

    val level = PreferenceManager.getDefaultSharedPreferences(getApplication())
        .getInt(SELECTED_LEVEL_KEY, NO_LEVEL_SET).apply {
            if (this == NO_LEVEL_SET) {
                throw IllegalStateException("No game level set")
            }
        }

    val ballModels = Array(BALL_COUNT) { BallModel() }

    var ballLauncherStartTime = 0L
    var lastBallLaunchTime = 0L
    var ballLauncherInitialDelay = 0L
    var ballLauncherInterval = 0L
    var ballLauncherStopTime: Long = 0L*/

}