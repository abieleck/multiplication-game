package com.example.android.multiplicationgame.view.scores

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.android.multiplicationgame.OneEvent1ParamComponent
import com.example.android.multiplicationgame.R
import com.example.android.multiplicationgame.view.ComponentState


class PlayerNameReaderComponent(private val activity: AppCompatActivity) :
    OneEvent1ParamComponent<String?>() {

    companion object {
        private const val ACTIVE_TAG = "ACTIVE"
        private const val SHOW_ON_RESUME_TAG = "SHOW_ON_RESUME"
    }

    private val popupLayout: ViewGroup =
        activity.layoutInflater.inflate(R.layout.add_high_score, null) as ViewGroup
    private var popUpWindow = PopupWindow(
        popupLayout, LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT, true
    )
    private val rootView = activity.findViewById<View>(android.R.id.content)
    private val btnCancel = popupLayout.findViewById<Button>(R.id.btn_add_score_cancel)
    private val btnOk = popupLayout.findViewById<Button>(R.id.btn_add_score_ok)
    private val lstScoreList = popupLayout.findViewById<ListView>(R.id.score_list)
    private val highScoresAdapter = HighScoresAdapter()

    private var active = false
    private var showOnResume = false
    private var edtPlayerName: EditText? = null

    init {
        btnOk.setOnClickListener { returnName() }
        btnCancel.setOnClickListener { cancel() }
        lstScoreList.itemsCanFocus = true
        lstScoreList.adapter = HighScoresAdapter()
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(ACTIVE_TAG, active)
        if (active) {
            put(SHOW_ON_RESUME_TAG, popUpWindow.isShowing)
        }
    }

    override fun restoreThisComponent(state: ComponentState) =
        with(state) {
            active = getBoolean(ACTIVE_TAG)
            if (active) {
                showOnResume = getBoolean(SHOW_ON_RESUME_TAG)
            }
        }

    fun readPlayerName(addScoreList: List<HighScoreEdit>) {
        active = true
        highScoresAdapter.highScores = addScoreList
        popUpWindow.show()
    }

    override fun resumeThisComponent() {
        if (active && showOnResume) {
            popUpWindow.show()
            showOnResume = false
        }
    }

    fun addPlayerNameListener(playerNameListener: (playerName: String?) -> Unit) {
        addEvent1Listener(playerNameListener)
    }

    private fun PopupWindow.show() {
        showAtLocation(rootView, Gravity.CENTER, 0, 0)
    }

    private fun returnName() {
        popUpWindow.dismiss()
        active = false
        notifyListeners(edtPlayerName?.text?.toString())
    }

    private fun cancel() {
        popUpWindow.dismiss()
        active = false
        notifyListeners(null)
    }

    override fun destroyThisComponent() {
        if (popUpWindow.isShowing) {
            popUpWindow.dismiss()
        }
    }

    override fun thisComponentOnLayout() {
        // to allow visible keyboard for EditText inside ListView
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private inner class HighScoresAdapter : BaseAdapter() {

        var highScores: List<HighScoreEdit> = emptyList()
            set(value) {
                field = value
                lstScoreList.adapter = this
            }

        override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
            val scoreData: HighScoreEdit = highScores[position]
            val view = convertView
                ?: getListView(highScores[position], container)
            return view.apply {
                findViewById<TextView>(R.id.score_rank).text = scoreData.rank.toString()
                findViewById<TextView>(R.id.score_result).text = scoreData.score.toString()
                when (scoreData.player) {
                    null -> edtPlayerName = findViewById<EditText>(R.id.score_player_add)
                        .apply { text.clear() }
                    else -> findViewById<TextView>(R.id.score_player_show).text = scoreData.player
                }
            }
        }

        private fun getListView(highScoreEdit: HighScoreEdit, container: ViewGroup): View {
            val viewId = highScoreEdit.player
                ?.let { R.layout.high_score_present }
                ?: R.layout.high_score_edit
            return activity.layoutInflater.inflate(viewId, container, false)
        }

        override fun getViewTypeCount(): Int = 2

        override fun getItemViewType(position: Int): Int = when (highScores[position].player) {
            null -> 0
            else -> 1
        }

        override fun getItem(position: Int): HighScoreEdit = highScores[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int =
            highScores.size


    }

}