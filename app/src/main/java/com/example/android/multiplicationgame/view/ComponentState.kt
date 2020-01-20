package com.example.android.multiplicationgame.view

import android.os.Bundle
import android.view.View
import com.example.android.multiplicationgame.view.ComponentState.Companion.DEFAULT_NAMESPACE

class ComponentState(private val bundle: Bundle) {

    companion object {
        const val DEFAULT_NAMESPACE = 0
    }

    constructor() : this(Bundle())

    fun put(tag: String, state: ComponentState, namespace: Int = DEFAULT_NAMESPACE) {
        bundle.putBundle(getKey(tag, namespace), state.bundle)
    }

    fun put(tag: String, integer: Int, namespace: Int = DEFAULT_NAMESPACE) {
        bundle.putInt(getKey(tag, namespace), integer)
    }

    fun put(tag: String, long: Long, namespace: Int = DEFAULT_NAMESPACE) {
        bundle.putLong(getKey(tag, namespace), long)
    }


    fun put(tag: String, float: Float, namespace: Int = DEFAULT_NAMESPACE) {
        bundle.putFloat(getKey(tag, namespace), float)
    }

    fun put(tag: String, boolean: Boolean, namespace: Int = DEFAULT_NAMESPACE) {
        bundle.putBoolean(getKey(tag, namespace), boolean)
    }

    fun put(tag: String, string: String, namespace: Int = DEFAULT_NAMESPACE) {
        bundle.putString(getKey(tag, namespace), string)
    }

    fun put(tag: String, intCollection: Collection<Int>, namespace: Int = DEFAULT_NAMESPACE) {
        bundle.putIntArray(getKey(tag, namespace), intCollection.toIntArray())
    }

    fun getState(tag: String, namespace: Int = DEFAULT_NAMESPACE): ComponentState? =
        bundle.getBundle(getKey(tag, namespace))?.let { ComponentState(it) }

    fun getInt(tag: String, namespace: Int = DEFAULT_NAMESPACE) =
        bundle.getInt(getKey(tag, namespace))

    fun getLong(tag: String, namespace: Int = DEFAULT_NAMESPACE) =
        bundle.getLong(getKey(tag, namespace))

    fun getFloat(tag: String, namespace: Int = DEFAULT_NAMESPACE) =
        bundle.getFloat(getKey(tag, namespace))

    fun getBoolean(tag: String, namespace: Int = DEFAULT_NAMESPACE) =
        bundle.getBoolean(getKey(tag, namespace))

    fun getString(tag: String, namespace: Int = DEFAULT_NAMESPACE) =
        bundle.getString(getKey(tag, namespace))

    fun <T : MutableCollection<Int>> getIntCollection(
        tag: String, namespace: Int = DEFAULT_NAMESPACE, collectionFactory: () -> T
    ) = collectionFactory().apply {
        bundle.getIntArray(getKey(tag, namespace))?.forEach { add(it) }
    }

    private fun getKey(tag: String, namespace: Int) = namespace.toString() + '_' + tag

}