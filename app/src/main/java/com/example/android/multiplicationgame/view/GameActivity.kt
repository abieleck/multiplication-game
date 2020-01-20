package com.example.android.multiplicationgame.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import com.example.android.multiplicationgame.*

class GameActivity : AppCompatActivity() {

    companion object {
        private const val NO_LEVEL = -1
        /*private const val GAME_TAG ="GAME"*/
    }


/*    private var answer: Int? = null
        set(value) {
            field = value
            txtDisplay.text = getDisplayText(question, answer)
        }

    private var question: Question? = null
        set(value) {
            field = value
            txtDisplay.text = getDisplayText(question, answer)
        }

    private val ballManager = BallManager()

    private val ballLauncher = PeriodicExecutor().apply {
        addAction {
            model.lastBallLaunchTime = System.currentTimeMillis()
            ballManager.launchBall()
            ballManager.prepareNewBallForLaunch(model.level)
        }
    }

    private val questionProvider = QuestionProvider()

    private val txtScore by lazy(LazyThreadSafetyMode.NONE) {
        findViewById<TextView>(R.id.score)
    }
    private val txtDisplay by lazy(LazyThreadSafetyMode.NONE) {
        findViewById<TextView>(R.id.display)
    }
    private val btnStartGame by lazy(LazyThreadSafetyMode.NONE) {
        findViewById<Button>(R.id.btn_start_game)
    }
    private var score: Int = 0
        set(value) {
            field = value
            txtScore.text = value.toString()
        }

    private val buttons by lazy(LazyThreadSafetyMode.NONE) {
        buttonIds.map {
            findViewById<Button>(it)
        }
    }

    private val model by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(GameViewModel::class.java)
    }

    private val ballImages by lazy(LazyThreadSafetyMode.NONE) {
        Array<ImageView>(BALL_COUNT) { findViewById(ballIds[it]) }
    }

    private val rootView by lazy(LazyThreadSafetyMode.NONE) {
        findViewById<View>(android.R.id.content)
    }

    private val ballPlaces by lazy(LazyThreadSafetyMode.NONE) {
        Log.d(logTag, "Initiating ball positions")
        FloatArray(BALL_COUNT) { ballImages[it].left.toFloat() }.apply {
            Log.d(logTag, "Ball positions initiated to ${this.asList()}")
        }
    }

    private val mainLayout by lazy (LazyThreadSafetyMode.NONE) {
        findViewById<ConstraintLayout>(R.id.game_activity_layout)
    }*/

    /*private var activityWasRecreated = true*/

    private lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val initialLevel = intent.getIntExtra(SELECTED_LEVEL_KEY, NO_LEVEL).apply {
            if (this == NO_LEVEL) {
                throw IllegalStateException("Game level was not provided")
            }
        }

        game = Game(this, initialLevel).also { it.makeRoot(this, savedInstanceState) }

        /*if (savedInstanceState == null) {
            game.init()
        } else {
            game.restoreState(ComponentState(savedInstanceState).getState(GAME_TAG))
        }

        val mainLayout = findViewById<ConstraintLayout>(R.id.game_activity_layout)
        mainLayout.doOnLayout {
            game.onLayout()
            game.resume()
        }

        activityWasRecreated = true*/

        /*score = model.score

        btnStartGame.setOnClickListener { startGame() }
        buttons.forEachIndexed { key, button ->
            button.setOnClickListener { processKeyboardPress(key) }
        }

        val levelColors = IntArray(LEVEL_COUNT) { ContextCompat.getColor(this, levelColorResources[it]) }
        mainLayout.doOnLayout {
            ballManager.init(ballPlaces, model.ballModels, ballImages, levelColors, model.ballAnimationTime)
        }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        log("onSaveInstanceState()")
    }

    /*    override fun onPause() {
        super.onPause()
        game.pause()
        activityWasRecreated = false
    }

    override fun onDestroy() {
        super.onDestroy()
        game.destroy()
    }

    override fun onResume() {
        super.onResume()
        if (!activityWasRecreated) {
            game.resume()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        ComponentState(outState).put(GAME_TAG, game.saveState())
    }*/

/*    private fun processKeyboardPress(key: Int) {
        if (key == BACKSPACE_KEY) {
            if ((answer ?: 0) < 10) {
                answer = null
            } else {
                answer = (answer as Int) / 10
            }
        } else {
            answer = (answer ?: 0) * 10 + key
            if (answer == 0 || answer!! >= 10 || question!!.answer <= 9) {
                processAnswer(answer!!)
                switchQuestion(questionProvider.getNewQuestion())
            }
        }
    }*/

