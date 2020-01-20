package com.example.android.multiplicationgame.view.timer

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.R
import com.example.android.multiplicationgame.view.ComponentState

class ProgressPresenter(activity: AppCompatActivity): OneEvent0ParamComponent() {

    companion object {
        private const val SEGMENT_COUNT = 10
        private const val LAST_SEGMENT_INDEX = SEGMENT_COUNT - 1

        private const val PROGRESS_TAG = "PROGRESS"
    }

    private val progressSegments = with(intArrayOf(
        R.id.progress0, R.id.progress1, R.id.progress2, R.id.progress3, R.id.progress4,
        R.id.progress5, R.id.progress6, R.id.progress7, R.id.progress8, R.id.progress9
    )) {
        Array(size) { ProgressSegmentComponent(activity.findViewById<View>(this[it])) }
    }

    private var progress = 0

    init {
        ProgressSegmentComponent.initialize(activity)
        progressSegments[LAST_SEGMENT_INDEX].addOnShownListener(this::notifyListeners) // last segment shown, game end
        addSubComponents(*progressSegments)
    }

    fun addFullProgressListener(fullProgressListener: () -> Unit) {
        addEvent1Listener(fullProgressListener)
    }

    fun addFullProgressListeners(vararg fullProgressListeners: () -> Unit) {
        addEvent1Listeners(*fullProgressListeners)
    }

    fun increaseProgress(level: Int) {
        if (progress < SEGMENT_COUNT) {
            progressSegments[progress++].show(level)
        }
    }

    fun clearProgress() {
        while (progress > 0) {
            progressSegments[--progress].hideImmediately()
        }
    }

    fun decreaseProgress() {
        if (progress > 0) {
            progressSegments[--progress].hide()
        }
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(PROGRESS_TAG, progress)
    }

    override fun restoreThisComponent(state: ComponentState) {
        progress = state.getInt(PROGRESS_TAG) // segments should restore themselves
    }
}