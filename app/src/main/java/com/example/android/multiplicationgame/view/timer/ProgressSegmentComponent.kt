package com.example.android.multiplicationgame.view.timer

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.R
import com.example.android.multiplicationgame.view.ComponentState
import com.example.android.multiplicationgame.view.timer.BallComponent.Companion.levelColors

class ProgressSegmentComponent(private val view: View): OneEvent0ParamComponent() {

    companion object {

        private lateinit var levelColors: IntArray

        fun initialize(activity: AppCompatActivity) {
            levelColors = with(intArrayOf(
                R.color.colorLevel_1,
                R.color.colorLevel_2,
                R.color.colorLevel_3,
                R.color.colorLevel_4,
                R.color.colorLevel_5,
                R.color.colorLevel_6,
                R.color.colorLevel_7,
                R.color.colorLevel_8,
                R.color.colorLevel_9,
                R.color.colorLevel_10
            )) {
                IntArray(size) { ContextCompat.getColor(activity, this[it]) }
            }
        }

        private const val VISIBLE_TAG = "VISIBILITY"
        private const val COLOR_TAG = "COLOR"
    }

    private var color = 0
        set(value) {
            field = value
            view.setBackgroundColor(value)
        }

    private var visible = false
        set(value) {
            field = value
            view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        }

    fun addOnShownListener(listener: () -> Unit) {
        addEvent1Listener(listener)
    }

    fun addOnShownListeners(vararg listeners: () -> Unit) {
        addEvent1Listeners(*listeners)
    }

    fun show(level: Int) {
        color = levelColors[level - 1]
        visible = true
        notifyListeners()
    }

    fun hide() {
        hideImmediately() // TODO pop-out
    }

    fun hideImmediately() {
        visible = false
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(VISIBLE_TAG, visible) // TODO apply animator for pop-out
        if (visible) {
            put(COLOR_TAG, color)
        }
    }

    override fun restoreThisComponent(state: ComponentState) {
        visible = state.getBoolean(VISIBLE_TAG)
        if (visible) {
            color = state.getInt(COLOR_TAG)
        }
    }
}