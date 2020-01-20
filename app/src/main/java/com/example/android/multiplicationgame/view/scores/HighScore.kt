package com.example.android.multiplicationgame.view.scores

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HighScore(
    @PrimaryKey val id: Long? = null,
    val rank: Int,
    val player: String,
    val score: Int
)