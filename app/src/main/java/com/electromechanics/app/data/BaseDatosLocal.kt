
package com.electromechanics.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatosLocal(context: Context) :
    SQLiteOpenHelper(context, "electromantencion.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE registros_jornada (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT NOT NULL,
                fecha_hora TEXT NOT NULL,
                latitud REAL,
                longitud REAL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        versionAnterior: Int,
        versionNueva: Int
    ) {
        // Conservamos los registros existentes.
    }

    fun guardarRegistro(
        tipo: String,
        fechaHora: String,
        latitud: Double? = null,
        longitud: Double? = null
    ): Boolean {
        val valores = ContentValues().apply {
            put("tipo", tipo)
            put("fecha_hora", fechaHora)

            if (latitud != null) put("latitud", latitud)
            if (longitud != null) put("longitud", longitud)
        }

        return writableDatabase.insert(
            "registros_jornada",
            null,
            valores
        ) != -1L
    }

    fun obtenerRegistros(): List<String> {
        val registros = mutableListOf<String>()

        readableDatabase.rawQuery(
            """
            SELECT tipo, fecha_hora, latitud, longitud
            FROM registros_jornada
            ORDER BY id DESC
            """.trimIndent(),
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val tipo = cursor.getString(0)
                val fechaHora = cursor.getString(1)
                val latitud = if (cursor.isNull(2)) {
                    "No disponible"
                } else {
                    cursor.getDouble(2).toString()
                }
                val longitud = if (cursor.isNull(3)) {
                    "No disponible"
                } else {
                    cursor.getDouble(3).toString()
                }

                registros.add(
                    "$tipo | $fechaHora | GPS: $latitud, $longitud"
                )
            }
        }

        return registros
    }

    fun obtenerUltimoRegistro(tipo: String): String? {
        readableDatabase.rawQuery(
            """
            SELECT fecha_hora
            FROM registros_jornada
            WHERE tipo = ?
            ORDER BY id DESC
            LIMIT 1
            """.trimIndent(),
            arrayOf(tipo)
        ).use { cursor ->
            return if (cursor.moveToFirst()) {
                cursor.getString(0)
            } else {
                null
            }
        }
    }
}
