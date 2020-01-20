package com.example.android.multiplicationgame.view.timer

import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.classTag
import com.example.android.multiplicationgame.log
import com.example.android.multiplicationgame.view.ComponentState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToLong

class TimeImpulseGenerator(private val initialLevel: Int): OneEvent0ParamComponent() {

    companion object {
        private const val NO_IMPULSES_YET = 0L
        private val INTERVALS = longArrayOf(3000L, 2500L, 2000L, 1500L, 1000L, 800L, 600L, 500L, 400L, 300L)

        private const val LEVEL_TAG = "LEVEL"
        private const val DELAY_TAG = "DELAY"
        private const val RUNNING_TAG = "RUNNING"
    }

    private var disposable: Disposable? = null

    private var runId = 0 // increased on every deactivation so scheduled listeners can abort if started with different
    private var startTime = 0L
    private var lastImpulseTime = 0L
    private var initialDelay = 0L
    private var level = initialLevel
    private var running = false

    fun addTimeImpulseListener(listener: () -> Unit) {
        addEvent1Listener(listener)
    }

    fun addTimeImpulseListeners(vararg listeners: () -> Unit) {
        addEvent1Listeners(*listeners)
    }

    fun setLevel(newLevel: Int) {
        deactivate(newLevel)
        level = newLevel
        resume()
    }

    fun start() {
        log("start()")
        if (!running) {
            running = true
            level = initialLevel
            initialDelay = INTERVALS[level - 1]
            wakeThisComponent()
        }
    }

    fun stop() {
        if (running) {
            disposeDisposable()
            running = false
        }
    }

    override fun sleepThisComponent() {
        if (running) {
            deactivate()
        }
    }

    override fun wakeThisComponent() {
        if (running) {
            lastImpulseTime = NO_IMPULSES_YET
            val currentRunId = runId
            disposable = Observable.interval(initialDelay, INTERVALS[level - 1], TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    disposable?.log("subscriber fired")
                    if (currentRunId == runId) {
                        lastImpulseTime = System.currentTimeMillis()
                        notifyListeners()
                    }
                }
            log("Created ${disposable?.classTag()}")
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

    private fun deactivate(levelAfterActivation: Int = level) {
        val currentTime = System.currentTimeMillis()
        disposeDisposable()
        initialDelay = when {
            notLaunchedYet() ->
                max(initialDelay - (currentTime - startTime), 0)
            levelAfterActivation == level ->
                max(INTERVALS[levelAfterActivation - 1] - (currentTime - lastImpulseTime), 0)
            else -> {
                val oldInterval = INTERVALS[level - 1]
                val newInterval = INTERVALS[levelAfterActivation - 1].toFloat()
                val delayForOldInterval = max(oldInterval - (currentTime - lastImpulseTime), 0).toFloat()

                (newInterval * (delayForOldInterval / oldInterval.toFloat())).roundToLong()
            }
        }
    }

    private fun notLaunchedYet() = lastImpulseTime == NO_IMPULSES_YET

    private fun disposeDisposable() {
        runId++ // listener actions scheduled in the meantime for main thread will see a different ID and abort
        log("${disposable?.classTag()}.dispose()")
        disposable?.dispose()
        disposable = null
    }

    override fun saveThisComponent(): ComponentState = ComponentState().apply {
        put(RUNNING_TAG, running)
        if (running) {
            put(LEVEL_TAG, level)
            put(DELAY_TAG, initialDelay)
        }
    }

    override fun restoreThisComponent(state: ComponentState) {
        with (state) {
            running = getBoolean(RUNNING_TAG)
            if (running) {
                level = getInt(LEVEL_TAG)
                initialDelay = getLong(DELAY_TAG)
            }
        }
    }
}