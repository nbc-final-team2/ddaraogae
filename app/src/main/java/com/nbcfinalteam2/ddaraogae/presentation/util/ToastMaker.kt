package com.nbcfinalteam2.ddaraogae.presentation.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object ToastMaker {
    fun make(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun make(context: Context, @StringRes resId: Int) {
        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
    }
}