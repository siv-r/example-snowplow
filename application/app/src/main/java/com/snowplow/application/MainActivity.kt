package com.snowplow.application

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.snowplow.wrapped_tracker.WrappedSnowplowTracker
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val screenViewButton = findViewById<Button>(R.id.screenViewButton)

//        val config = Properties()
//        config.load(applicationContext.assets.open("snowplow.properties"))

        val endpoint="https://collector-endpoint.com"
        val apiKey="apiKey"
        val tracker = WrappedSnowplowTracker(applicationContext,endpoint,apiKey)

        screenViewButton.setOnClickListener {
            tracker.trackScreenView(
                timeOnScreen = 123,
                scrollDepth = 50,
                screenName = "home_page"
            )
        }
    }
}