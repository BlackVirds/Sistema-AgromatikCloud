# ☁️ Agromatik Cloud - Backend

Plataforma integral para la gestión agrícola inteligente y monitoreo IoT. Este repositorio contiene la API RESTful desarrollada para administrar huertas, cultivos, usuarios y procesar datos de sensores en tiempo real.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED)
![JWT](https://img.shields.io/badge/Auth-JWT-black)

---
# 📖 Documentación de Uso de API (Manual Técnico)

Esta guía detalla cómo interactuar con la API de Agromatik Cloud.
A diferencia de la documentación automática (Swagger), aquí se especifica exactamente qué campos son necesarios y cuáles se generan automáticamente por el sistema.
## 🗄️ Diseño de Base de Datos

Puedes ver el Diagrama Entidad-Relación (ERD) interactivo en el siguiente enlace:

[Ver Diagrama ER de AgromatikCloud](https://dbdiagram.io/d/Copy-of-AgromatikCloudDB-692936bfd6676488babde004)
## 🔐 1. Autenticación y Usuarios

### A. Registrar Nuevo Usuario
Crea una cuenta nueva. El backend encriptará la contraseña y asignará el rol por defecto.

* **Método:** `POST`
* **Endpoint:** `/api/usuarios`
* **Campos Automáticos (NO ENVIAR):** `id`, `uuid`, `fechaRegistro`, `ultimoLogin`, `activo` (default: true).

**JSON Body:**
```json
{
  "email": "agricultor@ejemplo.com",       // [OBLIGATORIO] Único en el sistema
  "passwordHash": "mi_contraseña_segura",  // [OBLIGATORIO] Se encriptará automáticamente
  "nombre": "Fulano",                        // [OBLIGATORIO]
  "apellido": "Cualquiera",                     // [OPCIONAL]
  "telefono": "555-1234",                  // [OPCIONAL]
  "tipo": "AGRICULTOR",                    // [OBLIGATORIO] AGRICULTOR, EMPRESA, COOPERATIVA
  "suscriptionPlan": "BASICO",             // [OPCIONAL] Default: BASICO (no se puede cambiar por ahora)
  "configuraciones": "{\"tema\":\"dark\"}" // [OPCIONAL] JSON como String
}
```

**cURL:**

```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"email":"test@agro.com", "passwordHash":"123456", "nombre":"Juan", "tipo":"AGRICULTOR"}'
```

-----

### B. Iniciar Sesión (Obtener Token)

Intercambia credenciales por un **JWT (JSON Web Token)**. Necesitarás este token para todas las demás peticiones.

  * **Método:** `POST`
  * **Endpoint:** `/api/auth/login`

**JSON Body:**

```json
{
  "email": "agricultor@ejemplo.com", // [OBLIGATORIO]
  "password": "mi_contraseña_segura" // [OBLIGATORIO]
}
```

**Respuesta Exitosa:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1p..."
}
```

> ⚠️ **IMPORTANTE:** Copia el token recibido. En las siguientes peticiones (excepto la simulación de sensores), debes incluir el encabezado:
> `Authorization: Bearer <TU_TOKEN_AQUI>`

-----

## 🌱 2. Gestión de Huertas

### A. Crear Huerta

Registra una huerta asociada a tu usuario.

  * **Nota de Seguridad:** No necesitas enviar el `usuario_id`. El sistema usa el usuario del Token JWT.

  * **Método:** `POST`

  * **Endpoint:** `/api/huertas`

  * **Campos Automáticos:** `id`, `uuid`, `fechaCreacion`, `activa`, `usuario`.

**JSON Body:**

```json
{
  "nombre": "Huerta Los Abuelos",      // [OBLIGATORIO]
  "ubicacionGeografica": "19.4,-99.1", // [OPCIONAL] Formato estricto "latitud,longitud"
  "tamañoHectareas": 5.5,              // [OPCIONAL] Número decimal
  "tipoSuelo": "FRANCO",               // [OPCIONAL] ARCILLOSO, ARENOSO, LIMOSO, FRANCO, OTROS
  "direccion": "Camino Real s/n",      // [OPCIONAL]
  "municipio": "Tequila",              // [OPCIONAL] 
  "estado": "Jalisco",                 // [OPCIONAL] Estado/Provincia
  "pais": "México",                     // [OPCIONAL] Default: México
  "altitudMetros": 1500.50,            // [OPCIONAL]
  "descripcion": "Cultivo experimental" // [OPCIONAL]
}
```

**cURL:**

```bash
curl -X POST http://localhost:8080/api/huertas \
  -H "Authorization: Bearer <TU_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Huerta 1", "ubicacionGeografica": "20.5,-103.2"}'
