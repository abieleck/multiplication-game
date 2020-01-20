package com.example.android.multiplicationgame.view.executor

import com.example.android.multiplicationgame.OneEvent5ParamComponent
import com.example.android.multiplicationgame.view.ComponentState
import com.example.android.multiplicationgame.view.executor.GameKeyboard.Companion.BACKSPACE

// type parameters are for askTime, answerTime, factor1, factor2, correctAnswer
class AnswerComposer: OneEvent5ParamComponent<Long, Long, Int, Int, Boolean>() {

    companion object {
        private const val FACTOR1_TAG = "FACTOR1"
        private const val FACTOR2_TAG = "FACTOR2"
        private const val ANSWER_TAG = "ANSWER"
        private const val ELAPSED_TIME_TAG = "ELAPSED_TIME"
    }

    private var factor1 = 0
    private var factor2 = 0
    private var answer = 0
    private var expectedAnswer = 0
    private var elapsedTime = 0L
    private var resumeTime = 0L

    fun addAnswerListener(answerListener: (askTime: Long, answerTime: Long, factor1: Int, factor2: Int,
                                           isAnswerCorrect: Boolean) -> Unit) {
        addEvent1Listener(answerListener)
    }

    fun onNewQuestion(factor1: Int, factor2: Int) {
        this.factor1 = factor1
        this.factor2 = factor2
        elapsedTime = 0L
        resumeTime = System.currentTimeMillis()
        answer = 0
        expectedAnswer = factor1 * factor2
    }

    fun appendSymbol(symbol: Int) {
        if (symbol == BACKSPACE) {
            answer /= 10
        } else {
            answer = answer * 10 + symbol
            if (answer >= 10 || expectedAnswer < 10 || symbol == 0) {
                // parameters are for askTime, answerTime, factor1, factor2, correctAnswer
                val answerTime = System.currentTimeMillis()
                notifyListeners(answerTime, elapsedTime + answerTime - resumeTime, factor1, factor2, answer == expectedAnswer)
            }
        }
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(FACTOR1_TAG, factor1)
        put(FACTOR2_TAG, factor2)
        put(ANSWER_TAG, answer)
        put(ELAPSED_TIME_TAG, elapsedTime + System.currentTimeMillis() - resumeTime)
    }

    override fun restoreThisComponent(state: ComponentState) {
        with(state) {
            factor1 = getInt(FACTOR1_TAG)
            factor2 = getInt(FACTOR2_TAG)
            expectedAnswer = factor1 * factor2
            answer = getInt(ANSWER_TAG)
            elapsedTime = getLong(ELAPSED_TIME_TAG)
            resumeTime = System.currentTimeMillis()

        }
    }
}