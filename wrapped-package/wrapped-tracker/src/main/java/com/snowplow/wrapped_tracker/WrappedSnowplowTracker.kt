package com.snowplow.wrapped_tracker

import android.content.Context
import android.util.Log
import com.mcd.japan.AuthKeyInterceptor
import com.snowplowanalytics.snowplow.Snowplow
import com.snowplowanalytics.snowplow.configuration.EmitterConfiguration
import com.snowplowanalytics.snowplow.configuration.NetworkConfiguration
import com.snowplowanalytics.snowplow.configuration.SessionConfiguration
import com.snowplowanalytics.snowplow.configuration.TrackerConfiguration
import com.snowplowanalytics.snowplow.controller.TrackerController
import com.snowplowanalytics.snowplow.network.HttpMethod
import com.snowplowanalytics.snowplow.network.RequestCallback
import com.snowplowanalytics.snowplow.tracker.DevicePlatform
import com.snowplowanalytics.snowplow.tracker.LogLevel
import com.snowplowanalytics.snowplow.tracker.LoggerDelegate
import com.snowplowanalytics.snowplow.util.TimeMeasure
import okhttp3.OkHttpClient
import org.jetbrains.annotations.NotNull
import java.util.*
import java.util.concurrent.TimeUnit

class WrappedSnowplowTracker: LoggerDelegate {

    private val tracker: TrackerController
    //    private val appConfig = Properties()
    private val config: Properties
    private val endpoint: String
    private val apiKey: String

    private val okHttpClient: OkHttpClient
    private val httpMethod: HttpMethod
    private val networkConfig: NetworkConfiguration

    private val foregroundTimeout: TimeMeasure
    private val backgroundTimeout: TimeMeasure
    private val sessionConfig: SessionConfiguration

    private val emitRange: Int
    private val threadPoolSize: Int
    private val byteLimitGet: Int
    private val byteLimitPost: Int
    private val emitterConfig: EmitterConfiguration

    private val appId: String
    private val namespace: String
    private val devicePlatform: DevicePlatform
    private val setBase64Encoding: Boolean
    private val setScreenContext: Boolean
    private val setSessionContext: Boolean
    private val setPlatformContext: Boolean
    private val setApplicationContext: Boolean
    private val setGeoLocationContext: Boolean
    private val setDeepLinkContext: Boolean
    private val setLifecycleAutoTracking: Boolean
    private val setScreenViewAutoTracking: Boolean
    private val setExceptionAutoTracking: Boolean
    private val setInstallAutoTracking: Boolean
    private val setDiagnosticAutoTracking: Boolean
    private val trackerConfig: TrackerConfiguration

    constructor(@NotNull applicationContext: Context, @NotNull endpoint: String, @NotNull apiKey: String) {
        config = mapOf(
            "setLifecycleAutoTracking" to "",
            "setScreenViewAutoTracking" to "",
            "setExceptionAutoTracking" to "",
            "setInstallAutoTracking" to "true",
            "setDiagnosticAutoTracking" to ""
        ).toProperties()
        this.endpoint = endpoint
        this.apiKey = apiKey
        okHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(AuthKeyInterceptor(this.apiKey))
            .build()
        httpMethod = HttpMethod.POST

        networkConfig = NetworkConfiguration(
            this.endpoint,
            httpMethod
        ).okHttpClient(okHttpClient)

        foregroundTimeout = TimeMeasure(30, TimeUnit.MINUTES)
        backgroundTimeout = TimeMeasure(30, TimeUnit.MINUTES)
        sessionConfig = SessionConfiguration(
            foregroundTimeout,
            backgroundTimeout
        )

        emitRange = 500
        threadPoolSize = 20
        byteLimitGet = 52000
        byteLimitPost = 52000
        emitterConfig = EmitterConfiguration()
            .requestCallback(getRequestCallback())
            .emitRange(emitRange)
            .threadPoolSize(threadPoolSize)
            .byteLimitGet(byteLimitGet)
            .byteLimitPost(byteLimitPost)

        appId = "android"
        namespace = "android"
        devicePlatform = DevicePlatform.Mobile
        setBase64Encoding = true //
        setScreenContext = true
        setSessionContext = true
        setPlatformContext = true
        setApplicationContext = true
        setGeoLocationContext = true
        setDeepLinkContext = true
        setLifecycleAutoTracking =
            if (config.contains("setLifecycleAutoTracking")) config.getProperty("setLifecycleAutoTracking")
                .toBoolean() else false
        setScreenViewAutoTracking =
            if (config.contains("setScreenViewAutoTracking")) config.getProperty("setScreenViewAutoTracking")
                .toBoolean() else false
        setExceptionAutoTracking =
            if (config.contains("setExceptionAutoTracking")) config.getProperty("setExceptionAutoTracking")
                .toBoolean() else false
        setInstallAutoTracking =
            if (config.contains("setInstallAutoTracking")) config.getProperty("setInstallAutoTracking")
                .toBoolean() else false
        setDiagnosticAutoTracking =
            if (config.contains("setDiagnosticAutoTracking")) config.getProperty("setDiagnosticAutoTracking")
                .toBoolean() else false
        trackerConfig = TrackerConfiguration(appId)
            .logLevel(LogLevel.VERBOSE)
            .loggerDelegate(this)
            .devicePlatform(devicePlatform)
            .base64encoding(setBase64Encoding)
            .sessionContext(setSessionContext)
            .screenContext(setScreenContext)
            .platformContext(setPlatformContext)
            .applicationContext(setApplicationContext)
            .geoLocationContext(setGeoLocationContext)
            .deepLinkContext(setDeepLinkContext)
            .lifecycleAutotracking(setLifecycleAutoTracking)
            .screenViewAutotracking(setScreenViewAutoTracking)
            .exceptionAutotracking(setExceptionAutoTracking)
            .installAutotracking(setInstallAutoTracking)
            .diagnosticAutotracking(setDiagnosticAutoTracking)

        tracker = Snowplow.createTracker(
            applicationContext,
            namespace,
            networkConfig,
            sessionConfig,
            emitterConfig,
            trackerConfig
        )
    }

    fun trackScreenView(
        timeOnScreen: Int,
        scrollDepth: Int,
        screenName: String
    ) {

        val screenViewEvent = ScreenViewEvent(
            timeOnScreen,
            scrollDepth,
            screenName
        )

        tracker.track(screenViewEvent.data)
    }


    private fun getRequestCallback(): RequestCallback? {
        return object : RequestCallback {
            override fun onSuccess(successCount: Int) {
                updateLogger(
                    "Emitter Send Success:\n " +
                            "- Events sent: " + successCount + "\n"
                )
            }

            override fun onFailure(successCount: Int, failureCount: Int) {
                updateLogger(
                    (
                            "Emitter Send Failure:\n " +
                                    "- Events sent: " + successCount + "\n " +
                                    "- Events failed: " + failureCount + "\n"
                            )
                )
            }
        }
    }

    private fun updateLogger(s: String) {
        Log.v("verbose", s)
    }

    override fun error(tag: String, msg: String) {
        if (msg != null) {
            Log.v("error", msg)
        }
    }

    override fun debug(tag: String, msg: String) {
        if (msg != null) {
            Log.v("debug", msg)
        }
    }

    override fun verbose(tag: String, msg: String) {
        if (msg != null) {
            Log.v("verbose", msg)
        }
    }
}