```

-----

## 🌽 3. Gestión de Cultivos

### A. Crear Cultivo

Asigna un cultivo a una de tus huertas. Debes especificar el ID de la huerta.

  * **Método:** `POST`
  * **Endpoint:** `/api/cultivos`
  * **Campos Automáticos:** `id`, `uuid`, `estado` (default: ACTIVO).

**JSON Body:**

```json
{
  "huerta": { "id": 1 },            // [OBLIGATORIO] ID de la huerta existente
  "tipoCultivo": "Maíz",            // [OBLIGATORIO]
  "fechaSiembra": "2025-05-15",     // [OBLIGATORIO] Formato YYYY-MM-DD
  "variedad": "Blanco Híbrido",     // [OPCIONAL]
  "metodoRiego": "GOTEO",           // [OPCIONAL] GOTEO, ASPERSION, INUNDACION
  "densidadSiembra": 50000          // [OPCIONAL] Plantas por hectárea
  "fechaCosechaEstimada": "2025-10-15", // [OPCIONAL]
  "notas": "Semilla certificada lote 55" // [OPCIONAL]
}
```

-----

## 📡 4. Sensores y Datos (IoT)

### A. Registrar Sensor (Alta de Dispositivo)

Da de alta un dispositivo físico en la plataforma.

  * **Método:** `POST`
  * **Endpoint:** `/api/sensores`
  * **Campos Automáticos:** `id`, `uuid`, `estado` (ACTIVO).

**JSON Body:**

```json
{
  "huerta": { "id": 1 },             // [OBLIGATORIO] Dónde está instalado
  "nombre": "Sensor-Humedad-01",     // [OBLIGATORIO] Debe ser único en todo el sistema (ID Físico)
  "tipoSensor": "HUMEDAD_SUELO",     // [OBLIGATORIO] HUMEDAD_SUELO, HUMEDAD_AMBIENTAL, PH, LUZ, VIENTO, LLUVIA
  "modelo": "ESP32-S3",              // [OPCIONAL]
  "fabricante": "AgroTech",          // [OPCIONAL]
  "fechaInstalacion": "2025-01-10",  // [OPCIONAL]
  "bateriaNivel": 100,               // [OPCIONAL] % de batería inicial
  "configuraciones": "{\"sleep_mode\": true}", // [OPCIONAL]
  "ubicacionGeografica": "19.4,-99.1"// [OPCIONAL] 
}
```

### B. Enviar Lectura (Simulación IoT)

Este endpoint es usado por los dispositivos físicos.

  * **Autenticación:** Puede usar JWT o la `X-API-KEY` definida en el backend (`AgromatikSecureKeyForSensorsOnly`).

  * **Lógica:** Busca el sensor por su `nombre` (no por UUID) y evalúa alertas automáticamente.

  * **Método:** `POST`

  * **Endpoint:** `/api/lecturas`

**JSON Body:**

```json
{
  "sensorNombre": "Sensor-Humedad-01", // [OBLIGATORIO] Debe coincidir con el nombre registrado
  "valor": 45.5,                       // [OBLIGATORIO] Valor numérico (Double)
  "unidad": "%",                       // [OBLIGATORIO]
  "rawData": "{\"voltaje\": 3.3}"      // [OPCIONAL] Datos extra en String/JSON
}
```

**cURL (Modo Dispositivo Físico):**

```bash
curl -X POST http://localhost:8080/api/lecturas \
  -H "X-API-KEY: AgromatikSecureKeyForSensorsOnly" \
  -H "Content-Type: application/json" \
  -d '{"sensorNombre": "Sensor-Humedad-01", "valor": 12.5, "unidad": "%"}'
