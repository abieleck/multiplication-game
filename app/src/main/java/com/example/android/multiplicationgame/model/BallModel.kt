package com.example.android.multiplicationgame.model

import android.view.View
import android.widget.ImageView
import com.example.android.multiplicationgame.BALL_COUNT
import java.util.*

class BallModel {

    private var basePositionIndex = 0
    private var baseAlpha = 1f
    private val targets = ArrayDeque<BallTarget>(BALL_COUNT + 1)
    var visible: Boolean = false
    var level: Int = 1


    fun putAt(positionIndex: Int, alpha: Float) {
        basePositionIndex = positionIndex
        baseAlpha = alpha
        targets.clear()
    }

    fun addTarget(target: BallTarget) = targets.addLast(target)

    val isMoving
        get() = targets.isNotEmpty()

    fun updateImageVisibility(ballImage: ImageView) {
        ballImage.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }

    fun updateImageLevel(ballImage: ImageView, levelColors: IntArray) {
        ballImage.setColorFilter(levelColors[level - 1])
    }

    fun ultimatePositionIndex() = targets.peekLast()?.positionIndex?:basePositionIndex

    fun ultimateAlpha() = targets.peekLast()?.alpha?:baseAlpha

    fun updateImagePositionAlpha(ballImage: ImageView, t: Long, positions: FloatArray) {
        var resultX = positions[basePositionIndex]
        var resultAlpha = baseAlpha

        val iterator = targets.iterator()
        if (iterator.hasNext()) {
            var prevX = resultX
            var prevAlpha = resultAlpha

            do with(iterator.next()) {
                val targetX = positions[positionIndex]
                if (t >= startTime + duration) { // reached this target
                    basePositionIndex = positionIndex
                    baseAlpha = alpha
                    resultX = targetX
                    resultAlpha = alpha
                    iterator.remove()
                    onEnd?.invoke() // invoke finish action
                } else if (t > startTime) {
                    val interp = interpolate((t - startTime).toFloat() / duration.toFloat())
                    resultX += (targetX - prevX) * interp
                    resultAlpha += (alpha - prevAlpha) * interp
                }
                prevAlpha = alpha
                prevX = targetX
            } while (iterator.hasNext())
        }
        ballImage.setImagePositionAlpha(resultX, resultAlpha)
    }

    private fun ImageView.setImagePositionAlpha(position: Float, alpha: Float) {
        if (x != position) {
            x = position
        }
        if (this.alpha != alpha) {
            this.alpha = alpha
        }
    }

    private fun interpolate(t: Float) = when {
        t >= 1 -> 1f
        else -> ((Math.cos((t + 1) * Math.PI) / 2.0f) + 0.5f).toFloat()
    }

}