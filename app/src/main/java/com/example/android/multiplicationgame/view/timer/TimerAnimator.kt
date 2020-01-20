package com.example.android.multiplicationgame.view.timer

import android.animation.ValueAnimator
import com.example.android.multiplicationgame.view.ComponentState
import com.example.android.multiplicationgame.view.GameComponent

class TimerAnimator: GameComponent() {

    companion object {
        private const val CURRENT_PLAY_TIME_TAG = "PLAY_TIME"
    }

    fun addAnimatorListeners(vararg listeners: (currentPlayTime: Long) -> Unit) {
        listeners.forEach { animationListeners.add(it) }
    }

    private val animationListeners: MutableList<(currentPlayTime: Long) -> Unit> = ArrayList()

    private var listenersAreActive = true

    private val animator = ValueAnimator.ofInt(0).apply {
        duration = Long.MAX_VALUE
        interpolator = null
        addUpdateListener {
            if (listenersAreActive) {
                animationListeners.forEach { listener -> listener(it.currentPlayTime) }
            }
        }
    }

    val currentPlayTime
        get() = if (sleeping) pausedTime else animator.currentPlayTime

    private var pausedTime = animator.currentPlayTime

    fun reset() {
        if (sleeping) {
            pausedTime = 0L
            wake()
        } else {
            animator.currentPlayTime = 0L
        }
    }

    fun removeAnimatorListeners() {
        animationListeners.clear()
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(CURRENT_PLAY_TIME_TAG, pausedTime)
    }

    override fun restoreThisComponent(state: ComponentState) {
        pausedTime = state.getLong(CURRENT_PLAY_TIME_TAG)
    }

    override fun sleepThisComponent() {
        pausedTime = animator.currentPlayTime
        animator.cancel()
    }

    override fun wakeThisComponent() {
        setAnimatorPlayTime(pausedTime)
        animator.start()
    }

    override fun pauseThisComponent() {
        if (!sleeping) {
            sleepThisComponent()
        }
    }

    override fun resumeThisComponent() {
        if (!sleeping) {
            wakeThisComponent()
        }
    }

    private fun setAnimatorPlayTime(playTime:Long) {
        listenersAreActive = false
        animator.currentPlayTime = playTime
        listenersAreActive = true
    }

}