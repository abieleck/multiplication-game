package com.example.android.multiplicationgame.view.timer

import android.view.View
import android.widget.ImageView
import com.example.android.multiplicationgame.BALL_QUEUE_SIZE
import com.example.android.multiplicationgame.view.ComponentState
import com.example.android.multiplicationgame.view.GameComponent
import java.util.*

class BallComponent(private val ballImage: ImageView, override var id: Int): Ball, GameComponent(), TimerAnimatorListener {

    companion object {
        const val POSITION_TRAVEL_TIME_MS = 1500f / BALL_QUEUE_SIZE

        lateinit var coordinates: FloatArray
        lateinit var levelColors: IntArray

        private const val START_POSITION_TAG = "START_POSITION"
        private const val START_ALPHA_TAG = "START_ALPHA"
        private const val START_SCALE_TAG ="START_SCALE"
        private const val VISIBLE_TAG = "VISIBLE"
        private const val LEVEL_TAG = "LEVEL"
        private const val TRANSFORMATION_SIZE_TAG = "TRANSFORMATION_COUNT"
        private const val TRANSFORMATION_PREFIX = "TRANS"
    }

    var level: Int = 0
    private var visible = false

    private var startPosition = 0
    private var startAlpha = 0.0f
    private var startScale = 1.0f

    override val ultimatePosition
        get() = transformations.peekLast()?.endPosition ?: startPosition
    override var ultimateAlpha = startAlpha
        private set
    override var ultimateScale = startScale
        private set

    private val transformations = ArrayDeque<BallTransformation>()

    fun show(position: Int, alpha: Float, scale:Float, level: Int) {
        visible = true

        startPosition = position
        startAlpha = alpha
        startScale = scale

        transformations.clear()

        ultimateAlpha = alpha
        ultimateScale = scale

        this.level = level

        with (ballImage) {
            x = coordinates[position]
            this.alpha = alpha
            scaleX = scale
            scaleY = scale
            visibility = View.VISIBLE
            setColorFilter(levelColors[level - 1])
        }
    }

    fun hide() {
        visible = false
        ballImage.visibility = View.INVISIBLE
    }

    override fun move(moveStartTime: Long, targetPosition: Int, targetAlpha: Float, targetScale: Float,
                      finishActionId: Int) {
        val ultimateTime = transformations.peekLast()?.endTime ?: moveStartTime
        val duration = travelTime(ultimatePosition, targetPosition) + ultimateTime - moveStartTime
        transformations.add(BallTransformation(
            ultimatePosition,
            targetPosition,
            targetAlpha - ultimateAlpha,
            targetScale - ultimateScale,
            moveStartTime,
            duration,
            finishActionId)
        )
        ultimateAlpha = targetAlpha
        ultimateScale = targetScale
    }

    private fun travelTime(startPosition: Int, endPosition: Int)
            = (Math.abs(startPosition - endPosition) * POSITION_TRAVEL_TIME_MS).toLong()

    override fun transform(targetAlpha: Float, targetScale: Float, duration: Long, finishedActionId: Int) {
        transformations.add(BallTransformation(
            ultimatePosition,
            ultimatePosition,
            targetAlpha - ultimateAlpha,
            targetScale - ultimateScale,
            System.currentTimeMillis(),
            duration,
            finishedActionId)
        )
        ultimateAlpha = targetAlpha
        ultimateScale = targetScale
    }

    override fun onAnimationFrame(currentPlayTime: Long) {
        if (visible) {
            //clearFinishedTransformations(currentPlayTime, finishAction)
            var newX = coordinates[startPosition]
            var newAlpha = startAlpha
            var newScale = startScale
            transformations.forEach {
                with(it) {
                    newX += calculateXChange(currentPlayTime, coordinates)
                    newAlpha += calculateAlphaChange(currentPlayTime)
                    newScale += calculateScaleChange(currentPlayTime)
                }
            }
            with (ballImage) {
                x = newX
                alpha = newAlpha
                scaleX = newScale
                scaleY = newScale
            }
        }
    }

    private fun clearFinishedTransformations(currentPlayTime: Long, finishAction: (actionId: Int, Ball) -> Unit) {
        val iterator = transformations.iterator()
        while (iterator.hasNext()) {
            with(iterator.next()) {
            if (isFinished(currentPlayTime)) {
                finishAction(finishActionId, this@BallComponent)

                startPosition = endPosition
                startAlpha += alphaChange
                startScale += scaleChange

                iterator.remove()
            }
        }
        }
    }

    override fun saveThisComponent(): ComponentState = ComponentState().apply {
        put(VISIBLE_TAG, visible)
        if (visible) {
            put(START_POSITION_TAG, startPosition)
            put(START_ALPHA_TAG, startAlpha)
            put(START_SCALE_TAG, startScale)

            put(LEVEL_TAG, level)
            put(TRANSFORMATION_SIZE_TAG, transformations.size)
            transformations.forEachIndexed { i, transformation ->
                put(TRANSFORMATION_PREFIX + i, transformation.getState())
            }
        }
    }

    override fun restoreThisComponent(state: ComponentState) {
        with (state) {
            visible = getBoolean(VISIBLE_TAG)

            if (visible) {
                startPosition = getInt(START_POSITION_TAG)
                startAlpha = getFloat(START_ALPHA_TAG)
                startScale = getFloat(START_SCALE_TAG)
                level = getInt(LEVEL_TAG)

                /*for (i in 0 until getInt(TRANSFORMATION_SIZE_TAG)) {
                    val transformationState = getState(TRANSFORMATION_PREFIX + i)
                    val transformation = BallTransformation(transformationState)
                    transformations.addLast(transformation)
                }*/

                ballImage.visibility = View.VISIBLE
            } else {
                ballImage.visibility = View.INVISIBLE
            }
        }
    }

/*    init {
        updatePositionAlpha(animationTime, positions)
        ballModel.updateImageLevel(ballImage, levelColors)
        ballModel.updateImageVisibility(ballImage)
    }

    var visible: Boolean
        set(value) {
            ballModel.visible = value
            ballModel.updateImageVisibility(ballImage)
        }
        get() = ballModel.visible

    val isMoving
        get() = ballModel.isMoving

    fun putAt(positionIndex: Int, alpha: Float)
            = ballModel.putAt(positionIndex, alpha)

    fun updatePositionAlpha(t: Long, positions: FloatArray)
            = ballModel.updateImagePositionAlpha(ballImage, t, positions)

    fun setLevel(level: Int, levelColors: IntArray) {
        ballModel.level = level
        ballModel.updateImageLevel(ballImage, levelColors)
    }

    fun addTarget(place: Int = getUltimatePlace(), alpha: Float = 1.0f, scale: Float = 1.0f, onEnd: (() -> Unit)? = null) {
        // TODO implement me, need current play time
    }

    fun getUltimatePlace(): Int {
        return 0 // TODO remove stub, implement
    }

    fun addTarget(target: BallTarget) =
        ballModel.addTarget(target)

    override fun equals(other: Any?) = other is BallComponent && other.ballImage.id == ballImage.id

    override fun hashCode() = ballImage.id

    override fun onAnimationFrame(currentPlayTime: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }*/

}
