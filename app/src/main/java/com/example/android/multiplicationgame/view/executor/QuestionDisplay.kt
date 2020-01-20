package com.example.android.multiplicationgame.view.executor

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.clearSpans
import androidx.core.view.updatePadding
import com.example.android.multiplicationgame.OneEvent2ParamComponent
import com.example.android.multiplicationgame.R
import com.example.android.multiplicationgame.log
import com.example.android.multiplicationgame.view.ComponentState
import com.example.android.multiplicationgame.view.executor.GameKeyboard.Companion.BACKSPACE

class QuestionDisplay(activity: AppCompatActivity): OneEvent2ParamComponent<Int, Int>() {

    companion object {
        private const val ACTIVE_TAG = "ACTIVE"
        private const val DISPLAY_TEXT_TAG = "DISPLAY_TEXT"
        private const val QUESTION_LENGTH_TAG = "QUESTION_LENGTH"
        private const val QUESTION_SCHEDULED_TAG ="SCHEDULED"
        private const val FACTOR1_TAG = "FACTOR1"
        private const val FACTOR2_TAG = "FACTOR2"
        private const val SCHEDULED_FACTOR_1_TAG = "SCHEDULED1"
        private const val SCHEDULED_FACTOR_2_TAG = "SCHEDULED2"
        private const val HIGHLIGHT_TYPE_TAG = "HIGHLIGHT"

        private const val WRONG_ANSWER_HIGHLIGHT_COLOR = Color.RED
        private const val CORRECT_ANSWER_HIGHLIGHT_COLOR = Color.GREEN // TODO adjust color to darker

        private const val ANSWER_HIGHLIGHTED_CORRECT = 1
        private const val ANSWER_HIGHLIGHTED_INCORRECT = -1
        private const val ANSWER_NOT_HIGHLIGHTED = 0

        private const val QUESTION_AND_ANSWER_MAX_LENGTH = 9
    }

    var active = true
        set(value) {
            field = value
            if (!value) {
                hider.removeDelayedActions()
            }
        }

    private val txtDisplay = activity.findViewById<TextView>(R.id.display)
    private val rootView  = activity.findViewById<View>(android.R.id.content)

    private val shower = QuestionShower(txtDisplay, rootView)
    private val hider = QuestionHider(txtDisplay, rootView)

    private var factor1 = 0
    private var factor2 = 0

    private var displayText = SpannableStringBuilder()
    private var questionLength = 0

    private var questionScheduled = false
    private var scheduledFactor1 = 0
    private var scheduledFactor2 = 0

    private var answerHighlightType = ANSWER_NOT_HIGHLIGHTED
        set(value) {
            when (value) {
                field, ANSWER_NOT_HIGHLIGHTED -> displayText.clearSpans()
                ANSWER_HIGHLIGHTED_CORRECT -> displayText.paintAnswer(CORRECT_ANSWER_HIGHLIGHT_COLOR)
                ANSWER_HIGHLIGHTED_INCORRECT -> displayText.paintAnswer(WRONG_ANSWER_HIGHLIGHT_COLOR, true)
            }
            txtDisplay.text = displayText
            field = value
        }

    private val correctDisplayBuilder = StringBuilder(QUESTION_AND_ANSWER_MAX_LENGTH)

    private fun Spannable.paintAnswer(color: Int, strikeThrough: Boolean = false) {
        clearSpans()
        setSpan(ForegroundColorSpan(color), questionLength, length, SPAN_EXCLUSIVE_INCLUSIVE)
        if (strikeThrough) {
            setSpan(StrikethroughSpan(), questionLength, length, SPAN_EXCLUSIVE_INCLUSIVE)
        }
    }

    init {
        shower.addQuestionShownListener {if (active) { notifyListeners(factor1, factor2) }}

        hider.addQuestionHiddenListener {
            if (active && questionScheduled) {
                questionScheduled = false
                showQuestionImmediately(scheduledFactor1, scheduledFactor2)
            }
        }

        addSubComponents(shower, hider)
    }

    fun addQuestionDisplayedListener(questionDisplayedListener: (factor1: Int, factor2: Int) -> Unit) {
        addEvent1Listener(questionDisplayedListener)
    }

    fun addQuestionDisplayedListeners(vararg questionDisplayedListeners: (factor1: Int, factor2: Int) -> Unit) {
        addEvent1Listeners(*questionDisplayedListeners)
    }

