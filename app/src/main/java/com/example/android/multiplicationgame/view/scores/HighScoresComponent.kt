package com.example.android.multiplicationgame.view.scores

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.model.GameViewModel
import com.example.android.multiplicationgame.view.ComponentState

class HighScoresComponent(activity: AppCompatActivity): OneEvent0ParamComponent() {

    companion object {
        private const val MAX_RANK = 5
        private const val MAX_INDEX = MAX_RANK - 1
    }

    private var scores: MutableList<HighScore>? = null
    private var scoreToAdd: Int = 0
    private var isScoreToAdd = false

    /*private val presenter = HighScoresPresenter()
    private val playerReader = PlayerReader()
    private val playerNameManager = PlayerNameManager()
    private val repository: HighScoreRepository*/



    init {
        ViewModelProviders.of(activity).get(GameViewModel::class.java).allHighScores.observe(activity,
            Observer { newScores -> newScores?.let {
                scores = ArrayList(newScores)
                scores?.let {
                    if (isScoreToAdd) {
                        addNewScore(scoreToAdd, it)
                    }
                }
            }
        })
    }

    fun addScore(score: Int) {
        scoreToAdd = score
        isScoreToAdd = true
        scores?.let { addNewScore(score, it) }
    }

    private fun addNewScore(score: Int, scores: MutableList<HighScore>) {
        var index = scores.size
        while (index > 0 && scores[index - 1].score < score) {
            index--
        }
        if (index > MAX_INDEX) {
            return
        }
        /*val playerName: String = playerNameManager.getName()*/
        /*newHighScore = when {
            scores.size < MAX_RANK -> HighScore(index + 1, )
        }*/

        /*if (index == scores.size) {
            scores.add(HighScore(index, playerName, score))
        }*/
    }



    override fun saveThisComponent(): ComponentState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun restoreThisComponent(state: ComponentState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}