package com.example.android.multiplicationgame

import android.util.Log

fun Any.log(message: String) {
    val logTag = "___${classTag()}"
    Log.d(logTag, message)
}

fun Any.classTag() = "${javaClass.simpleName}@${shortHashCode()}"

private fun Any.shortHashCode(): Int {
    var code = hashCode()
    var result = 0
    for (i in 0..2) {
        result += code % 1000
        code /= 1000
    }
    return result % 1000

}