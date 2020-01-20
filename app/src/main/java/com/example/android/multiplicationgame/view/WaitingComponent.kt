package com.example.android.multiplicationgame.view

import android.os.Handler
import com.example.android.multiplicationgame.OneEvent0ParamComponent

class WaitingComponent : OneEvent0ParamComponent() {

    companion object {
        private const val RUNNING_TAG = "RUNNING"
        private const val REMAINING_TIME_TAG = "REMAINING"
    }

    private val handler = Handler()

    private var running = false
    private var remainingTime = 0L
    private var restartTime = 0L
    private val endAction = Runnable {
        notifyListeners()
        running = false
    }

    fun addWaitingFinishedListener(listener: () -> Unit) {
        addEvent1Listener(listener)
    }

    fun addWaitingFinishedListeners(vararg listeners: () -> Unit) {
        addEvent1Listeners(*listeners)
    }

    fun startWaiting(duration: Long) {
        val currentTime = System.currentTimeMillis()
        remainingTime = if (running) {
            handler.removeCallbacks(endAction)
            Math.max(remainingTime - (currentTime - restartTime), duration)
        } else {
            running = true
            duration
        }
        restartTime = currentTime
        handler.postDelayed(endAction, remainingTime)

    }

    fun cancel() {
        if (running) {
            running = false
            handler.removeCallbacks(endAction)
        }
    }

    override fun sleepThisComponent() {
        if (running) {
            handler.removeCallbacks(endAction)
            remainingTime = Math.max(remainingTime - (System.currentTimeMillis() - restartTime), 0)
        }
    }

    override fun wakeThisComponent() {
        if (running) {
            restartTime = System.currentTimeMillis()
            handler.postDelayed(endAction, remainingTime)
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
            put(REMAINING_TIME_TAG, remainingTime)
        }
    }

    override fun restoreThisComponent(state: ComponentState) {
        running = state.getBoolean(RUNNING_TAG)
        if (running) {
            remainingTime = state.getLong(REMAINING_TIME_TAG)
        }
    }


}