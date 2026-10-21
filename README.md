# ElectroMantencion

Aplicación móvil Android desarrollada para apoyar la gestión de servicios de electromantención.

## Objetivo del proyecto

Facilitar la consulta de servicios, el registro de asistencia y el uso de recursos del dispositivo móvil.

## Tecnologías utilizadas

- Kotlin
- Android Studio
- Jetpack Compose y Material 3
- SQLite
- Git y GitHub

## Funcionalidades principales

- Inicio de sesión con validaciones.
- Navegación entre pantallas.
- Consulta y detalle de servicios.
- Registro de entrada y salida con SQLite.
- Persistencia local de registros de asistencia.
- Captura de fotografías mediante la cámara.
- Obtención de ubicación GPS con permisos.
- Pantalla de perfil.
- Validaciones separadas de la interfaz.

## Estructura del proyecto

- `data`: gestión de la base de datos local.
- `ui/screens`: pantallas de la aplicación.
- `ui/theme`: tema visual.
- `validation`: validaciones.
- `viewmodel`: lógica de estado.
- `MainActivity.kt`: actividad principal y navegación.

## Cómo ejecutar la aplicación

1. Abrir el proyecto en Android Studio.
2. Esperar la sincronización de Gradle.
3. Seleccionar un emulador o dispositivo Android.
4. Presionar **Run**.

Las funciones de cámara y ubicación requieren los permisos correspondientes.

## Repositorio

https://github.com/matjcontreras-ux/ElectroMantencion

## Estado del proyecto

Proyecto académico en desarrollo.
