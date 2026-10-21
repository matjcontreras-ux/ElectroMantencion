
package com.electromechanics.app.validation

object Validaciones {

    fun validarUsuario(usuario: String): String? {
        if (usuario.isBlank()) {
            return "Ingresa tu usuario."
        }

        if (usuario.length < 3) {
            return "El usuario debe tener al menos 3 caracteres."
        }

        return null
    }

    fun validarContrasena(contrasena: String): String? {
        if (contrasena.isBlank()) {
            return "Ingresa tu contraseña."
        }

        if (contrasena.length < 4) {
            return "La contraseña debe tener al menos 4 caracteres."
        }

        return null
    }

    fun validarCampoObligatorio(
        valor: String,
        nombreCampo: String
    ): String? {
        if (valor.isBlank()) {
            return "El campo $nombreCampo es obligatorio."
        }

        return null
    }
}
