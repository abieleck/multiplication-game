package com.example.android.multiplicationgame.model

data class BallTarget(
    val positionIndex: Int,
    val alpha: Float,
    val startTime: Long,
    val duration: Long,
    val onEnd: (() -> Unit)? = null
)