package com.orynastarkina.intelliweights.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface

/**
 * Created by Oryna Starkina on 27.02.2019.
 */

fun showExplanation(activity: Activity,
                            explanationText: String,
                            listener: (DialogInterface, Int) -> Unit) {
    AlertDialog.Builder(activity)
        .setPositiveButton("OK", listener)
        .setMessage(explanationText)
        .create()
        .show()
}