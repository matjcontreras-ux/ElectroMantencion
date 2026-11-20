
package com.electromechanics.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class ServicioRequest(
    val codigo: String,
    val titulo: String,
    val descripcion: String?,
    val direccion: String,
    val estado: String,
    val prioridad: String,
    val tecnicoId: String?,
    val fechaProgramada: String?
)

data class ServicioResponse(
    val id: String,
    val codigo: String,
    val titulo: String,
    val descripcion: String?,
    val direccion: String,
    val estado: String,
    val prioridad: String,
    val tecnicoId: String?,
    val fechaProgramada: String?
)

interface ServicioApi {

    @GET("api/servicios")
    suspend fun obtenerServicios(): List<ServicioResponse>

    @GET("api/servicios/{id}")
    suspend fun obtenerServicio(
        @Path("id") id: String
    ): ServicioResponse

    @POST("api/servicios")
    suspend fun registrarServicio(
        @Body servicio: ServicioRequest
    ): Response<ServicioResponse>

    @PUT("api/servicios/{id}")
    suspend fun actualizarServicio(
        @Path("id") id: String,
        @Body servicio: ServicioRequest
    ): Response<ServicioResponse>

    @DELETE("api/servicios/{id}")
    suspend fun eliminarServicio(
        @Path("id") id: String
    ): Response<Unit>
}
