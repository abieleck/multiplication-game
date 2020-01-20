package com.example.android.multiplicationgame.view

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import com.example.android.multiplicationgame.log

abstract class GameComponent {

    companion object {
        const val SUB_COMPONENT_STATE_NAMESPACE = ComponentState.DEFAULT_NAMESPACE + 1
        const val ROOT_COMPONENT_NAMESPACE = SUB_COMPONENT_STATE_NAMESPACE + 1
        private const val ROOT_COMPONENT_TAG = "ROOT_COMPONENT"
        private const val SLEEPING_TAG = "SLEEPING"
    }

    private var activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks? = null

    private val subComponents: MutableList<GameComponent> = ArrayList()

    /**
     * Boolean flag indicating whether this component has been put to sleep by
     * [sleep] method
     */
    var sleeping = false
        private set

    fun makeRoot(activity: Activity, savedInstanceState: Bundle?) {
        log("makeRoot(), register callbacks")
        // TODO register only when app started for the first time
        // TODO unregister only when activity destroyed for good
        activityLifecycleCallbacks = ActivityLifecycleCallbacks(activity).apply { activityWasRecreated = true }
        activity.application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)

        if (savedInstanceState == null) {
            init()
        } else {
            log("makeRoot(), restore state")
            restoreState(ComponentState(savedInstanceState)
                .getState(ROOT_COMPONENT_TAG, ROOT_COMPONENT_NAMESPACE))
        }

