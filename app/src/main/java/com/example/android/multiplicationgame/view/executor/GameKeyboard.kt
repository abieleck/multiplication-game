package com.example.android.multiplicationgame.view.executor

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.android.multiplicationgame.OneEvent1ParamComponent
import com.example.android.multiplicationgame.R
import com.example.android.multiplicationgame.view.ComponentState

class GameKeyboard(activity: AppCompatActivity): OneEvent1ParamComponent<Int>() {

    companion object {
        const val BACKSPACE = 10
        private const val ACTIVE_TAG = "ACTIVE"
    }

    private val buttons = with(intArrayOf(
        R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4, R.id.button_5,
        R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9, R.id.backspace)
    ) {
        Array<Button>(this.size) { buttonNumber ->
            val buttonId = this[buttonNumber]
            activity.findViewById<Button>(buttonId).apply {
                setOnClickListener { notifyListeners(buttonNumber) }
            }
        }
    }

    var active: Boolean = true
        set(value) {
            field = value
            buttons.forEach { it.isEnabled = value }
        }

    fun addKeyPressedListener(keyPressedListener: (key: Int) -> Unit) {
        addEvent1Listener(keyPressedListener)
    }

    fun addKeyPressedListeners(vararg keyPressedListeners: (key: Int) -> Unit) {
        addEvent1Listeners(*keyPressedListeners)
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(ACTIVE_TAG, active)
    }

    override fun restoreThisComponent(state: ComponentState) {
        active = state.getBoolean(ACTIVE_TAG)
    }
}