package com.example.data.musclewiki

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * MuscleWiki REST API Interface for fetching exercise movement form videos,
 * step-by-step instructions, and multi-angle recordings.
 */
interface MuscleWikiApiService {

    @GET("exercises")
    suspend fun getExercises(
        @Header("X-API-Key") apiKey: String? = null,
        @Header("Authorization") authHeader: String? = null,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("muscle") muscle: String? = null,
        @Query("limit") limit: Int = 20
    ): List<MuscleWikiExercise>

    @GET("exercises/{id}")
    suspend fun getExerciseById(
        @Path("id") id: Int,
        @Header("X-API-Key") apiKey: String? = null,
        @Header("Authorization") authHeader: String? = null
    ): MuscleWikiExercise
}
