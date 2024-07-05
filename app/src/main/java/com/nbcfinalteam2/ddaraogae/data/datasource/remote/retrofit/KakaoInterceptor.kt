package com.nbcfinalteam2.ddaraogae.data.datasource.remote.retrofit

import okhttp3.Interceptor
import okhttp3.Response

object KakaoInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("page", "1")
            .addQueryParameter("sort", "distance")
            .addQueryParameter("radius", "3000")
            .build()

        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", Key.KAKAO_API_KEY)
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}