package com.electromechanics.app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatosLocal(context: Context) :
    SQLiteOpenHelper(context, "electromantencion.db", null, 2) {

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
        crearTablaUsuarios(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        versionAnterior: Int,
        versionNueva: Int
    ) {
        if (versionAnterior < 2) {
            crearTablaUsuarios(db)
        }
    }

    private fun crearTablaUsuarios(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                rut TEXT UNIQUE NOT NULL,
                nombre TEXT NOT NULL,
                clave TEXT NOT NULL,
                rol TEXT NOT NULL
            )
            """.trimIndent()
        )
        // Usuario local por defecto para pruebas offline
        db.execSQL(
            """
            INSERT OR IGNORE INTO usuarios (rut, nombre, clave, rol)
            VALUES ('12345678-9', 'Juan Perez', 'Clave1234', 'TECNICO')
            """.trimIndent()
        )
    }

    fun registrarUsuarioLocal(
        rut: String,
        nombre: String,
        clave: String,
        rol: String = "TECNICO"
    ): Boolean {
        val rutLimpio = rut.trim()

        // 1. Verificar que el usuario NO esté creado previamente
        readableDatabase.rawQuery(
            "SELECT id FROM usuarios WHERE rut = ? LIMIT 1",
            arrayOf(rutLimpio)
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                return false // Ya existe, no se crea
            }
        }

        // 2. Si no existe, lo inserta
        val valores = ContentValues().apply {
            put("rut", rutLimpio)
            put("nombre", nombre.trim())
            put("clave", clave)
            put("rol", rol)
        }
        return writableDatabase.insert(
            "usuarios",
            null,
            valores
        ) != -1L
    }

    fun validarLoginLocal(rut: String, clave: String): Pair<String, String>? {
        readableDatabase.rawQuery(
            """
            SELECT nombre, rol
            FROM usuarios
            WHERE rut = ? AND clave = ?
            LIMIT 1
            """.trimIndent(),
            arrayOf(rut.trim(), clave)
        ).use { cursor ->
            return if (cursor.moveToFirst()) {
                Pair(cursor.getString(0), cursor.getString(1))
            } else {
                null
            }
        }
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