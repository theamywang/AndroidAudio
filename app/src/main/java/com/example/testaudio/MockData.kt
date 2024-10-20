package com.example.testaudio

import android.content.Context

fun readBase64FromRaw(context: Context): String {
    return context.resources.openRawResource(R.raw.audio_base64).bufferedReader().use { it.readText() }
}