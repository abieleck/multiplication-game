package com.example.android.multiplicationgame.view.score

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.multiplicationgame.R
import com.example.android.multiplicationgame.view.ComponentState
import com.example.android.multiplicationgame.view.GameComponent

class ScoreKeeper(activity: AppCompatActivity): GameComponent() {

    private val scoreTag = "SCORE"

    private val txtScore = activity.findViewById<TextView>(R.id.score)

    var score: Int = 0
        set(value) {
            field = value
            txtScore.text = value.toString()
        }

    override fun saveThisComponent() = ComponentState().apply {
        put(scoreTag, score)
    }


    override fun restoreThisComponent(state: ComponentState) {
        score = state.getInt(scoreTag)
    }

    fun scorePoints(correctAnswer: Boolean) {
        if (correctAnswer) {
            score += 1
        }
    }

}