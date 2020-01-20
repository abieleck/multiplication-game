package com.example.android.multiplicationgame.view

import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button

import androidx.appcompat.widget.AppCompatButton
import com.example.android.multiplicationgame.R

const val SELECTED_LEVEL_KEY = "LEVEL"
private const val SELECTED_BUTTON_SCALE = 1.6f

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val preferences = PreferenceManager.getDefaultSharedPreferences(application)
        // fetch level
        var selectedLevel = preferences.getInt(SELECTED_LEVEL_KEY, 0)
        if (selectedLevel == 0) { // level not saved yet, use starting value of 1
            selectedLevel = 1
            preferences.edit().putInt(SELECTED_LEVEL_KEY, selectedLevel).apply()
        }

        // fetch level selectors
        val selectors: List<AppCompatButton> = intArrayOf(
            R.id.btn_level_1,
            R.id.btn_level_2,
            R.id.btn_level_3,
            R.id.btn_level_4,
            R.id.btn_level_5,
            R.id.btn_level_6,
            R.id.btn_level_7,
            R.id.btn_level_8,
            R.id.btn_level_9,
            R.id.btn_level_10
        ).map(this::findViewById)

        selectors.forEachIndexed { i, button ->
            button.setOnClickListener {
                val previousButton = selectors[selectedLevel - 1]
                ValueAnimator.ofFloat(1f, SELECTED_BUTTON_SCALE).apply {
                    duration = 400
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        with(previousButton) {
                            val scale = 1 + SELECTED_BUTTON_SCALE - value
                            scaleX = scale
                            scaleY = scale
                        }
                        with(button) {
                            scaleX = value
                            scaleY = value
                        }
                    }
                    start()
                }
                // update level
                if (i != selectedLevel - 1) {
                    selectedLevel = i + 1
                    preferences.edit().putInt(SELECTED_LEVEL_KEY, selectedLevel).apply()
                }
            }
        }

        // select level button for selected level
        with(selectors[selectedLevel - 1]) {
            scaleX = SELECTED_BUTTON_SCALE
            scaleY = SELECTED_BUTTON_SCALE
        }

        // make Play button start game activity
        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            intent = Intent(this@MainActivity, GameActivity::class.java).apply {
                putExtra(SELECTED_LEVEL_KEY, selectedLevel)
            }
            startActivity(intent)
        }
    }


}
