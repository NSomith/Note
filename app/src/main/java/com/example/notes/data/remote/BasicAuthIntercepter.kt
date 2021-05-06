package com.example.notes.data.remote

import com.example.notes.other.Constants.IGONORE_AUTH_URLS
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthIntercepter :Interceptor{
    var email:String? = null
    var password:String?= null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request() //check the request
        if(request.url.encodedPath in IGONORE_AUTH_URLS){
            return chain.proceed(request)
        }

        val authenticatedRequest = request.newBuilder()
                .header("Authorization",Credentials.basic(email?:"",password?:""))
                .build()
        return chain.proceed(authenticatedRequest)
    }
}