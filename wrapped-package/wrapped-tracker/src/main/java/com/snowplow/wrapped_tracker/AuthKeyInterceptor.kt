package com.mcd.japan

import androidx.annotation.CheckResult
import androidx.annotation.RestrictTo
import okhttp3.Interceptor
import okhttp3.Response
import org.jetbrains.annotations.NotNull

@RestrictTo(RestrictTo.Scope.LIBRARY)
class AuthKeyInterceptor(@NotNull private val apiKey: String) : Interceptor {
    @CheckResult
    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("apiKey", apiKey)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
