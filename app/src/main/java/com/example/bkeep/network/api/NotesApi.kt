package com.example.bkeep.network.api

import com.example.lib.data.hive.Weight
import com.example.lib.data.location.HiveLocation
import com.example.lib.data.notes.Note
import retrofit2.Response
import retrofit2.http.GET

interface NotesApi {

    @GET("notes/{id}")
    suspend fun getHiveNotes(hiveId: Int):Response<List<Note>>

}