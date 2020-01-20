package com.example.android.multiplicationgame.view.scores

class HighScoreService {
    fun getHighScores(timeoutMs: Int): List<HighScore>? {
        return listOf( // TODO mock
            HighScore(rank = 1, player = "ADAM", score = 2),
            HighScore(rank = 2, player = "KUBA", score = 1),
            HighScore(rank = 3, player = "KUBA", score = 0)
        )
    }

    fun addScore(score: Int, playerName: String) = Unit // TODO mock
}