```
### B. Enviar Lectura Individual

Envía un solo dato. Ideal para sensores que transmiten en tiempo real.

  * **Método:** `POST`
  * **Endpoint:** `/api/lecturas`
  * **Autenticación:**
      * **Dispositivo Físico:** Header `X-API-KEY: AgromatikSecureKeyForSensorsOnly`
      * **Usuario/App:** Header `Authorization: Bearer <TOKEN_JWT>`

**JSON Body:**

```json
{
  "sensorNombre": "Sensor-Humedad-01", // [OBLIGATORIO] Nombre exacto del sensor
  "valor": 45.5,                       // [OBLIGATORIO] Valor numérico
  "unidad": "%",                       // [OBLIGATORIO]
  "rawData": "{\"v\": 3.3, \"t\": 25}" // [OPCIONAL] Metadata extra
}
```

### C. Enviar Lecturas Masivas (Batch)

Permite enviar múltiples lecturas de golpe (Array JSON). Ideal para gateways que acumulan datos offline y los envían juntos.

  * **Método:** `POST`
  * **Endpoint:** `/api/lecturas/batch`

**JSON Body (Array):**

```json
[
  {
    "sensorNombre": "Sensor-Humedad-01",
    "valor": 44.0,
    "unidad": "%"
  },
  {
    "sensorNombre": "Sensor-Temp-02",
    "valor": 23.5,
    "unidad": "C",
    "rawData": "{\"error\": false}"
  },
  {
    "sensorNombre": "Sensor-Luz-01",
    "valor": 850,
    "unidad": "lux"
  }
]
```

**cURL (Ejemplo Batch):**

```bash
curl -X POST http://localhost:8080/api/lecturas/batch \
  -H "X-API-KEY: AgromatikSecureKeyForSensorsOnly" \
  -H "Content-Type: application/json" \
  -d '[{"sensorNombre": "Sensor-Humedad-01", "valor": 45, "unidad": "%"}, {"sensorNombre": "Sensor-Humedad-01", "valor": 46, "unidad": "%"}]'
```
-----

## 🚜 5. Actividades de Huerta

### A. Registrar Actividad

Agendas una tarea (riego, poda, fertilización).

  * **Método:** `POST`
  * **Endpoint:** `/api/actividades-huerta`

**JSON Body:**

```json
{
  "huerta": { "id": 1 },               // [OBLIGATORIO]
  "tipoActividad": "RIEGO",            // [OBLIGATORIO] RIEGO, PODA, FERTILIZACION, COSECHA
  "cultivo": { "id": 1 },              // [OPCIONAL] Si afecta a un cultivo específico
  "usuarioResponsable": { "id": 2 },   // [OPCIONAL] A quién se asigna
  "descripcion": "Riego matutino",     // [OPCIONAL]
  "fechaProgramada": "2025-10-20T08:00:00", // [OPCIONAL] Cuándo se PLANEÓ
  "fechaActividad": "2025-10-20T08:15:00",  // [OPCIONAL] Cuándo se REALIZÓ (si ya pasó)
  "completada": false,                 // [OPCIONAL] Default: false
  "recursosUsados": "{\"aguaLitros\": 500, \"fertilizanteKg\": 20}", // [OPCIONAL] JSON String
  "notas": "Se observó baja presión en la bomba 2." // [OPCIONAL] Observaciones extra
}
```

-----

## 📊 6. Reportes y Gráficas

Estos endpoints solo aceptan `GET` y devuelven datos calculados para las gráficas del Frontend.

1.  **Historial de Sensor (Gráfico de Línea):**
    `GET /api/reportes/sensor/{uuid_sensor}?inicio=2025-01-01&fin=2025-01-31`

2.  **Eficiencia de Actividades (Gráfico de Barras):**
    `GET /api/reportes/actividades?inicio=2025-01-01&fin=2025-12-31`

3.  **Frecuencia de Alertas (Pie Chart):**
    `GET /api/reportes/frecuencia-alertas?inicio=2025-01-01&fin=2025-12-31`
    *(Opcional: añadir `&huertaId=1` para filtrar)*

4.  **Correlación Sensor vs Actividades (Gráfico Combinado):**
    `GET /api/reportes/correlacion?sensorUuid=...&cultivoId=...&inicio=...&fin=...`

-----

## ⚠️ Notas sobre Errores Comunes

1.  **Error 403 Forbidden:**
      * Verifica que estás enviando el header `Authorization: Bearer ...`.
      * Verifica que el recurso (Huerta/Sensor) realmente pertenece al usuario del token. El sistema bloquea accesos cruzados.
2.  **Error 400 Bad Request:**
      * Revisa el formato de `ubicacionGeografica`. Debe ser una cadena simple `"lat,lon"` (ej. `"19.5,-99.2"`), no un objeto JSON.
      * Revisa formatos de fecha (`YYYY-MM-DD` para LocalDate, `YYYY-MM-DDTHH:mm:ss` para LocalDateTime).
3.  **Sensores:**
      * Si usas el simulador (`SensorDataGeneratorService`), asegúrate de que los sensores tengan el estado `ACTIVO` en la base de datos, o no generará datos.

<!-- end list -->

---

## 🛠️ 7. Operaciones CRUD Adicionales

A continuación se detallan los endpoints para consultar, modificar y eliminar recursos.
**Nota:** Todas las actualizaciones (`PUT`) soportan **modificación parcial**. Solo necesitas enviar los campos que deseas cambiar; los demás conservarán su valor actual.

### 🍏 A. Huertas

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/huertas` | Lista todas tus huertas activas. |
| `GET` | `/api/huertas/{uuid}` | Obtiene el detalle de una huerta específica. |

