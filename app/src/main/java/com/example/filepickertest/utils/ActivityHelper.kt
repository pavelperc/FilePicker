package com.flamingo.ipynbviewer.utils

import android.app.Activity
import android.content.Intent
import android.util.Log


typealias OnActivityResult = (resultCode: Int, data: Intent?) -> Unit


/** Replaces startActivityForResult with [startForResult] with callback. */
class ActivityHelper(val activity: Activity) {
    companion object {
        private const val TAG = "ActivityHelper"
    }

    var _requestCounter: Int = 0
    val _resultsMap = mutableMapOf<Int, OnActivityResult>()

    /** default startActivityForResult version with a callback. */
    fun startForResult(
        intent: Intent,
        onResult: OnActivityResult
    ) {
        _resultsMap[_requestCounter] = onResult
        activity.startActivityForResult(intent, _requestCounter)
        _requestCounter++
    }

    /** should intercept onActivityResult from the activity. */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        _resultsMap[requestCode]?.invoke(resultCode, data)
            ?: Log.e(TAG, "not found request code $requestCode")

        _resultsMap.remove(requestCode)
    }
}