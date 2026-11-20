package com.electromechanics.app

import com.electromechanics.app.validation.Validaciones
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    @Test
    fun validarUsuario_vacio_retornaError() {
        val resultado = Validaciones.validarUsuario("")
        assertEquals("Ingresa tu usuario.", resultado)
    }

    @Test
    fun validarUsuario_corto_retornaError() {
        val resultado = Validaciones.validarUsuario("ab")
        assertEquals("El usuario debe tener al menos 3 caracteres.", resultado)
    }

    @Test
    fun validarUsuario_valido_retornaNull() {
        val resultado = Validaciones.validarUsuario("12345678-9")
        assertNull(resultado)
    }

    @Test
    fun validarContrasena_vacia_retornaError() {
        val resultado = Validaciones.validarContrasena("")
        assertEquals("Ingresa tu contraseña.", resultado)
    }

    @Test
    fun validarContrasena_corta_retornaError() {
        val resultado = Validaciones.validarContrasena("123")
        assertEquals("La contraseña debe tener al menos 4 caracteres.", resultado)
    }

    @Test
    fun validarContrasena_valida_retornaNull() {
        val resultado = Validaciones.validarContrasena("Clave1234")
        assertNull(resultado)
    }

    @Test
    fun validarCampoObligatorio_vacio_retornaError() {
        val resultado = Validaciones.validarCampoObligatorio("", "Descripción")
        assertEquals("El campo Descripción es obligatorio.", resultado)
    }

    @Test
    fun validarCampoObligatorio_valido_retornaNull() {
        val resultado = Validaciones.validarCampoObligatorio("Mantenimiento preventivo", "Descripción")
        assertNull(resultado)
    }
}