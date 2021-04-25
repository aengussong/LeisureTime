package com.aengussong.prioritytime.util.extention

import android.widget.EditText
import android.widget.TextView

fun EditText.setOnEditorActionListener(imeAction: Int, onAction: (textView: TextView) -> Unit) {
    setOnEditorActionListener { textView, actionId, _ ->
        if (actionId == imeAction) {
            onAction(textView)
            true
        } else {
            false
        }
    }
}