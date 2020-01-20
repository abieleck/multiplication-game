package com.example.android.multiplicationgame.view.timer

class BallAnnihilator(ballManager: BallManager) {

    companion object {
        private const val ANNIHILATION_DURATION_MS = 500L
    }

    private val annihilateBallActionId = ballManager.registerFinishAction(ballManager::recycleBall)

    fun annihilate(ball: Ball) {
        ball.transform(0.0f, 2.0f, ANNIHILATION_DURATION_MS, annihilateBallActionId)
    }

}