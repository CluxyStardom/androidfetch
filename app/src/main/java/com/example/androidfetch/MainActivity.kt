package com.example.androidfetch

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tiktokLink = findViewById<TextView>(R.id.tiktok_link)
        val text = "follow me on tiktok"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("tiktok")
        val endIndex = startIndex + "tiktok".length
        spannableString.setSpan(ForegroundColorSpan(Color.BLUE), startIndex, endIndex, 0)
        tiktokLink.text = spannableString

        tiktokLink.setOnClickListener {
            val url = "https://www.tiktok.com/@cluxystardom"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            Toast.makeText(this, "Redirecting to TikTok...", Toast.LENGTH_SHORT).show()
        }
    }
}