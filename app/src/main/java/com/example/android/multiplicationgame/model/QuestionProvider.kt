package com.example.android.multiplicationgame.model

import com.example.android.multiplicationgame.OneEvent2ParamComponent
import com.example.android.multiplicationgame.view.ComponentState
import kotlin.random.Random

class QuestionProvider: OneEvent2ParamComponent<Int, Int>() {

    companion object {
        private const val FACTOR_LIMIT = 10

        private const val REPEAT_TAG = "REPEAT"
        private const val FACTOR1_TAG = "FACTOR1"
        private const val FACTOR2_TAG = "FACTOR2"
    }

    private var factor1 = 0
    private var factor2 = 0
    private var repeatQuestion = false

    fun addQuestionFetchedListener(questionFetchedListener: (factor1: Int, factor2: Int) -> Unit) {
        addEvent1Listener(questionFetchedListener)
    }

    fun addQuestionFetchedListeners(vararg questionFetchedListeners: (factor1: Int, factor2: Int) -> Unit) {
        addEvent1Listeners(*questionFetchedListeners)
    }

    fun registerAnswer(answerTime: Long, duration: Long, correct: Boolean) {
        // TODO implement saving question statistics
        repeatQuestion = !correct
    }

    fun fetchNewQuestion() {
        // TODO implement background processing of question fetching
        if (repeatQuestion) {
            repeatQuestion = false
        } else {
            factor1 = Random.nextInt(FACTOR_LIMIT)
            factor2 = Random.nextInt(FACTOR_LIMIT)
        }
        notifyListeners(factor1, factor2)
    }

    fun cancelQuestionFetch() {
        // TODO implement me
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(REPEAT_TAG, repeatQuestion)
        if (repeatQuestion) {
            put(FACTOR1_TAG, factor1)
            put(FACTOR2_TAG, factor2)
        }
    }

    override fun restoreThisComponent(state: ComponentState) {
        with (state) {
            repeatQuestion = getBoolean(REPEAT_TAG)
            if (repeatQuestion) {
                factor1 = getInt(FACTOR1_TAG)
                factor2 = getInt(FACTOR2_TAG)
            }
        }
    }

}