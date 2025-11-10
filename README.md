# Agromatik Cloud - API Backend

## Descripción

Este es el backend de **AGROMATIK_CLOUD** para gestionar usuarios, huertas, sensores, lecturas, cultivos, alertas y actividades agrícolas para analizar los datos de cada usuario.  
Este repositorio contiene las rutas principales de la API y la documentación básica del modelo de datos.

---

## Tabla de contenidos

- [Diagrama de Base de Datos](#diagrama-de-base-de-datos)  
- [Autenticación](#autenticación)  
- [Rutas de la API](#rutas-de-la-api)  
- [Instalación y ejecución](#instalación-y-ejecución)  
- [Variables de entorno](#variables-de-entorno)  
- [Ejemplos (cURL)](#ejemplos-curl)  
- [Contribuir](#contribuir)  
- [Licencia](#licencia)

---

## Diagrama de Base de Datos

El diagrama muestra la estructura del modelo de datos (entidades principales, relaciones y claves foráneas).

📊 Ver Diagrama Entidad–Relación (DER): https://dbdiagram.io/d/AgromatikCloudDB-68da3ad3d2b621e42258e007

Nota: el diagrama fue generado con dbdiagram.io.

---

## Autenticación

Esta API está protegida usando JSON Web Tokens (JWT). Todas las peticiones a endpoints protegidos deben incluir un token válido en el encabezado de autorización.

El flujo de autenticación es el siguiente:

- Registro (Público): Un nuevo usuario se registra enviando sus datos al endpoint `POST /api/usuarios`. El UsuarioService hashea la contraseña (BCrypt) y la guarda.

- Login (Público): El usuario envía su email y contraseña (en texto plano) al endpoint `POST /api/auth/login`.

- Respuesta: El servidor (AuthController) valida las credenciales. Si son correctas, el JwtService genera un token firmado y lo devuelve al cliente.

- Acceso (Privado): El cliente (frontend) debe guardar este token (ej. en localStorage) y enviarlo en todas las peticiones futuras a rutas protegidas usando el encabezado:

  Authorization: Bearer <token_jwt>

El JwtAuthenticationFilter interceptará y validará este token en cada petición.

---

## Rutas de la API

A continuación un resumen de los endpoints disponibles.

### 🔑 Módulo de Autenticación y Registro (Rutas Públicas)

Estas rutas son accesibles sin un Token JWT.

| Método | Ruta | Propósito |
|--------|------|-----------|
| POST | /api/auth/login | Inicia sesión (intercambia email/password por un Token JWT). |
| POST | /api/usuarios | Registra un nuevo usuario en el sistema. |

---

### 🔒 Módulos Protegidos (Rutas Privadas)

Todas las siguientes rutas requieren un encabezado `Authorization: Bearer <token>`.

Usuarios (Gestión)  
Nota: Estas rutas deben estar restringidas solo a ROLE_ADMIN en producción.

- `GET /api/usuarios`  
- `GET /api/usuarios/{uuid}`  
- `PUT /api/usuarios/{uuid}`  
- `DELETE /api/usuarios/{uuid}`

Huertas

- `GET /api/huertas` (Filtra por usuario autenticado)  
- `POST /api/huertas`  
- `GET /api/huertas/{uuid}`  
- `PUT /api/huertas/{uuid}`  
- `DELETE /api/huertas/{uuid}`  
- `GET /api/huertas/usuarios/{usuarioId}` (Solo para Admins)

Cultivos

- `GET /api/cultivos`  
- `POST /api/cultivos`  
- `GET /api/cultivos/huerta/{huertaId}`  
- `GET /api/cultivos/{uuid}`  
- `PUT /api/cultivos/{uuid}`  
- `DELETE /api/cultivos/{uuid}`

Sensores

- `GET /api/sensores`  
- `POST /api/sensores`  
- `GET /api/sensores/{uuid}`  
- `GET /api/sensores/huerta/{huertaId}`  
- `PUT /api/sensores/{uuid}`  
- `DELETE /api/sensores/{uuid}`

Lecturas (Motor de Datos)

- `POST /api/lecturas`  
- `GET /api/lecturas/sensor/{uuid}/ultima`  
- `GET /api/lecturas/sensor/{uuid}`  
- `GET /api/lecturas/sensor/{uuid}/rango?inicio={fechaInicio}&fin={fechaFin}`

Alertas (Motor de Inteligencia)

- `GET /api/alertas?pagina={pagina}&tamano={tamano}`  
- `GET /api/alertas/{id}`  
- `PUT /api/alertas/{id}/leida`

Actividades Huerta

- `GET /api/actividades-huerta`  
- `POST /api/actividades-huerta`  
- `GET /api/actividades-huerta/{id}`  
- `GET /api/actividades-huerta/huerta/{huertaId}`  
- `GET /api/actividades-huerta/cultivo/{cultivoId}`  
- `PUT /api/actividades-huerta/{id}`  
- `DELETE /api/actividades-huerta/{id}`

---

## Instalación y ejecución

Este proyecto utiliza Java 17 y Maven.

Clonar el repositorio:


Configurar variables de entorno:  
Crea o modifica el archivo `application.yml` (o `.properties`) en `src/main/resources` para que coincida con tu configuración de base de datos y la clave secreta de JWT.

Compilar y construir:

```bash
mvn clean install
```

Ejecutar la aplicación:

```bash
mvn spring-boot:run
```

La API estará disponible en http://localhost:8080.

---

## Variables de entorno (ejemplo)

Configuración requerida en `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/agromatik_db
    username: tu_usuario_db
    password: tu_password_db
  jpa:
    database-platform: org.hibernate.spatial.dialect.mysql.MySQL8SpatialDialect
    # ...

# Clave secreta para firmar los JWT
app:
  jwt:
    secret: "EstaEsMiClaveSecretaSuperLargaParaAgromatikCloud2025"
    expiration-ms: 86400000 # 24 horas
```

---

## Ejemplos (cURL)

1. Registrar un nuevo usuario

```bash
curl -X POST http://localhost:8080/api/usuarios \
-H "Content-Type: application/json" \
-d '{
    "email": "agricultor@prueba.com",
    "passwordHash": "password123",
    "nombre": "Agricultor",
    "apellido": "Prueba",
    "tipo": "AGRICULTOR",
    "suscriptionPlan": "BASICO"
}'
```

2. Iniciar Sesión (Obtener Token)

```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{
    "email": "agricultor@prueba.com",
    "password": "password123"
}'
```

Respuesta: `{"token":"eyJhbGciOiJIUzI1NiJ9..."}`

3. Acceder a una Ruta Protegida (Ej. Huertas)

Copia el token de la respuesta anterior.

```bash
curl -X GET http://localhost:8080/api/huertas \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## Contribuir

Por favor, sigue las convenciones de código y envía Pull Requests a la rama develop.

---

## Licencia

MIT
