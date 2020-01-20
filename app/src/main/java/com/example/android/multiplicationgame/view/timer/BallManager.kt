package com.example.android.multiplicationgame.view.timer

import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.valueIterator
import com.example.android.multiplicationgame.*
import com.example.android.multiplicationgame.view.ComponentState
import com.example.android.multiplicationgame.view.GameComponent
import java.util.*
import kotlin.collections.ArrayList

class BallManager(private val activity: AppCompatActivity): GameComponent() {
    /*val logTag = this.javaClass.simpleName

    private val timeToTraverseQueueMs = 1500L

    private lateinit var balls: Array<Ball>
    private var ballToLaunch: Ball? = null

    private lateinit var positions: FloatArray
    private var createPosition: Float = 0f
    private var launchPosition: Float = 0f

    private lateinit var levelColors: IntArray
    private var launchDuration: Long = 0L
    private lateinit var durationsFromLaunch: LongArray
    private var velocity: Float = 0f


    private var ballsInQueueCount: Int = 0
    private val unusedBalls: ArrayDeque<Ball> = ArrayDeque(BALL_COUNT)

    private val animator = ValueAnimator.ofInt(0).apply {
        duration = Long.MAX_VALUE
        interpolator = null
        addUpdateListener {
            balls.forEach {
                if (it.isMoving) {
                    it.updatePositionAlpha(this.currentPlayTime, positions)
                }
            }
        }
    }*/

    // new ball is put at create position, is visible, has alpha 0 and scale 1

    companion object {

        const val NO_ACTION = 0

        /*private val BALL_IMAGE_IDS = intArrayOf(
            R.id.img_ball_0, R.id.img_ball_1, R.id.img_ball_2, R.id.img_ball_3, R.id.img_ball_4, R.id.img_ball_5,
            R.id.img_ball_6, R.id.img_ball_7, R.id.img_ball_8, R.id.img_ball_9, R.id.img_ball_10, R.id.img_ball_11)*/

        //val BALL_COUNT = BALL_IMAGE_IDS.size
        val LAST_BALL_POSITION = BALL_COUNT - 1
        private const val USED_BALL_IDS_TAG = "USED_BALLS"
    }

    private val finishActions = ArrayList<(Ball) -> Unit>().apply { add {/*default action with Id 0 does nothing*/} }

    private val ballAnimator = TimerAnimator()

    private val ballsById = SparseArray<BallComponent>(BALL_COUNT).apply {
        /*BALL_IMAGE_IDS.forEachIndexed { i, imgId ->
            val ballComponent = BallComponent(activity.findViewById(imgId), i)
            this.append(i, ballComponent)
        }*/
    }

    val currentPlayTime
        get() = ballAnimator.currentPlayTime

    private val unusedBalls = ArrayDeque<BallComponent>(BALL_COUNT).apply {
        ballsById.valueIterator().forEach { add(it) }
    }
    private val usedBalls: MutableSet<BallComponent> = HashSet(BALL_COUNT)

    init {
        BallComponent.levelColors = with(intArrayOf(
            R.color.colorLevel_1,
            R.color.colorLevel_2,
            R.color.colorLevel_3,
            R.color.colorLevel_4,
            R.color.colorLevel_5,
            R.color.colorLevel_6,
            R.color.colorLevel_7,
            R.color.colorLevel_8,
            R.color.colorLevel_9,
            R.color.colorLevel_10
        )) {
            IntArray(size) { ContextCompat.getColor(activity, this[it]) }
        }

        addSubComponents(*unusedBalls.toArray(arrayOf()), ballAnimator)
    }

    fun registerFinishAction(action: (Ball) -> Unit) = finishActions.apply { add(action) }.size - 1

    fun reset() {
        while (usedBalls.isNotEmpty()) {
            recycleBall(usedBalls.first())
        }
    }

    fun createBall(position: Int, alpha: Float, scale: Float, level: Int): Ball = unusedBalls.pollLast().apply {
        show(position, alpha, scale, level)
        usedBalls.add(this)
        //ballAnimator.addAnimatorListeners(this)
    }

    fun recycleBall(ball: Ball) {
        if (usedBalls.remove(ball as BallComponent)) {
            unusedBalls.addLast(ball)
            //ballAnimator.removeAnimatorListener(ball)
            ball.hide()
        }
    }

    /*override fun thisComponentOnLayout() {
        BallComponent.coordinates = FloatArray(BALL_COUNT) {
             activity.findViewById<ImageView>(BALL_IMAGE_IDS[it]).left.toFloat()
        }
    }*/