 /*   private fun switchQuestion(question: Question) {
        val dx = rootView.width.toFloat() / 2
        ValueAnimator.ofFloat(-1f, 1f).apply {
            startDelay = 200
            duration = 600
            var isOldText = true
            addUpdateListener {
                val value = animatedValue as Float
                txtDisplay.alpha = Math.abs(value)
                txtDisplay.translationX = dx * (value + (if (value < 0) 1 else -1))
                if (isOldText && value >= 0) {
                    isOldText = false
                    this@GameActivity.question = question
                    answer = null
                }
            }
            addEvent1Listener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    enableKeyboard(true)
                }
            })
            start()
        }
    }*/

 /*   private fun processAnswer(answer: Int) {
        enableKeyboard(false)
        if (question!!.answer == answer) {
            score++
        }
    }*/

/*    private fun startGame() {
        model.gameState = WAIT_FOR_QUESTION
        setGameState(model.gameState)
        ballManager.hideAllBalls()
        score = 0
        showQuestion(questionProvider.getNewQuestion())
        ballManager.prepareNewBallForLaunch(model.level)
        runBallLauncher()
    }*/

 /*   fun gameOver() {
        ballLauncher.stop()
        enableKeyboard(false)
        btnStartGame.visibility = View.VISIBLE
    }*/

/*    private fun showQuestion(question: Question) {
        txtDisplay.alpha = 0f
        val dx = -rootView.width.toFloat() / 2
        txtDisplay.translationX = dx
        this.answer = null
        this.question = question
        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            addUpdateListener { updatedAnimation ->
                val value = animatedValue as Float
                txtDisplay.alpha = value
                txtDisplay.translationX = dx * (1 - value)
            }
            addEvent1Listener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    enableKeyboard(true)
                }
            })
            start()
        }
    }*/

/*    fun enableKeyboard(enabled: Boolean) {
        buttons.forEach { it.isEnabled = enabled }
    }*/

/*    private fun setGameState(newGameState: GameState) {
        enableKeyboard(newGameState.keyboardEnabled)
        btnStartGame.visibility = if (newGameState.btnStartVisible) View.VISIBLE else View.INVISIBLE
        applyStateChangeToBallLauncher(model.gameState.ballLauncherState, newGameState.ballLauncherState)
        model.gameState = newGameState
    }*/

/*    private fun applyStateChangeToBallLauncher(oldState: GameState.BallLauncherState, newState: GameState.BallLauncherState) {
        when (oldState) {
            GameState.BallLauncherState.STOPPED -> when (newState) {
                GameState.BallLauncherState.STOPPED, GameState.BallLauncherState.PAUSED -> Unit
                GameState.BallLauncherState.RUNNING -> runBallLauncher()
            }
            GameState.BallLauncherState.PAUSED -> when(newState) {
                GameState.BallLauncherState.STOPPED, GameState.BallLauncherState.PAUSED -> Unit
                GameState.BallLauncherState.RUNNING -> restartBallLauncher()
            }
            GameState.BallLauncherState.RUNNING -> when(newState) {
                GameState.BallLauncherState.STOPPED, GameState.BallLauncherState.PAUSED -> stopBallLauncher()
                GameState.BallLauncherState.RUNNING -> Unit
            }
        }
    }*/

/*    private fun restartBallLauncher() {
        val initialDelay = when(model.lastBallLaunchTime) {
            0L -> Math.max(0L, model.ballLauncherInitialDelay - (model.ballLauncherStopTime - model.ballLauncherStartTime))
            else -> Math.max(0L, model.ballLauncherInterval - (model.ballLauncherStopTime - model.lastBallLaunchTime))
        }
        runBallLauncher(initialDelay)
    }*/

/*    private fun stopBallLauncher() {
        ballLauncher.stop()
        model.ballLauncherStopTime = System.currentTimeMillis()
    }*/

/*    private fun runBallLauncher(initialDelay: Long = BALL_LAUNCHER_INITIAL_DELAY, interval: Long = BALL_LAUNCHER_INTERVAL) {
        model.ballLauncherStartTime = System.currentTimeMillis()
        model.lastBallLaunchTime = 0L
        model.ballLauncherStopTime = 0L
        model.ballLauncherInitialDelay = initialDelay
        model.ballLauncherInterval = interval
        ballLauncher.start(initialDelay, interval)
    }*/

/*    private fun getDisplayText(question: Question?, answer: Int?): String? {
        if (question == null) {
            return null
        }
        val questionString = question.a.toString() + " \u22C5 " + question.b + " = "
        if (answer == null) {
            return questionString
        }
        return questionString + answer.toString()
    }*/

}
