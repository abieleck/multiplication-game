package com.example.android.multiplicationgame.view.presenter

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.android.multiplicationgame.*
import com.example.android.multiplicationgame.view.ComponentState
import com.example.android.multiplicationgame.view.WaitingComponent

class Presenter(activity: AppCompatActivity): ThreeEvents0P0P0PComponent() {

    companion object {
        private const val SHOW_ON_RESUME_TAG = "SHOW_ON_RESTORE"
        private const val FACTOR1_TAG = "FACTOR1"
        private const val FACTOR2_TAG = "FACTOR2"
        private const val ACTIVE_TAG = "ACTIVE"
    }

    private val popupLayout: ViewGroup
            = activity.layoutInflater.inflate(R.layout.presenter_popup, null) as ViewGroup
    private var popUpWindow = PopupWindow(popupLayout, LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT)
    private val rootView = activity.findViewById<View>(android.R.id.content)
    private val txtFactor1 = popupLayout.findViewById<TextView>(R.id.factor1)
    private val txtFactor2 = popupLayout.findViewById<TextView>(R.id.factor2)
    private val btnResult = popupLayout.findViewById<Button>(R.id.result)

    private val initialWaiter = WaitingComponent()
    private val popFactor1 = PoppingView(txtFactor1, 600L, 0.4f)
    private val popTimesSign = PoppingView(popupLayout.findViewById(R.id.times_sign), 400L, 0.4f)
    private val popFactor2 = PoppingView(txtFactor2, 600L, 0.4f)
    private val popEquals = PoppingView(popupLayout.findViewById(R.id.equals_sign), 400L, 0.4f)
    private val popResult = PoppingView(btnResult, 700L, 0.5f)
    private val fadeInButton = FadeInButtonComponent(btnResult)
    private val tappingHand = BlinkingClickableComponent(popupLayout.findViewById<View>(R.id.tapping_hand))

    private var showOnResume = false

    private var factor1 = 0
    private var factor2 = 0

    private var active = false

    init {
        initialWaiter.addWaitingFinishedListener {
            popUpWindow.show()
            popFactor1.popIn()
        }
        popFactor1.addPoppedInListener(popTimesSign::popIn)
        popTimesSign.addPoppedInListener(popFactor2::popIn)
        popFactor2.addPoppedInListener(popEquals::popIn)
        popEquals.addPoppedInListener(popResult::popIn)
        popResult.addPoppedInListener {
            popResult.log("-> ${fadeInButton.classTag()}.fadeIn()")
            fadeInButton.fadeIn()
        }
        fadeInButton.addFadedInListener {
            fadeInButton.log("-> ${tappingHand.classTag()}.visible=true")
            tappingHand.visible = true
        }
        btnResult.setOnClickListener { dismiss() }
        tappingHand.addOnClickListener(this::dismiss)
        addSubComponents(initialWaiter, popFactor1, popTimesSign, popFactor2, popEquals, popResult, fadeInButton,
            tappingHand)
    }

    fun addPresentationStartListener(presentationStartListener: () -> Unit)
            = addEvent1Listener(presentationStartListener)

    fun addPresentationStartListeners(vararg presentationStartListeners: () -> Unit)
            = addEvent1Listeners(*presentationStartListeners)

    private fun notifyPresentationStartListeners() = notifyEvent1Listeners()

    fun addPresentationSkipListener(presentationSkipListener: () -> Unit) = addEvent2Listener(presentationSkipListener)

    fun addPresentationSkipListeners(vararg presentationSkipListeners: () -> Unit)
            = addEvent2Listeners(*presentationSkipListeners)

    private fun notifyPresentationSkipListeners() =
        notifyEvent2Listeners()

    fun addPresentationEndListener(presentationEndListener: () -> Unit) {
        addEvent3Listener(presentationEndListener)
    }

    fun addPresentationEndListeners(vararg presentationEndListeners: () -> Unit) {
        addEvent3Listeners(*presentationEndListeners)
    }

    private fun notifyPresentationEndListeners() = notifyEvent3Listeners()

    private fun dismiss() {
        popUpWindow.dismiss()
        active = false
        notifyPresentationEndListeners()
    }

    fun showAnswerPresentation(factor1: Int, factor2: Int, correct: Boolean) {
        if (correct) {
            notifyPresentationSkipListeners()
        } else {
            active = true
            notifyPresentationStartListeners()
            putTextOnScreen(factor1, factor2)

            initialWaiter.cancel()
            popFactor1.hide()
            popTimesSign.hide()
            popFactor2.hide()
            popEquals.hide()
            fadeInButton.hide()
            tappingHand.visible = false
            popResult.hide()

            initialWaiter.startWaiting(1000L)
        }
    }

    override fun saveThisComponent() = ComponentState().apply {
        put(ACTIVE_TAG, active)
        if (active) {
            put(FACTOR1_TAG, factor1)
            put(FACTOR2_TAG, factor2)
            put(SHOW_ON_RESUME_TAG, popUpWindow.isShowing)
        }
    }

    override fun restoreThisComponent(state: ComponentState) {
        with (state) {
            active = getBoolean(ACTIVE_TAG)
            if (active) {
                showOnResume = getBoolean(SHOW_ON_RESUME_TAG)
                factor1 = getInt(FACTOR1_TAG)
                factor2 = getInt(FACTOR2_TAG)
                putTextOnScreen(getInt(FACTOR1_TAG), getInt(FACTOR2_TAG))
            }
        }
    }

    override fun destroyThisComponent() {
        if (popUpWindow.isShowing) {
            popUpWindow.dismiss()
        }
    }

    private fun putTextOnScreen(factor1: Int, factor2: Int) {
        this.factor1 = factor1
        this.factor2 = factor2
        txtFactor1.text = factor1.toString()
        txtFactor2.text = factor2.toString()
        btnResult.text = (factor1 * factor2).toString()
    }

    override fun resumeThisComponent() {
        if (active && showOnResume) {
            popUpWindow.show()
            showOnResume = false
        }
    }

    private fun PopupWindow.show() {
        showAtLocation(rootView, Gravity.CENTER, 0, 0)
    }

}