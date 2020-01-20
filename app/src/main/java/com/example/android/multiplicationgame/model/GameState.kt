package com.example.android.multiplicationgame.model

enum class GameState(
    val keyboardEnabled: Boolean,
    val btnStartVisible: Boolean,
    val ballLauncherState: BallLauncherState) {

    BEFORE_START(false, true, BallLauncherState.STOPPED),
    WAIT_FOR_QUESTION(false, false, BallLauncherState.RUNNING),
    PAUSED_WAIT_FOR_QUESTION(false, false, BallLauncherState.PAUSED);

    enum class BallLauncherState {
        STOPPED,
        PAUSED,
        RUNNING
    }

    var nextOnPause: GameState? = null
        private set(value) {
            field = value
        }

    var nextOnResume: GameState? = null
        private set(value) {
            field = value
        }

    companion object {
        init {
            BEFORE_START.nextOnPause = BEFORE_START
            BEFORE_START.nextOnResume = WAIT_FOR_QUESTION

            WAIT_FOR_QUESTION.nextOnPause = PAUSED_WAIT_FOR_QUESTION
            PAUSED_WAIT_FOR_QUESTION.nextOnResume = WAIT_FOR_QUESTION
        }
    }




}