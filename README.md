# Agromatik Cloud - API Backend

Descripción
----------
Agromatik Cloud es el backend para gestionar usuarios, huertas, sensores, lecturas, cultivos, alertas y actividades agrícolas. Este repositorio contiene las rutas principales de la API y la documentación básica del modelo de datos.


Tabla de contenidos
------------------
- [Diagrama de Base de Datos](#diagrama-de-base-de-datos)
- [Rutas de la API](#rutas-de-la-api)
- [Autenticación](#autenticación)
- [Ejemplos (cURL)](#ejemplos-curl)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Variables de entorno](#variables-de-entorno)
- [Contribuir](#contribuir)
- [Licencia](#licencia)

Diagrama de Base de Datos
-------------------------
El diagrama muestra la estructura del modelo de datos (entidades principales, relaciones y claves foráneas).

📊 Ver Diagrama Entidad–Relación (DER): [https://dbdiagram.io/d/AgromatikCloudDB-68da3ad3d2b621e42258e007](https://dbdiagram.io/d/AgromatikCloudDB-69045b696735e111709dadeb)

Nota: el diagrama fue generado con dbdiagram.io.

🔐 Resumen de Rutas API (con Seguridad JWT)
🔑 Módulo de Autenticación y Registro (Rutas Públicas)

Estas rutas están configuradas como permitAll() y son las únicas accesibles sin un Token JWT.

Método	Ruta	Acceso	Propósito
POST	/api/auth/login	Público	Inicia sesión (email/password → Token JWT).
POST	/api/usuarios	Público	Registra un nuevo usuario en el sistema.
🔒 Módulos Protegidos (Rutas Privadas — Token Requerido)

Todas las siguientes rutas están configuradas como authenticated() y requieren un Token JWT válido enviado en el encabezado:

Authorization: Bearer <token>

👤 Usuarios (Gestión)

💡 Estas rutas se recomiendan solo para ROLE_ADMIN.

Método	Ruta	Propósito
GET	/api/usuarios	Obtiene la lista de usuarios.
GET	/api/usuarios/{uuid}	Obtiene un usuario específico.
PUT	/api/usuarios/{uuid}	Actualiza datos de usuario.
DELETE	/api/usuarios/{uuid}	Desactiva un usuario (Soft Delete).
🌾 Huertas
Método	Ruta	Propósito
GET	/api/huertas	Obtiene las huertas (filtradas por usuario).
POST	/api/huertas	Crea una nueva huerta.
GET	/api/huertas/{uuid}	Obtiene una huerta específica.
PUT	/api/huertas/{uuid}	Actualiza una huerta.
DELETE	/api/huertas/{uuid}	Desactiva una huerta (Soft Delete).
GET	/api/huertas/usuarios/{usuarioId}	Obtiene huertas de un usuario (Admin).
🌱 Cultivos
Método	Ruta	Propósito
GET	/api/cultivos	Obtiene todos los cultivos.
POST	/api/cultivos	Crea un nuevo cultivo.
GET	/api/cultivos/huerta/{huertaId}	Obtiene cultivos de una huerta.
GET	/api/cultivos/{uuid}	Obtiene un cultivo específico.
PUT	/api/cultivos/{uuid}	Actualiza un cultivo.
DELETE	/api/cultivos/{uuid}	Cancela un cultivo (Soft Delete).
⚙️ Sensores
Método	Ruta	Propósito
GET	/api/sensores	Obtiene todos los sensores.
POST	/api/sensores	Crea un nuevo sensor.
GET	/api/sensores/{uuid}	Obtiene un sensor específico.
GET	/api/sensores/huerta/{huertaId}	Obtiene sensores de una huerta.
PUT	/api/sensores/{uuid}	Actualiza un sensor.
DELETE	/api/sensores/{uuid}	Desactiva un sensor (Soft Delete).
📈 Lecturas (Motor de Datos)
Método	Ruta	Propósito
POST	/api/lecturas	Recibe lecturas desde sensores o simulador.
GET	/api/lecturas/sensor/{uuid}/ultima	Obtiene la lectura más reciente.
GET	/api/lecturas/sensor/{uuid}	Obtiene el historial de lecturas.
GET	/api/lecturas/sensor/{uuid}/rango	Obtiene lecturas por rango de fechas.
🚨 Alertas (Motor de Inteligencia)
Método	Ruta	Propósito
GET	/api/alertas	Obtiene lista de alertas generadas.
GET	/api/alertas/{id}	Obtiene una alerta específica.
PUT	/api/alertas/{id}/leida	Marca una alerta como leída.
🧑‍🌾 Actividades Huerta
Método	Ruta	Propósito
GET	/api/actividades-huerta	Obtiene todas las actividades.
POST	/api/actividades-huerta	Crea una nueva actividad.
GET	/api/actividades-huerta/{id}	Obtiene una actividad específica.
GET	/api/actividades-huerta/huerta/{huertaId}	Obtiene actividades de una huerta.
GET	/api/actividades-huerta/cultivo/{cultivoId}	Obtiene actividades de un cultivo.
PUT	/api/actividades-huerta/{id}	Actualiza una actividad.
DELETE	/api/actividades-huerta/{id}	Completa o elimina una actividad (Soft Delete).
🧾 Autenticación

La API utiliza JWT (JSON Web Token) para la autenticación.
Cada usuario obtiene un token al iniciar sesión y debe enviarlo en cada solicitud:

Authorization: Bearer <tu_token_jwt>


Tokens firmados con:

Algoritmo: HS256

Clave secreta configurada en application.properties

jwt.secret=tu_clave_secreta_segura
jwt.expiration=86400000

Instalación y ejecución
-----------------------


Variables de entorno (ejemplo)
------------------------------


Contribuir
----------

Licencia
--------


Notas y recomendaciones
-----------------------

