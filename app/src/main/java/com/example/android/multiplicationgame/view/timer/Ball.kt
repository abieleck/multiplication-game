package com.example.android.multiplicationgame.view.timer

interface Ball {
    val id: Int
    val ultimatePosition: Int
    val ultimateAlpha: Float
    val ultimateScale: Float

    fun move(
        moveStartTime: Long,
        targetPosition: Int,
        targetAlpha: Float = ultimateAlpha,
        targetScale: Float = ultimateScale,
        finishActionId: Int)

    fun transform(
        targetAlpha: Float = ultimateAlpha,
        targetScale: Float = ultimateScale,
        duration: Long,
        finishedActionId: Int)

}