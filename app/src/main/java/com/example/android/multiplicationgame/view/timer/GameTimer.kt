package com.example.android.multiplicationgame.view.timer

import androidx.appcompat.app.AppCompatActivity
import com.example.android.multiplicationgame.OneEvent0ParamComponent
import com.example.android.multiplicationgame.classTag
import com.example.android.multiplicationgame.log
import com.example.android.multiplicationgame.view.*

class GameTimer(activity: AppCompatActivity, private val initialLevel: Int): OneEvent0ParamComponent() /*, LaunchTimerListener*/ {
    companion object {
        /*private val gameDurationPerLevel = floatArrayOf(30000f, 25000f, 20000f, 15000f, 13000f, 10000f, 8000f, 6000f, 4000f, 3500f)
        private val bonusTimePerLevel = floatArrayOf(1000f, 900f, 800f, 700f, 650f, 600f, 550f, 400f, 350f, 300f)*/

        private const val LEVEL_TAG = "LEVEL"
/*        private const val PREVIOUS_TIME_TAG = "PREVIOUS_TIME"
        private const val PROGRESS_TAG = "PROGRESS"*/
    }

    //private val animator = TimerAnimator()

    private val impulseGenerator = TimeImpulseGenerator(initialLevel)
    private val presenter = ProgressPresenter(activity)
    /*
        private val timerView = activity.findViewById<ProgressBar>(R.id.timer)
        private val maxTimerValue = timerView.max
        private val progressPerMs = maxTimerValue / gameDurationPerLevel[initialLevel - 1]
        private var previousPlayTime = 0L

        private var progress = 0f
            set(value) {
                field = value
                val roundedValue = Math.round(value)
                if (roundedValue != timerView.progress) {
                    timerView.progress = roundedValue
                }
            }*/
    var level = initialLevel
        set(value) {
            field = value
            impulseGenerator.setLevel(value)
        }
    /*private val ballManager = BallManager(activity)

    private val starter = BallStarter(ballManager, initialLevel)

    private val launchTimer = LaunchTimer(initialLevel).also {
        it.addTimerListener(this)
    }
    private val annihilator = BallAnnihilator(ballManager)
    private val ballsQueue = ArrayDeque<Ball>()*/

    /*private val animator = AnimatorComponent(
        ValueAnimator.ofInt(0).apply {
            duration = Long.MAX_VALUE // infinite
            interpolator = null
            addUpdateListener {
                updateProgress(it.currentPlayTime)
            }
        }
    )*/

    init {
        //animator.addAnimatorListeners(this::updateProgress)
        presenter.addFullProgressListener {
            presenter.log("-> ${impulseGenerator.classTag()}.stop()")
            impulseGenerator.stop()
            notifyListeners() // pass full progress event to game-end listeners
        }
        impulseGenerator.addTimeImpulseListener {
            impulseGenerator.log("-> ${presenter.classTag()}.increaseProgress()")
            presenter.increaseProgress(level)
        }
        addSubComponents(impulseGenerator, presenter/*, animator, ballManager, starter, launchTimer*/)
    }

    fun addGameEndListener(gameEndListener: () -> Unit) {
        addEvent1Listener(gameEndListener)
    }

    fun addGameEndListeners(vararg gameEndListeners: () -> Unit) {
        addEvent1Listeners(*gameEndListeners)
    }

    fun stop() {
        impulseGenerator.stop()
        //animator.cancel()

    }

    fun start() {
        /*progress = 0f
        previousPlayTime = 0L*/
        log("start()")
        level = initialLevel
        presenter.clearProgress()
        impulseGenerator.start()
        //animator.start()
        /*ballsQueue.clear()
        ballManager.reset()
        starter.reset()
        launchTimer.wake()*/
    }

    /*private fun updateProgress(currentPlayTime: Long) {
        progress += Math.min(maxTimerValue.toFloat(), (currentPlayTime - previousPlayTime) * progressPerMs)
        if (progress >= maxTimerValue) {
            notifyListeners()
            animator.cancel()
        }
        previousPlayTime = currentPlayTime
    }*/

    /*override fun onBallLaunch() {
        val ballToLaunch = starter.getBall()
        ballsQueue.addLast(ballToLaunch)
        ballToLaunch.move(ballManager.currentPlayTime, BallStarter.LAUNCH_POSITION - 1,
            finishActionId = NO_ACTION)
        if (ballsQueue.size == BALL_QUEUE_SIZE) {
            gameEndListeners.forEach(GameEndListener::onGameEnd)
            launchTimer.sleep()
        } else {
            ballToLaunch.move(ballManager.currentPlayTime, ballsQueue.size - 1, finishActionId = NO_ACTION)
        }
    }*/

    fun adjustProgress(correctAnswer: Boolean) {
        if (correctAnswer/* && ballsQueue.isNotEmpty()*/) {
            presenter.decreaseProgress()
            //progress = Math.max(0f, progress - bonusTimePerLevel[level - 1] * progressPerMs)
            /*annihilator.annihilate(ballsQueue.pollFirst())
            ballsQueue.forEachIndexed { i, ball ->
                ball.move(ballManager.currentPlayTime, i, finishActionId = NO_ACTION)
            }*/
        }
    }

    /*override fun onLevelChanged(newLevel: Int) {
        level = newLevel
        starter.level = newLevel
        launchTimer.setLevel(newLevel)
    }*/

    override fun saveThisComponent() = ComponentState().apply {
        put(LEVEL_TAG, level)
        //put(PREVIOUS_TIME_TAG, previousPlayTime)
        //put(PROGRESS_TAG, progress)
        /*put(IDS_ARRAY_TAG, ballsQueue.map { it.id })*/
    }

    override fun restoreThisComponent(state: ComponentState) {
        with(state) {
            level = getInt(LEVEL_TAG)
            //previousPlayTime = getLong(PREVIOUS_TIME_TAG)
            //progress = getFloat(PROGRESS_TAG)
        }
        /*ballsQueue.clear()
        state.getIntCollection(IDS_ARRAY_TAG) { ArrayList<Int>() }.forEach { ballId ->
            ballsQueue.addLast(ballManager.getBall(ballId))
        }*/
    }



}