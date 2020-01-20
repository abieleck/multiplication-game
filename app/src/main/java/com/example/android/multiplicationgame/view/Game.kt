package com.example.android.multiplicationgame.view

import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.android.multiplicationgame.R
import com.example.android.multiplicationgame.classTag
import com.example.android.multiplicationgame.log
import com.example.android.multiplicationgame.model.QuestionProvider
import com.example.android.multiplicationgame.view.executor.AnswerComposer
import com.example.android.multiplicationgame.view.executor.GameKeyboard
import com.example.android.multiplicationgame.view.executor.QuestionDisplay
import com.example.android.multiplicationgame.view.level.LevelKeeper
import com.example.android.multiplicationgame.view.presenter.Presenter
import com.example.android.multiplicationgame.view.score.ScoreKeeper
import com.example.android.multiplicationgame.view.scores.EditScoreComponent
import com.example.android.multiplicationgame.view.scores.HighScoreService
import com.example.android.multiplicationgame.view.timer.GameTimer

class Game(activity: AppCompatActivity, initialLevel: Int) : GameComponent() {

    companion object {
        private const val FIRST_GAME_TAG = "FIRST_GAME"
        private const val BTN_VISIBILITY = "BTN_VISIBILITY"
    }

    private val btnStartGame = activity.findViewById<Button>(R.id.btn_start_game)

    private val highScoreService = HighScoreService()

    private val timer = GameTimer(activity, initialLevel)
    private val levelKeeper = LevelKeeper(initialLevel)
    private val scoreKeeper = ScoreKeeper(activity)
    private val answerComposer = AnswerComposer()
    private val questionProvider = QuestionProvider()
    private val keyboard = GameKeyboard(activity)
    private val presenter = Presenter(activity)
    private val display = QuestionDisplay(activity)
    private val scoreEditor = EditScoreComponent(activity, highScoreService)

    private var firstGame = true

    init {
        timer.addGameEndListener {
            keyboard.active = false
            display.active = false
            questionProvider.cancelQuestionFetch()
            timer.stop()
            scoreEditor.addNewScore(scoreKeeper.score)
        }

        scoreEditor.addScoreEditedListener(this::showStartButton)

        levelKeeper.addLevelChangeListener { newLevel -> timer.level = newLevel }

        answerComposer.addAnswerListener { answerTime, duration, factor1, factor2, isAnswerCorrect: Boolean ->
            keyboard.active = false
            display.highlightAnswer(isAnswerCorrect)
            questionProvider.registerAnswer(answerTime, duration, isAnswerCorrect) // async
            answerComposer.log("-> ${questionProvider.classTag()}.fetchNewQuestion()")
            questionProvider.fetchNewQuestion() // async
            presenter.showAnswerPresentation(factor1, factor2, isAnswerCorrect)
            scoreKeeper.scorePoints(isAnswerCorrect)
            timer.adjustProgress(isAnswerCorrect)
            levelKeeper.adjustLevel(isAnswerCorrect)
        }

        with(presenter) {
            addPresentationStartListener(timer::sleep)
            addPresentationSkipListener {
                presenter.log("-> ${display.classTag()}.hideQuestion()")
                display.hideQuestion()
            }
            addPresentationEndListener {
                timer.wake()
                presenter.log("-> ${display.classTag()}.showCorrectAnswer()")
                display.showCorrectAnswer()
                display.hideQuestion()
            }
        }

        keyboard.addKeyPressedListeners(
            display::appendSymbol,
            answerComposer::appendSymbol
        ) // display must be first

        display.addQuestionDisplayedListeners(
            { a, b ->
                display.log("-> ${answerComposer.classTag()}.onNewQuestion($a, $b)")
                answerComposer.onNewQuestion(a, b)
            },
            { _, _ -> keyboard.active = true }
        )

        questionProvider.addQuestionFetchedListener { a, b ->
            questionProvider.log("-> ${display.classTag()}.showNewQuestion($a, $b)")
            display.showNewQuestion(a, b)
        }

        addSubComponents(
            timer, levelKeeper, scoreKeeper, answerComposer, questionProvider,
            keyboard, presenter, display, scoreEditor
        )

        btnStartGame.setOnClickListener {
            it.visibility = View.GONE
            scoreKeeper.score = 0
            levelKeeper.level = initialLevel
            timer.start()
            display.clear()
            display.active = true
            questionProvider.fetchNewQuestion()
        }
    }

    private fun showStartButton() {
        btnStartGame.visibility = View.VISIBLE
    }

    override fun resumeThisComponent() {
        if (firstGame) {
            firstGame = false
            scoreKeeper.score = 0
            questionProvider.fetchNewQuestion()
            timer.start()
        }
    }

    override fun saveThisComponent(): ComponentState = ComponentState().apply {
        put(FIRST_GAME_TAG, firstGame)
        put(BTN_VISIBILITY, btnStartGame.visibility)

    }

    override fun restoreThisComponent(state: ComponentState) {
        with(state) {
            firstGame = state.getBoolean(FIRST_GAME_TAG)
            btnStartGame.visibility = getInt(BTN_VISIBILITY)
        }
    }

}