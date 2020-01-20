package com.example.android.multiplicationgame.view.timer

import com.example.android.multiplicationgame.view.ComponentState

class BallTransformation(
    private val startPosition: Int,
    val endPosition: Int,
    val alphaChange: Float,
    val scaleChange: Float,
    private val startTime: Long,
    private val duration: Long,
    val finishActionId: Int) {

    companion object {
        private const val START_POSITION_TAG = "START_POSITION"
        private const val END_POSITION_TAG = "END_POSITION"
        private const val ALPHA_CHANGE_TAG = "ALPHA"
        private const val SCALE_CHANGE_TAG = "SCALE"
        private const val START_TIME_TAG = "START_TIME"
        private const val DURATION_TAG = "DURATION"
        private const val FINISH_ACTION_TAG = "ON_FINISH"
    }

    constructor(state: ComponentState): this(
        state.getInt(START_POSITION_TAG),
        state.getInt(END_POSITION_TAG),
        state.getFloat(ALPHA_CHANGE_TAG),
        state.getFloat(SCALE_CHANGE_TAG),
        state.getLong(START_TIME_TAG),
        state.getLong(DURATION_TAG),
        state.getInt(FINISH_ACTION_TAG)
    )

    val endTime
        get() = startTime + duration

    fun isFinished(currentPlayTime: Long) = currentPlayTime >= startTime + duration

    fun calculateXChange(currentPlayTime: Long, coordinates: FloatArray) =
        calculateChange(coordinates[endPosition] - coordinates[startPosition], currentPlayTime)

    fun calculateAlphaChange(currentPlayTime: Long) = calculateChange(alphaChange, currentPlayTime)

    fun calculateScaleChange(currentPlayTime: Long) = calculateChange(scaleChange, currentPlayTime)

    private fun calculateChange(ultimateChange: Float, currentPlayTime: Long) = when {
        currentPlayTime >= endTime ->
            ultimateChange
        currentPlayTime <= startTime ->
            0f
        else ->
            ultimateChange * interpolate((currentPlayTime - startTime).toFloat() / duration.toFloat())
    }

    private fun interpolate(t: Float) = ((Math.cos((t + 1) * Math.PI) / 2.0f) + 0.5f).toFloat()

    fun getState(): ComponentState = ComponentState().apply {
        put(START_POSITION_TAG, startPosition)
        put(END_POSITION_TAG, endPosition)
        put(ALPHA_CHANGE_TAG, alphaChange)
        put(SCALE_CHANGE_TAG, scaleChange)
        put(START_TIME_TAG, startTime)
        put(DURATION_TAG, duration)
        put(FINISH_ACTION_TAG, finishActionId)
    }

}