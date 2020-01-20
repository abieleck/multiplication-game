package com.example.android.multiplicationgame.view.presenter

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.view.ComponentState

class BlinkingClickableComponent(private val view: View): OneEvent0ParamComponent() {

    companion object {
        private const val TAP_DURATION_MS = 1000L
        private const val ACCELERATOR_FACTOR = 2.0f

        private const val VISIBLE_TAG = "VISIBLE"
    }

    private val handAnimation = AlphaAnimation(0.0f, 1.0f).apply {
        interpolator = AccelerateInterpolator(ACCELERATOR_FACTOR)
        duration = TAP_DURATION_MS
        repeatMode = Animation.REVERSE
        repeatCount = Animation.INFINITE
    }
    var visible
        get() = this.view.visibility == View.VISIBLE
        set (value) {
            with(this.view) {
                if (value) {
                    visibility = View.VISIBLE
                    isEnabled = true
                    startAnimation(handAnimation)
                } else {
                    isEnabled = false
                    animation?.cancel()
                    visibility = View.INVISIBLE
                }
            }
        }

    init {
        this.view.setOnClickListener { notifyListeners() }
    }

    fun addOnClickListener(listener: () -> Unit) {
        addEvent1Listener(listener)
    }

    fun addOnClickListeners(vararg listeners: () -> Unit) {
        addEvent1Listeners(*listeners)
    }

    override fun pauseThisComponent() {
        this.view.animation?.cancel()
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(VISIBLE_TAG, visible)
    }

    override fun restoreThisComponent(state: ComponentState) {
        visible = state.getBoolean(VISIBLE_TAG)
    }
}