# Arquitectura ElectroMantencion

## 1. Diagrama de Arquitectura del Sistema
```mermaid
graph TD
  A[App Android - Kotlin / Jetpack Compose] -->|MVVM + Coroutines| B[SQLite Local - Login/Cache]
  A -->|Retrofit REST API| C[ms-usuarios - Spring Boot / Docker]
  A -->|Retrofit REST API| D[ms-servicios - Spring Boot / Docker]
  A -->|Retrofit REST API| E[ms-asistencia - Spring Boot / Docker]
  C & D & E -->|Spring Data JPA| F[(Base de Datos PostgreSQL en Render)]
  A -->|Recursos Nativos| G[Camara y GPS del Dispositivo]
