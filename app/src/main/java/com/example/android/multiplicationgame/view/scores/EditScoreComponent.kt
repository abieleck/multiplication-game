package com.example.android.multiplicationgame.view.scores

import androidx.appcompat.app.AppCompatActivity
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.view.ComponentState
import kotlin.math.min

class EditScoreComponent(
    activity: AppCompatActivity,
    private val highScoreService: HighScoreService
) : OneEvent0ParamComponent() {

    companion object {
        private const val MAX_SCORES_COUNT = 5
        private const val SCORES_FETCH_TIMEOUT_MS = 1500
    }

    private val playerNameReaderComponent = PlayerNameReaderComponent(activity)
    private var score: Int = 0

    init {
        playerNameReaderComponent.addPlayerNameListener(this::saveScore)
        addSubComponents(playerNameReaderComponent)
    }

    fun addNewScore(score: Int) =
        if (score > 0)
            highScoreService.getHighScores(SCORES_FETCH_TIMEOUT_MS)
                ?.takeIf { it.isEmpty() || it.last().score < score }
                ?.let {
                    this.score = score
                    playerNameReaderComponent.readPlayerName(getAddScoreList(it, score))
                }
        else
            notifyListeners()


    private fun saveScore(playerName: String?) {
        playerName
            ?.let { highScoreService.addScore(score, it) }
            .also { notifyListeners() }
    }

    private fun getAddScoreList(scores: List<HighScore>, score: Int): List<HighScoreEdit> {
        val result: MutableList<HighScoreEdit> = ArrayList(min(MAX_SCORES_COUNT, scores.size + 1))
        var i = 0
        while (i < scores.size && i < MAX_SCORES_COUNT && scores[i].score >= score) {
            result.add(HighScoreEdit(i + 1, scores[i].player, scores[i].score))
            i++
        }
        if (i < MAX_SCORES_COUNT) {
            result.add(HighScoreEdit(i + 1, null, score))
            i++
        }
        while (i <= scores.size && i < MAX_SCORES_COUNT) {
            result.add(HighScoreEdit(i + 1, scores[i - 1].player, scores[i - 1].score))
            i++
        }
        return result
    }

    fun addScoreEditedListener(scoreEditedListener: () -> Unit) =
        addEvent1Listener(scoreEditedListener)

    override fun saveThisComponent() = ComponentState().apply {
        // TODO implement component saving
    }

    override fun restoreThisComponent(state: ComponentState) {
        // TODO implement component restore
    }
}