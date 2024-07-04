package com.nbcfinalteam2.ddaraogae.presentation.shared

import android.app.Activity
import android.graphics.Rect
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class KeyboardCleaner(private val activity: Activity) {
    private var prevFocus: View? = null
    private val gestureDetector = GestureDetector(activity, SingleTapListener())

    fun setPrevFocus(view: View?) {
        prevFocus = view
    }

    fun handleTouchEvent(ev: MotionEvent) {
        gestureDetector.onTouchEvent(ev)
    }

    private inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (e.action == MotionEvent.ACTION_UP && prevFocus is EditText) {
                val prevFocus = prevFocus ?: return false
                val hitRect = Rect()
                prevFocus.getGlobalVisibleRect(hitRect)

                if (!hitRect.contains(e.x.toInt(), e.y.toInt())) {
                    if (activity.currentFocus is EditText && activity.currentFocus != prevFocus) {
                        return false
                    } else {
                        (activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                            prevFocus.windowToken,
                            0
                        )
                        prevFocus.clearFocus()
                    }
                }
            }
            return super.onSingleTapUp(e)
        }
    }
}