    fun showNewQuestion(factor1: Int, factor2: Int) {
        if (active) {
            if (displayText.isEmpty()) {
                showQuestionImmediately(factor1, factor2)
            } else {
                scheduledFactor1 = factor1
                scheduledFactor2 = factor2
                questionScheduled = true
            }
        }
    }

    private fun showQuestionImmediately(factor1: Int, factor2: Int) {
        log("Set factor1=$factor1, factor2=$factor2")
        this.factor1 = factor1
        this.factor2 = factor2

        displayText.setNewQuestion(factor1, factor2)
        questionLength = displayText.length
        answerHighlightType = ANSWER_NOT_HIGHLIGHTED

        centerQuestion()
        shower.showQuestion(displayText)
    }

    fun showCorrectAnswer() {
        displayText.replace(questionLength, displayText.length, (factor1 * factor2).toString())
        answerHighlightType = ANSWER_HIGHLIGHTED_CORRECT
        txtDisplay.text = displayText
    }

    fun clear() {
        shower.stop()
        hider.stop()
        displayText.clear()
        txtDisplay.text = displayText
        questionScheduled = false
    }

    fun highlightAnswer(correctAnswer: Boolean) {
        answerHighlightType = if (correctAnswer) ANSWER_HIGHLIGHTED_CORRECT else ANSWER_HIGHLIGHTED_INCORRECT
    }

    private fun SpannableStringBuilder.setNewQuestion(factor1: Int, factor2: Int) {
        this.clearSpans()
        this.clear()
        this.append(factor1.toString()).append(" ⋅ ").append(factor2.toString()).append(" = ")
    }

    fun appendSymbol(symbol: Int) {
        with(displayText) {
            if (symbol != BACKSPACE) {
                append(symbol.toString())
            } else if (length > questionLength) {
                delete(lastIndex, lastIndex + 1)
            }
            txtDisplay.text = displayText
        }
    }

    fun hideQuestion() {
        if (active) {
            hider.hideQuestion()
        }
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(ACTIVE_TAG, active)
        put(DISPLAY_TEXT_TAG, displayText.toString())
        put(QUESTION_LENGTH_TAG, questionLength)
        put(FACTOR1_TAG, factor1)
        put(FACTOR2_TAG, factor2)
        put(QUESTION_SCHEDULED_TAG, questionScheduled)
        if (questionScheduled) {
            put(SCHEDULED_FACTOR_1_TAG, scheduledFactor1)
            put(SCHEDULED_FACTOR_2_TAG, scheduledFactor2)
        }
        put(HIGHLIGHT_TYPE_TAG, answerHighlightType)
    }

    override fun restoreThisComponent(state: ComponentState) {
        with (state) {
            active = getBoolean(ACTIVE_TAG)
            questionLength = getInt(QUESTION_LENGTH_TAG)
            factor1 = getInt(FACTOR1_TAG)
            factor2 = getInt(FACTOR2_TAG)
            this@QuestionDisplay.log("Restore factor1=$factor1, factor2=$factor2")
            questionScheduled = getBoolean(QUESTION_SCHEDULED_TAG)
            if (questionScheduled) {
                scheduledFactor1 = getInt(SCHEDULED_FACTOR_1_TAG)
                scheduledFactor2 = getInt(SCHEDULED_FACTOR_2_TAG)
                this@QuestionDisplay.log("Restore scheduledFactor1=$scheduledFactor1, scheduledFactor2=$scheduledFactor2")
            }
            displayText.clear()
            displayText.append(getString(DISPLAY_TEXT_TAG))
            answerHighlightType = getInt(HIGHLIGHT_TYPE_TAG)
        }
    }

    override fun thisComponentOnLayout() {
        if (displayText.isNotEmpty()) {
            centerQuestion()
        }
    }

    private fun centerQuestion() {
        correctDisplayBuilder
            .clear()
            .append(displayText)
            .replace(questionLength, correctDisplayBuilder.length, (factor1 * factor2).toString())
        val targetTextLength = txtDisplay.paint.measureText(correctDisplayBuilder, 0, correctDisplayBuilder.length)
        log("textLength==$targetTextLength, displayWidth = ${txtDisplay.width}, Set leftPadding=${Math.round((txtDisplay.width - targetTextLength) / 2)}")
        txtDisplay.updatePadding(left = Math.round((txtDisplay.width - targetTextLength) / 2))
        log("displayWidth==${txtDisplay.width}")
    }

}