**Actualizar Huerta:**
* **Método:** `PUT`
* **Endpoint:** `/api/huertas/{uuid}`

```json
{
  "nombre": "Huerta San José (Renombrada)", // [OPCIONAL]
  "activa": true,                             // [OPCIONAL] Reactivar huerta
  "tamañoHectareas": 6.0                      // [OPCIONAL] Ajustar tamaño
}
```

**Eliminar Huerta (Soft Delete):**

  * **Método:** `DELETE`
  * **Endpoint:** `/api/huertas/{uuid}`
  * **Efecto:** Marca la huerta como `activa: false`. No borra los datos históricos.

-----

### 🌽 B. Cultivos

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/cultivos` | Lista todos tus cultivos. |
| `GET` | `/api/cultivos/huerta/{huertaId}` | Filtra cultivos por ID de huerta. |
| `GET` | `/api/cultivos/{uuid}` | Detalle de un cultivo. |

**Actualizar Cultivo:**

  * **Método:** `PUT`
  * **Endpoint:** `/api/cultivos/{uuid}`

```json
{
  "estado": "COSECHADO",             // [OPCIONAL] PLANIFICADO, ACTIVO, COSECHADO, CANCELADO
  "fechaCosechaReal": "2025-10-30",  // [OPCIONAL] Registrar fecha real
  "notas": "Cosecha exitosa con rendimiento alto."
}
```

**Eliminar Cultivo:**

  * **Método:** `DELETE`
  * **Endpoint:** `/api/cultivos/{uuid}`
  * **Efecto:** Cambia el estado a `CANCELADO`.

-----

### 📡 C. Sensores

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/sensores` | Lista todos tus sensores. |
| `GET` | `/api/sensores/huerta/{huertaId}` | Filtra sensores por huerta. |
| `GET` | `/api/sensores/{uuid}` | Detalle técnico del sensor. |

**Actualizar Sensor:**

  * **Método:** `PUT`
  * **Endpoint:** `/api/sensores/{uuid}`

```json
{
  "nombre": "Sensor-Humedad-01-B",   // [OPCIONAL] Renombrar
  "estado": "MANTENIMIENTO",         // [OPCIONAL] ACTIVO, INACTIVO, MANTENIMIENTO, FALLA
  "ultimoMantenimiento": "2025-11-01",
  "huerta": { "id": 2 }              // [OPCIONAL] Mover sensor a otra huerta
}
```

**Desactivar Sensor:**

  * **Método:** `DELETE`
  * **Endpoint:** `/api/sensores/{uuid}`
  * **Efecto:** Cambia el estado a `INACTIVO`. El sensor dejará de procesar lecturas en el simulador.

-----

### 🚜 D. Actividades

