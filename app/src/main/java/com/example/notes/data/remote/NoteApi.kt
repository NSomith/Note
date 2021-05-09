package com.example.notes.data.remote

import com.example.notes.data.local.entity.Note
import com.example.notes.data.remote.request.AccountRequest
import com.example.notes.data.remote.request.DeleteRequest
import com.example.notes.data.remote.request.OwnerRequest
import com.example.notes.data.remote.response.SimpleResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NoteApi {

    @POST("/register")
    suspend fun register(
            @Body registerRequest:AccountRequest
    ):Response<SimpleResponse>

    @POST("/login")
    suspend fun login(
            @Body registerRequest:AccountRequest
    ):Response<SimpleResponse>

    @POST("/addNote")
    suspend fun addNote(
            @Body note:Note
    ):Response<ResponseBody> //when we dont have any content in the response only status code then this helps

    @POST("/deleteNote")
    suspend fun deleteNote(
            @Body deleteRequest: DeleteRequest
    ):Response<ResponseBody>

    @POST("/addOwnerToNote")
    suspend fun addOwnerToNote(
            @Body addownerrequest:OwnerRequest
    ):Response<SimpleResponse>

    @GET("/getNotes")
    suspend fun getNotes():Response<List<Note>>


}