        val mainLayout = activity.findViewById<View>(android.R.id.content)
        mainLayout?.doOnLayout {
            onLayout()
            resume()
        }

    }

    protected fun addSubComponents(vararg subComponents: GameComponent) {
        subComponents.forEach { this.subComponents.add(it) }
    }

    /**
     * Save this component state. This method is used to save the component state before the activity is destroyed.
     * The logic to save the component state should be provided by overriding method [saveThisComponent]
     */
    fun saveState(): ComponentState = saveThisComponent().apply {
        subComponents.forEachIndexed { index, subComponent ->
            put(index.toString(), subComponent.saveState(), SUB_COMPONENT_STATE_NAMESPACE)
        }
        put(SLEEPING_TAG, sleeping) // TODO move this out of the default namespace to avoid conflicts
    }

    /**
     * Restores this component state. This method is used to restore component state after the activity
     * is recreated. The logic to restore the component state should be provided by overriding method
     * [restoreThisComponent]
     */
    fun restoreState(state: ComponentState?) {
        if (state == null) return
        restoreThisComponent(state)
        subComponents.forEachIndexed { index, subComponent ->
            subComponent.restoreState(state.getState(index.toString(), SUB_COMPONENT_STATE_NAMESPACE))
        }
        sleeping = state.getBoolean(SLEEPING_TAG)
    }

    /**
     * Sets the component's initial state when the activity is started. This method is not called when the
     * activity is recreated after configuration change - in that case the [restoreState] is called.
     * The method calls [initThisComponent] and then recursively initializes each sub-component.
     */
    fun init() {
        initThisComponent()
        subComponents.forEach(GameComponent::init)
    }

    /**
     * Pauses the activities of component. sleep/[wake] should be used instead of [pause]/[resume]
     * to implement pausing the component while the game is running (e.g. when displaying
     * popup).
     * If the component is not sleeping, this method calls [sleepThisComponent] (which should
     * be overwritten to provide desired behaviour) and then puts recursively all sub-components
     * to sleep.
     */
    fun sleep() {
        if (!sleeping) {
            sleeping = true
            sleepThisComponent()
            subComponents.forEach(GameComponent::sleep)
        }
    }

    /**
     * Resumes the activities of component paused by [sleep] method. [sleep]/wake
     * should be used instead of [pause]/[resume] to implement pausing the
     * component while the game is running (e.g. when displaying popup).
     * If the component is sleeping, this method calls [wakeThisComponent] (which should
     * be overwritten to provide desired behaviour) and then wakes recursively all sub-components.
     */
    fun wake() {
        if (sleeping) {
            sleeping = false
            wakeThisComponent()
            subComponents.forEach(GameComponent::wake)
        }
    }

    /**
     * Pauses the activities of this component. This method is called when the
     * android activity is paused.
     * The method pauses all sub-components and calls [pauseThisComponent], which
     * should be overwritten to provide desired logic.
     */
    fun pause() {
        pauseThisComponent()
        subComponents.forEach(GameComponent::pause)
    }

    fun destroy() {
        destroyThisComponent()
        subComponents.forEach(GameComponent::destroy)
    }

    /**
     * Resumes the activities of this component. This method is called when the android activity
     * resumes after pause or is created for the first time. Unlike onResume, the method is always
     * called after the main activity layout is laid out.
     * The method resumes all sub-components and then calls [resumeThisComponent], which should be
     * overwritten to provide desired logic.
     */
    fun resume() {
        subComponents.forEach(GameComponent::resume)
        resumeThisComponent()
    }

    /**
     * Called when the main activity layout is laid out, always before [resume]. The method calls
     * [thisComponentOnLayout] (which should be overwritten to provide desired behaviour) and then calls
     * onLayout for each sub-component.
     */
    fun onLayout() {
        thisComponentOnLayout()
        subComponents.forEach(GameComponent::onLayout)
    }

    /**
     * Provides logic to initialize the state of this component after the activity is started. The method is
     * used when the activity is started for the first time. It is not used when the activity is recreated
     * - in that case the [restoreThisComponent] is called.
     */
    protected open fun initThisComponent() = Unit

    /**
     * This method is called every time the main activity layout is laid out after the activity is started
     * or restarted. The method should contain logic to take necessary measurements. Component's activities should
     * be resumed in [resumeThisComponent]
     */
    protected open fun thisComponentOnLayout() = Unit

    /**
     * Overwrite this method to provide logic for pausing this componnet's activities while the game
     * is running. This method should not be called directly - use [sleep] instead, which also puts to
     * sleep all sub-components.
     */
    protected open fun sleepThisComponent() = Unit

    /**
     * Provides logic for deactivation of this component in case the Android activity is paused).
     * It might be necessary to check the [sleeping] flag to determine if the activities need to be paused.
     */
    protected open fun pauseThisComponent() = Unit

    /**
     * Overwrite this method to provide logic to save this component state
     */
    protected abstract fun saveThisComponent(): ComponentState

    /**
     * Overwrite this method to provide logic to destroy this component when activity calls onDestroy
     */
    protected open fun destroyThisComponent() = Unit

    /**
     * Overwrite this method to provide logic to restore this component state
     */
    protected abstract fun restoreThisComponent(state: ComponentState)

    /**
     * Provides logic for activation of this component when the Android activity is resumed.
     * It might be necessary to check the [sleeping] flag to determine if the activities should be started.
     *
     * This method is called if the activity is resumed after pause, recreated or created for the first time.
     * It is always called when the activity layout is laid out.
     */
    protected open fun resumeThisComponent() = Unit

    /**
     * Overwrite this method to provide logic for resuming the component's activities after it has been put
     * to sleep by [sleep] method. This method should not be called directly - use [wake] instead, which also
     * wakes all sub-components.
     */
    protected open fun wakeThisComponent() = Unit


    private inner class ActivityLifecycleCallbacks(private val activity: Activity): Application.ActivityLifecycleCallbacks {

        var activityWasRecreated = true

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            if (activity != null && activity::class == this.activity::class) {
                /*if (savedInstanceState == null) {
                    init()
                } else {
                    restoreState(ComponentState(savedInstanceState)
                        .getState(ROOT_COMPONENT_TAG, ROOT_COMPONENT_NAMESPACE))
                }

                val mainLayout = activity?.findViewById<View>(android.R.id.content)
                mainLayout?.doOnLayout {
                    onLayout()
                    resume()
                }*/

                //activityWasRecreated = true
            }
        }

        override fun onActivityPaused(activity: Activity?) {
            if (activity != null && activity::class == this.activity::class) {
                pause()
                activityWasRecreated = false
            }
        }

        override fun onActivityDestroyed(activity: Activity?) {
            if (activity != null && activity::class == this.activity::class) {
                destroy()
                log("unregisterActivityLifecycleCallbacks()")
                activity.application.unregisterActivityLifecycleCallbacks(this)
            }
        }

        override fun onActivityResumed(activity: Activity?) {
            if (activity != null && activity::class == this.activity::class && !activityWasRecreated) {
                resume()
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            if (activity != null && activity::class == this.activity::class && outState != null) {
                this@GameComponent.log("onActivitySaveInstanceState()")
                ComponentState(outState).put(ROOT_COMPONENT_TAG, saveState(), ROOT_COMPONENT_NAMESPACE)
            }
        }

        override fun onActivityStarted(activity: Activity?) = Unit

        override fun onActivityStopped(activity: Activity?) = Unit

    }

}