A diferencia de los otros recursos, las actividades se identifican por **ID numérico**, no por UUID.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/actividades-huerta` | Lista todas las actividades. |
| `GET` | `/api/actividades-huerta/{id}` | Detalle de una actividad. |
| `GET` | `/api/actividades-huerta/cultivo/{cultivoId}` | Filtra por cultivo. |

**Actualizar Actividad:**

  * **Método:** `PUT`
  * **Endpoint:** `/api/actividades-huerta/{id}`

```json
{
  "completada": true,                        // [OPCIONAL] Marcar como hecha
  "fechaActividad": "2025-10-20T10:00:00",   // [OPCIONAL] Cuándo se hizo realmente
  "recursosUsados": "{\"aguaLitros\": 500}"  // [OPCIONAL] Registrar consumo
}
```

**Eliminar/Finalizar Actividad:**

  * **Método:** `DELETE`
  * **Endpoint:** `/api/actividades-huerta/{id}`
  * **Efecto:** Marca `completada = true` y asigna la fecha actual si no tiene.

-----

## 👤 8. Gestión de Perfil (Usuario)

Endpoints seguros para que el usuario gestione su propia cuenta sin necesidad de enviar su ID. El sistema identifica al usuario automáticamente a través del **Token JWT**.

### A. Obtener mis datos
Recupera toda tu información personal (incluyendo plan, rol, etc.).

* **Método:** `GET`
* **Endpoint:** `/api/usuarios/me`
* **Header:** `Authorization: Bearer <TU_TOKEN>`

### B. Actualizar mi perfil
Permite cambiar datos personales. **Nota:** No se permite cambiar el email, contraseña o rol por esta vía.

* **Método:** `PUT`
* **Endpoint:** `/api/usuarios/me`

**JSON Body:**
```json
{
  "nombre": "Juan Carlos",
  "apellido": "Pérez",
  "telefono": "555-9999",
  "configuraciones": "{\"notificaciones\": false}"
}
```

### C. Eliminar mi cuenta

  * **Método:** `DELETE`
  * **Endpoint:** `/api/usuarios/me`
  * **Efecto:** Desactiva el acceso (`activo = false`).

-----

## 🔔 9. Gestión de Alertas

El sistema genera alertas automáticamente, pero el usuario debe gestionarlas.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/alertas?pagina=0&tamano=10` | Obtiene alertas paginadas. |
| `GET` | `/api/alertas/{id}` | Detalle de una alerta. |

**Marcar Alerta como Leída:**

  * **Método:** `PUT`
  * **Endpoint:** `/api/alertas/{id}/leida`
  * **Cuerpo:** No requiere cuerpo (Empty Body).
## ⚙️ 10. Configuración del Entorno

El proyecto utiliza un archivo `application.properties` (o variables de entorno en Docker). Las principales variables que puedes configurar son:

| Variable | Descripción | Valor por Defecto |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | URL de conexión a BD | `jdbc:postgresql://localhost:5432/agromatik_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de BD | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de BD | `root` |
| `JWT_SECRET_KEY` | Clave para firmar tokens | *(Definida internamente, se recomienda cambiar en prod)* |
| `SENSOR_API_KEY` | API Key para dispositivos físicos | `AgromatikSecureKeyForSensorsOnly` |
## 🚀 11. Despliegue y Ejecución

Esta aplicación está contenerizada para facilitar su despliegue. A continuación se detallan los comandos necesarios.

### Prerrequisitos
* Tener instalado [Docker Desktop](https://www.docker.com/products/docker-desktop/).
* (Opcional) Java 17+ y Maven si deseas ejecutarlo sin contenedores.

### 🐳 Opción 1: Docker Compose (Recomendado)
Este método levanta automáticamente la base de datos PostgreSQL y el Backend de Spring Boot ya configurados para verse entre sí.

1.  **Levantar el entorno (Build & Run):**
    Ejecuta esto en la raíz del proyecto. El flag `-d` corre los procesos en segundo plano.
    ```bash
    docker-compose up -d --build
    ```

2.  **Verificar logs:**
    Para asegurar que Spring Boot inició correctamente:
    ```bash
    docker-compose logs -f app
    ```
    *(Presiona `Ctrl + C` para salir de los logs)*.

3.  **Detener y eliminar contenedores:**
    ```bash
    docker-compose down
    ```
    > **Nota:** Si usas volúmenes en tu compose, los datos de la BD persistirán aunque bajes los contenedores.

---

### 🛠️ Opción 2: Construcción Manual de la Imagen
Si prefieres generar el `.jar` y crear la imagen manualmente paso a paso:

1.  **Generar el ejecutable (JAR):**
    ```bash
    ./mvnw clean package -DskipTests
    ```
    *(En Windows CMD usa `mvnw.cmd`)*

2.  **Construir la imagen Docker:**
    ```bash
    docker build -t agromatik-backend .
    ```

3.  **Ejecutar contenedor individual:**
    ```bash
    docker run -p 8080:8080 --name mi-backend agromatik-backend
    ```
## 📄 Documentación Interactiva (Swagger UI)

Este proyecto cuenta con documentación automática generada con **OpenAPI (Swagger)**.
Una vez que la aplicación esté corriendo (localmente o en Docker), puedes acceder a la interfaz visual para probar los endpoints en tiempo real:

* **Interfaz Visual (UI):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Especificación JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> **Nota:** Swagger es ideal para probar endpoints individuales. Para flujos complejos de negocio (como el ciclo de vida de un cultivo), consulta la guía detallada más arriba en este README.
<!-- end list -->
