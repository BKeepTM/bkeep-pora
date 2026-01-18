package com.example.bkeep.network.api

import com.example.lib.data.hive.CreateHiveRequest
import com.example.lib.data.hive.Weight
import com.example.lib.data.location.HiveLocation
import com.example.lib.data.notes.CreateNoteRequest
import com.example.lib.data.notes.Note
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotesApi {

    @GET("notes/{hiveId}")
    suspend fun getHiveNotes(@Path("hiveId") hiveId: Int): Response<List<Note>>

    @POST("notes")
    suspend fun createNote(@Body request: CreateNoteRequest): Response<Unit>

    @POST("notes/remove")
    suspend fun deleteNote(@Query("id") noteId: Int): Response<Unit>

}