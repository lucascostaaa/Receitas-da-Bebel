package com.lucascosta.receitasdabebel.share

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent

fun shareTextToWhatsApp(
    context: Context,
    text: String,
    title: String
) {
    val whatsAppIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        setPackage(WHATSAPP_PACKAGE)
        putExtra(Intent.EXTRA_TEXT, text)
    }

    try {
        context.startActivity(whatsAppIntent)
    } catch (_: ActivityNotFoundException) {
        val chooserIntent = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            },
            title
        )
        context.startActivity(chooserIntent)
    }
}

private const val WHATSAPP_PACKAGE = "com.whatsapp"
