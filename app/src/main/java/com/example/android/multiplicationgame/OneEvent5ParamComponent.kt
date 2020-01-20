package com.example.android.multiplicationgame

abstract class OneEvent5ParamComponent<T1P1, T1P2, T1P3, T1P4, T1P5>: OneEventComponent<(T1P1, T1P2, T1P3, T1P4, T1P5) -> Unit>() {

    protected fun notifyListeners(param1: T1P1, param2: T1P2, param3: T1P3, param4: T1P4, param5: T1P5) {
        listeners.forEach { it(param1, param2, param3, param4, param5) }
    }

}