package com.example.daawahtv.network

import retrofit2.http.GET
import retrofit2.http.Path

interface DashboardApiService {

    // This function returns a DashboardResponse and should now compile correctly
    @GET("streamit/api/v1/content/dashboard/{type}")
    suspend fun getDashboardContent(
        @Path("type") type: String // e.g., "home"
    ): DashboardResponse
}