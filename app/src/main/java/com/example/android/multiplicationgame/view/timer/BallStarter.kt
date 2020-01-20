package com.example.android.multiplicationgame.view.timer

import com.example.android.multiplicationgame.view.ComponentState
import com.example.android.multiplicationgame.view.GameComponent
import com.example.android.multiplicationgame.view.timer.BallManager.Companion.NO_ACTION
import com.example.android.multiplicationgame.view.timer.BallManager.Companion.LAST_BALL_POSITION

class BallStarter(private val ballManager: BallManager, var level: Int): GameComponent() {

    companion object {
        val CREATE_POSITION = LAST_BALL_POSITION
        val LAUNCH_POSITION = CREATE_POSITION - 1

        private const val LEVEL_TAG = "LEVEL"
        private const val BALL_ID_TAG = "BALL_ID"
    }

    private lateinit var ballAtStart: Ball

    private var ballIsInitialized = false

    fun getBall(): Ball = ballAtStart.also { ballAtStart = getNewBallAtStart() }

    fun reset() {
        ballAtStart = getNewBallAtStart()
        ballIsInitialized = true
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(LEVEL_TAG, level)
        put(BALL_ID_TAG, ballAtStart.id)
    }

    override fun restoreThisComponent(state: ComponentState) {
        level = state.getInt(BALL_ID_TAG)
        val ballId = state.getInt(BALL_ID_TAG)
        ballAtStart = ballManager.getBall(ballId)!!
        ballIsInitialized = true
    }

    override fun resumeThisComponent() {
        if (!ballIsInitialized) {
            reset()
        }
    }

    private fun getNewBallAtStart() = ballManager.createBall(CREATE_POSITION, 0.0f, 1.0f, level).apply {
        move(ballManager.currentPlayTime, LAUNCH_POSITION, 1.0f, finishActionId = NO_ACTION)
    }


}