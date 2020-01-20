package com.example.android.multiplicationgame.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.log

/**
 * Component containing ValueAnimator. Wrapping animator in GameComponent allows to automatically
 * save/restore its state and pause/resume. The component will notify its listeners when the animation ends
 */
class AnimatorComponent(private val animator: ValueAnimator): OneEvent0ParamComponent() {

    companion object {
        private const val RUNNING_TAG = "RUNNING"
        private const val IN_DELAY_TAG = "IN_DELAY"
        private const val REMAINING_DELAY_TAG = "REMAINING_DELAY"
        private const val PLAY_TIME_TAG = "PLAY_TIME"
    }

    private val startDelay = animator.startDelay
    private var running = false // started and in delay period or already animating
    private var inDelayPeriod = false // started but still in delay period
    private var pausedTime = 0L
    private var startedTime = 0L
    private var remainingDelay = 0L
    private var listenersAreActive = true

    init {
        animator.startDelay = startDelay
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@AnimatorComponent.log("onAnimationEnd()")
                if (listenersAreActive) {
                    running = false
                    notifyListeners()
                }
            }
        })
    }

    fun addAnimationEndListener(listener: () -> Unit) {
        addEvent1Listener(listener)
    }

    fun addAnimationEndListeners(vararg listeners: () -> Unit) {
        addEvent1Listeners(*listeners)
    }

    fun start() {
        log("this.start()")
        running = true
        remainingDelay = startDelay
        animator.startDelay = startDelay
        startedTime = System.currentTimeMillis()
        log("Start animating, currentPlayTime==${animator.currentPlayTime}")
        animator.start()
    }

    /**
     * Cancels animation if it has been started but not yet running because still in delay period.
     * Listeners will not be notified of animation end.
     */
    fun cancelDelayedAnimation() {
        if (startDelay > 0 && running && !animator.isRunning) { // in delay period
            running = false
            listenersAreActive = false // prevent sending notifications to listeners
            animator.cancel()
            listenersAreActive = true
        }
    }

    /**
     * Stops the animation. It will immediately sets the end (final) animation frame.
     * Listeners will not be notified of animation end
     */
    fun end() {
        if (running) {
            running = false
            listenersAreActive = false // prevent sending notifications to listeners
            animator.end()
            listenersAreActive = true
        }
    }

    /**
     * Cancels the animation. It will immediately stop in its tracks. Unlike [end], current frame will
     * not be advanced to the end. Listeners will not be notified of animation end.
     */
    fun cancel() {
        if (running) {
            running = false
            listenersAreActive = false // prevent sending notifications to listeners
            animator.cancel()
            listenersAreActive = true
        }
    }

    override fun sleepThisComponent() {
        if (running) {
            if (animator.isRunning || startDelay == 0L) { // after delay period
                inDelayPeriod = false
                pausedTime = animator.currentPlayTime
                log("Set pausedTime=$pausedTime")
            } else {
                inDelayPeriod = true
                remainingDelay = Math.max(0L, remainingDelay - (System.currentTimeMillis() - startedTime))
            }

            listenersAreActive = false // prevent calling listeners when pausing animator
            animator.cancel()
            listenersAreActive = true
        }
    }

    override fun wakeThisComponent() {
        if (running) {
            if (inDelayPeriod) {
                animator.startDelay = remainingDelay
            } else {
                animator.startDelay = 0L
                animator.currentPlayTime = pausedTime
            }
            startedTime = System.currentTimeMillis()
            animator.start()
        }
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

    override fun saveThisComponent() = ComponentState().apply {
        put(RUNNING_TAG, running)
        if (running) {
            put(IN_DELAY_TAG, inDelayPeriod)
            if (inDelayPeriod) {
                put(REMAINING_DELAY_TAG, remainingDelay)
            } else {
                this@AnimatorComponent.log("Saving pausedTime=$pausedTime")
                put(PLAY_TIME_TAG, pausedTime)
            }
        }
    }

    override fun restoreThisComponent(state: ComponentState) {
        with (state) {
            running = getBoolean(RUNNING_TAG)
            if (running) {
                inDelayPeriod = getBoolean(IN_DELAY_TAG)
                if (inDelayPeriod) {
                    remainingDelay = getLong(REMAINING_DELAY_TAG)
                } else {
                    pausedTime = getLong(PLAY_TIME_TAG)
                    this@AnimatorComponent.log("Restored pausedTime=$pausedTime")
                }
            }
        }
    }
}