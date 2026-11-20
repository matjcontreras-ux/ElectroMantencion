
package com.electromechanics.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class AsistenciaRequest(
    val tecnicoId: String,
    val servicioId: String?,
    val tipo: String,
    val latitud: Double?,
    val longitud: Double?,
    val evidenciaUrl: String?
)

data class AsistenciaResponse(
    val id: String,
    val tecnicoId: String,
    val servicioId: String?,
    val tipo: String,
    val fechaHora: String,
    val latitud: Double?,
    val longitud: Double?,
    val evidenciaUrl: String?
)

interface AsistenciaApi {

    @GET("api/asistencias")
    suspend fun obtenerAsistencias(): List<AsistenciaResponse>

    @GET("api/asistencias/{id}")
    suspend fun obtenerAsistencia(
        @Path("id") id: String
    ): AsistenciaResponse

    @GET("api/asistencias/tecnico/{tecnicoId}")
    suspend fun obtenerPorTecnico(
        @Path("tecnicoId") tecnicoId: String
    ): List<AsistenciaResponse>

    @GET("api/asistencias/servicio/{servicioId}")
    suspend fun obtenerPorServicio(
        @Path("servicioId") servicioId: String
    ): List<AsistenciaResponse>

    @POST("api/asistencias")
    suspend fun registrarAsistencia(
        @Body asistencia: AsistenciaRequest
    ): Response<AsistenciaResponse>

    @DELETE("api/asistencias/{id}")
    suspend fun eliminarAsistencia(
        @Path("id") id: String
    ): Response<Unit>
}
