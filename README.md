# Actividad: Desarrollo de una API REST con Spring Boot

Este repositorio contiene el proyecto desarrollado para la actividad de creación de una API REST utilizando Spring Boot. Los controladores devuelven información de prueba estructurada en JSON simulando un sistema de gestión vecinal.

##  Requisitos Previos
- Java Development Kit (JDK) 17 o superior.
- Un navegador web.
- Postman (para realizar pruebas de las peticiones).

##  Configuración y Ejecución del Proyecto
El proyecto utiliza Gradle como gestor de dependencias. Para ejecutarlo localmente, sigue estos pasos:

1. Clona este repositorio en tu computadora.
2. Abre una terminal en la carpeta raíz del proyecto (donde se encuentra el archivo `build.gradle`).
3. Ejecuta el servidor utilizando el wrapper incluido con el siguiente comando:
   - **En Windows:** `.\gradlew bootRun` (o `cmd /c gradlew.bat bootRun`)
   - **En Mac/Linux:** `./gradlew bootRun`
4. Espera a que la consola indique que el servidor Tomcat inició en el puerto 8080.

## Endpoints Desarrollados
Se crearon controladores independientes, cada uno respondiendo exclusivamente desde el método del controller a peticiones `GET` en las siguientes rutas:

- `http://localhost:8080/api/colonias` : Retorna el catálogo de zonas.
- `http://localhost:8080/api/reportes` : Retorna reportes ciudadanos.
- `http://localhost:8080/api/usuarios` : Retorna los datos de los usuarios registrados.
