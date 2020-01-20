package com.example.android.multiplicationgame.view.executor

import android.animation.ValueAnimator
import android.view.View
import android.widget.TextView
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.view.AnimatorComponent
import com.example.android.multiplicationgame.view.ComponentState

class QuestionShower(private val txtDisplay: TextView, private val parent: View): OneEvent0ParamComponent() {

    companion object {
        private const val SHOW_DURATION_MS = 300L // TODO restore target 300
    }

    private var dx: Float = 0f // initialized in thisComponentOnLayout()

    private val animator = AnimatorComponent(
        ValueAnimator.ofFloat(-1f, 0f).apply {
            duration = SHOW_DURATION_MS
            addUpdateListener {
                val value = animatedValue as Float
                txtDisplay.alpha = Math.abs(value + 1)
                txtDisplay.translationX = dx * value
            }
        }
    )

    init {
        animator.addAnimationEndListener { notifyListeners() }
        addSubComponents(animator)
    }

    fun addQuestionShownListener(listener: () -> Unit) {
        addEvent1Listener(listener)
    }

    fun addQuestionShownListeners(vararg listeners: () -> Unit) {
        addEvent1Listeners(*listeners)
    }

    fun showQuestion(question: CharSequence) {
        txtDisplay.alpha = 0f
        txtDisplay.text = question

        animator.start()
    }

    fun stop() = animator.end()

    override fun thisComponentOnLayout() {
        dx = parent.width.toFloat() / 2
    }

    override fun saveThisComponent() = ComponentState()

    override fun restoreThisComponent(state: ComponentState) = Unit

}