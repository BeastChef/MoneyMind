package com.example.moneymind.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymind.utils.LocaleHelper

open class BaseActivityK : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val lang = newBase.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("app_lang", "ru") ?: "ru"
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }
}