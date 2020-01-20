package com.example.android.multiplicationgame.view.executor

import android.animation.ValueAnimator
import android.view.View
import android.widget.TextView
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.log
import com.example.android.multiplicationgame.view.AnimatorComponent
import com.example.android.multiplicationgame.view.ComponentState

class QuestionHider(private val txtDisplay: TextView, private val parent: View): OneEvent0ParamComponent() {
    companion object {

        private const val DELAY_MS = 200L // TODO restore 200L
        private const val HIDE_DURATION_MS = 300L // TODO restore 300L
    }
    private var dx: Float = 0f // initialized in thisComponentOnLayout()

    private val animator = AnimatorComponent(
        ValueAnimator.ofFloat(0f, 1f).apply {
            startDelay = DELAY_MS
            duration = HIDE_DURATION_MS
            addUpdateListener {
                val value = animatedValue as Float
                txtDisplay.alpha = 1 - value
                txtDisplay.translationX = dx * value
            }
        }
    )

    init {
        animator.log("Created QuestionHider animator")
        animator.addAnimationEndListener {
            notifyListeners()
            this@QuestionHider.log("Notify")
        }
        addSubComponents(animator)
    }

    fun addQuestionHiddenListener(listener: () -> Unit) {
        addEvent1Listener(listener)
    }

    fun addQuestionHiddenListeners(vararg listeners: () -> Unit) {
        addEvent1Listeners(*listeners)
    }

    fun hideQuestion() = animator.start()

    fun removeDelayedActions() = animator.cancelDelayedAnimation()

    fun stop() = animator.end()

    override fun thisComponentOnLayout() {
        dx = parent.width.toFloat() / 2
    }

    override fun saveThisComponent() = ComponentState()

    override fun restoreThisComponent(state: ComponentState) = Unit

}