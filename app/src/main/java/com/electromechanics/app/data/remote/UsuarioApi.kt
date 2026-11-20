
package com.electromechanics.app.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class UsuarioRequest(
    val rut: String,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String,
    val especialidad: String?
)

data class UsuarioResponse(
    val id: String,
    val rut: String,
    val nombre: String,
    val correo: String,
    val rol: String,
    val especialidad: String?,
    val activo: Boolean
)

// DATOS PARA INICIAR SESION
data class LoginRequest(
    val rut: String,
    val contrasena: String
)

// RESPUESTA DEL INICIO DE SESION
data class LoginResponse(
    val id: String,
    val rut: String,
    val nombre: String,
    val correo: String,
    val rol: String,
    val especialidad: String?,
    val activo: Boolean
)

interface UsuarioApi {

    @GET("api/usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioResponse>

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuario(
        @Path("id") id: String
    ): UsuarioResponse

    @GET("api/usuarios/rut/{rut}")
    suspend fun obtenerUsuarioPorRut(
        @Path("rut") rut: String
    ): UsuarioResponse

    @POST("api/usuarios")
    suspend fun registrarUsuario(
        @Body usuario: UsuarioRequest
    ): Response<UsuarioResponse>

    // ENDPOINT DE INICIO DE SESION
    @POST("api/usuarios/login")
    suspend fun iniciarSesion(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}