    fun getBall(ballId: Int): Ball? = ballsById[ballId]

    override fun saveThisComponent() = ComponentState().apply {
        put(USED_BALL_IDS_TAG, usedBalls.map(Ball::id))
    }

    override fun restoreThisComponent(state: ComponentState) {
        usedBalls.clear()
        usedBalls.addAll(state.getIntCollection(USED_BALL_IDS_TAG) { ArrayList<Int>(BALL_COUNT) }
            .map(ballsById::valueAt))

        ballAnimator.removeAnimatorListeners()
        //usedBalls.forEach(ballAnimator::addAnimatorListeners)

        ballsById.valueIterator().forEach {
            if (!usedBalls.contains(it)) {
                unusedBalls.add(it)
            }
        }
    }
/*    private fun calculateTravelTime(targetPosition: Int): Long = 500 // TODO remove this stub

    fun stopAnimation() {
        animator.cancel()
    }

    val animationTime
        get() = animator.currentPlayTime

    fun init(positions: FloatArray, ballModels: Array<BallModel>, ballImages: Array<ImageView>, levelColors: IntArray, animationTime: Long) {
        Log.d(logTag, "enter init")
        Log.d(logTag, "setting positions ${positions.asList()}")
        setPositions(positions)
        this.levelColors = levelColors
        createBalls(ballModels, ballImages, animationTime)
    }

    private fun setPositions(positions: FloatArray) {
        this.positions = positions
        createPosition = positions[CREATE_POSITION_INDEX]
        launchPosition = positions[LAUNCH_POSITION_INDEX]
        velocity = (positions[0] - launchPosition) / timeToTraverseQueueMs
        launchDuration = Math.round(2 * (launchPosition - createPosition) / velocity).toLong()
        durationsFromLaunch = LongArray(BALL_QUEUE_SIZE) {
            Math.round((positions[it] - createPosition) / velocity).toLong()
        }
    }

    private fun createBalls(ballModels: Array<BallModel>, ballImages: Array<ImageView>, animationTime: Long) {
        var animationNeeded = false
        balls = Array(BALL_COUNT) {
            val model = ballModels[it]
            val ball = Ball(
                ballImages[it],
                model,
                positions,
                levelColors,
                animationTime
            )
            if (model.visible) {
                if (model.ultimatePositionIndex() == LAUNCH_POSITION_INDEX) {
                    ballToLaunch = ball
                } else if (model.ultimateAlpha() != 0f) {
                    ballsInQueueCount++
                } // else the ball is scheduled to disappear
            } else {
                unusedBalls.addLast(ball)
            }
            if (model.isMoving) {
                animationNeeded = true
            }
            ball
        }
        animator.currentPlayTime = animationTime
        if (animationNeeded) {
            animator.start()
        }
    }

    fun hideAllBalls() {
        animator.cancel()
        unusedBalls.clear()
        balls.forEach {
            it.visible = false
            it.putAt(CREATE_POSITION_INDEX, 0f)
            unusedBalls.addLast(it)
            ballsInQueueCount = 0
        }
        animator.cancel()
    }

    fun prepareNewBallForLaunch(level: Int) {
        Log.d(logTag, "preparing new ball for launch")
        ballToLaunch = unusedBalls.removeLast().apply {
            setLevel(level, levelColors)
            putAt(CREATE_POSITION_INDEX, 0f)
            val startTime = animator.currentPlayTime
            addTargetToBall(this, BallTarget(LAUNCH_POSITION_INDEX, 1f, startTime, launchDuration))
            visible = true
        }
    }

    fun launchBall() {
        Log.d(logTag, "Launching ball")
        val startTime = animator.currentPlayTime
        addTargetToBall(ballToLaunch!!, BallTarget(LAST_POSITION_INDEX, 1f, startTime, durationsFromLaunch[LAST_POSITION_INDEX]))
        if (ballsInQueueCount < BALL_QUEUE_SIZE - 1) {
            addTargetToBall(ballToLaunch!!, BallTarget(ballsInQueueCount, 1f, startTime, durationsFromLaunch[ballsInQueueCount]))
        }
        ballToLaunch = null
        ballsInQueueCount++
    }

    private fun addTargetToBall(ball: Ball, target: BallTarget) {
        Log.d(logTag, "adding new target to ball: $target")
        ball.addTarget(target)
        if (!animator.isStarted) {
            animator.start()
        }
    }
*/

}