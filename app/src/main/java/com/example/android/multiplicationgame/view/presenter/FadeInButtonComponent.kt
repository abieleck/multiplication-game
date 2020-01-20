package com.example.android.multiplicationgame.view.presenter

import android.animation.ValueAnimator
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.view.AnimatorComponent
import com.example.android.multiplicationgame.view.ComponentState

class FadeInButtonComponent(private val button: Button): OneEvent0ParamComponent() {

    companion object {
        private const val ENABLED_TAG = "ENABLED"
        private const val ALPHA_TAG = "ALPHA"
    }

    private var btnAlpha = 0
        set(value) {
            field = value
            button.background.alpha = value
        }

    private val animator = AnimatorComponent(
        ValueAnimator.ofInt(0, 255).apply {
            interpolator = AccelerateInterpolator()
            duration = 3000L
            addUpdateListener {
                btnAlpha = it.animatedValue as Int
            }
        }
    )

    init {
        animator.addAnimationEndListeners({ button.isEnabled = true }, this::notifyListeners)
        addSubComponents(animator)
    }

    fun addFadedInListener(listener: () -> Unit) = addEvent1Listener(listener)

    fun addFadedInListeners(vararg listeners: () -> Unit) = addEvent1Listeners(*listeners)

    fun fadeIn() = animator.start()

    fun hide() {
        btnAlpha = 0
        button.isEnabled = false
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(ENABLED_TAG, button.isEnabled)
        put(ALPHA_TAG, btnAlpha)
    }

    override fun restoreThisComponent(state: ComponentState) {
        with(state) {
            button.isEnabled = getBoolean(ENABLED_TAG)
            btnAlpha = getInt(ALPHA_TAG)
        }
    }
}