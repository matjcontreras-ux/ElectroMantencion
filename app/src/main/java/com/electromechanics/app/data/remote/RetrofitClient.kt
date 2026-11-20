package com.electromechanics.app.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun crearRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val usuarios: UsuarioApi =
        crearRetrofit("https://ms-usuarios-m77p.onrender.com/").create(UsuarioApi::class.java)

    val servicios: ServicioApi =
        crearRetrofit("https://ms-servicios.onrender.com/").create(ServicioApi::class.java)

    val asistencia: AsistenciaApi =
        crearRetrofit("https://ms-asistencia-ebur.onrender.com/").create(AsistenciaApi::class.java)
}