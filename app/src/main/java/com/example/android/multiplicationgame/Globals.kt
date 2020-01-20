package com.example.android.multiplicationgame

import android.app.Activity
import android.view.View
import androidx.core.content.ContextCompat

val buttonIds = intArrayOf(
    R.id.button_0,
    R.id.button_1,
    R.id.button_2,
    R.id.button_3,
    R.id.button_4,
    R.id.button_5,
    R.id.button_6,
    R.id.button_7,
    R.id.button_8,
    R.id.button_9,
    R.id.backspace
)

/*val ballIds = intArrayOf(
    R.id.img_ball_0,
    R.id.img_ball_1,
    R.id.img_ball_2,
    R.id.img_ball_3,
    R.id.img_ball_4,
    R.id.img_ball_5,
    R.id.img_ball_6,
    R.id.img_ball_7,
    R.id.img_ball_8,
    R.id.img_ball_9,
    R.id.img_ball_10,
    R.id.img_ball_11
)*/

const val LEVEL_COUNT = 10
const val BALL_QUEUE_SIZE = 10
const val BALL_COUNT = BALL_QUEUE_SIZE + 2
const val CREATE_POSITION_INDEX = BALL_QUEUE_SIZE + 1
const val LAUNCH_POSITION_INDEX = BALL_QUEUE_SIZE
const val FIRST_POSITION_INDEX = 0
const val LAST_POSITION_INDEX = BALL_QUEUE_SIZE - 1

/*const val LAUNCH_BALL_ID = R.id.img_ball_10
const val CREATE_BALL_ID = R.id.img_ball_11*/

const val BALL_LAUNCHER_INITIAL_DELAY = 0L
const val BALL_LAUNCHER_INTERVAL = 2000L



