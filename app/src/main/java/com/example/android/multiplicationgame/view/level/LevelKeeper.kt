package com.example.android.multiplicationgame.view.level

import com.example.android.multiplicationgame.OneEvent1ParamComponent
import com.example.android.multiplicationgame.view.ComponentState

class LevelKeeper(initialLevel: Int): OneEvent1ParamComponent<Int>() {
    private val levelTag = "LEVEL"
    private val correctAnswerCountTag = "CORRECT_ANSWER_COUNT"

    private val maxLevel = 10

    var level = initialLevel

    private val correctAnswersToIncLevel = 10
    private var correctAnswerCount = 0

    fun addLevelChangeListener(levelChangeListener: (newLevel: Int) -> Unit) {
        addEvent1Listener(levelChangeListener)
    }

    fun addLevelChangeListeners(vararg levelChangeListeners: (newLevel: Int) -> Unit) {
        addEvent1Listeners(*levelChangeListeners)
    }

    fun adjustLevel(correctAnswer: Boolean) {
        if (correctAnswer) {
            correctAnswerCount++
            if (level <= maxLevel && correctAnswerCount >= correctAnswersToIncLevel) {
                correctAnswerCount = 0
                level ++
                notifyListeners(level)
            }
        }
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(levelTag, level)
        put(correctAnswerCountTag, correctAnswerCount)
    }

    override fun restoreThisComponent(state: ComponentState) {
        level = state.getInt(levelTag)
        correctAnswerCount = state.getInt(correctAnswerCountTag)
    }

}
