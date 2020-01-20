package com.example.android.multiplicationgame.view.presenter

import android.animation.ValueAnimator
import android.view.View
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.view.AnimatorComponent
import com.example.android.multiplicationgame.view.ComponentState

class PoppingView(private val view: View, private val popInDuration: Long, private val scaleIncrease: Float):
    OneEvent0ParamComponent() {

    companion object {
        private const val VISIBILITY_TAG = "VISIBILITY"
        private const val SCALE_TAG = "SCALE"
    }

    private val animator = AnimatorComponent(
        ValueAnimator.ofFloat(0f, 2 * scaleIncrease).apply {
            this.duration = popInDuration
            addUpdateListener {
                val value = animatedValue as Float
                val scale = if (value <= scaleIncrease) 1 + value else 1 + 2 * scaleIncrease - value
                view.scaleX = scale
                view.scaleY = scale
            }
        }
    )

    init {
        animator.addAnimationEndListener(this::notifyListeners)
        addSubComponents(animator)
    }

    fun addPoppedInListener(listener: () -> Unit) {
        addEvent1Listener(listener)
    }

    fun addPoppedInListeners(vararg listeners: () -> Unit) {
        addEvent1Listeners(*listeners)
    }

    fun popIn() {
        view.visibility = View.VISIBLE
        animator.start()
    }

    fun hide() {
        view.visibility = View.INVISIBLE
        view.scaleX = 1f
        view.scaleY = 1f
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(VISIBILITY_TAG, view.visibility)
        put(SCALE_TAG, view.scaleX)
    }

    override fun restoreThisComponent(state: ComponentState) {
        view.visibility = state.getInt(VISIBILITY_TAG)
        state.getFloat(SCALE_TAG).also {
            view.scaleX = it
            view.scaleY = it
        }
    }

}