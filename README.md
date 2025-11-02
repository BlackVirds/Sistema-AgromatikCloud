### Diagrama de Base de Datos

Puedes consultar el Diagrama Entidad-Relación (DER) del proyecto en el siguiente enlace:

[Ver Diagrama de la Base de Datos](https://dbdiagram.io/d/AgromatikCloudDB-68da3ad3d2b621e42258e007)

# 🌾 API – Sistema Agromatik Cloud

## 📡 Rutas del Backend

| **Módulo** | **Método HTTP** | **Ruta** | **Descripción / Parámetros** |
|-------------|------------------|-----------|-------------------------------|
| **Usuarios** | GET | `/api/usuarios` | Obtener todos los usuarios |
|  | GET | `/api/usuarios/{uuid}` | Obtener usuario por UUID |
|  | POST | `/api/usuarios` | Crear un nuevo usuario |
|  | PUT | `/api/usuarios/{uuid}` | Actualizar usuario por UUID |
|  | DELETE | `/api/usuarios/{uuid}` | Eliminar usuario por UUID |
| **Sensores** | GET | `/api/sensores` | Obtener todos los sensores |
|  | GET | `/api/sensores/{uuid}` | Obtener sensor por UUID |
|  | GET | `/api/sensores/huerta/{huertaId}` | Obtener sensores de una huerta |
|  | POST | `/api/sensores` | Crear un sensor |
|  | PUT | `/api/sensores/{uuid}` | Actualizar sensor por UUID |
|  | DELETE | `/api/sensores/{uuid}` | Eliminar sensor por UUID |
| **Lecturas** | POST | `/api/lecturas` | Registrar nueva lectura de sensor |
|  | GET | `/api/lecturas/sensor/{uuid}/ultima` | Obtener última lectura por sensor |
|  | GET | `/api/lecturas/sensor/{uuid}` | Obtener historial de lecturas |
|  | GET | `/api/lecturas/sensor/{uuid}/rango?inicio={fechaInicio}&fin={fechaFin}` | Lecturas por rango de fechas |
| **Huertas** | GET | `/api/huertas` | Obtener todas las huertas |
|  | GET | `/api/huertas/{uuid}` | Obtener huerta por UUID |
|  | GET | `/api/huertas/usuarios/{usuarioId}` | Obtener huertas por usuario |
|  | POST | `/api/huertas` | Crear una huerta |
|  | PUT | `/api/huertas/{uuid}` | Actualizar huerta por UUID |
|  | DELETE | `/api/huertas/{uuid}` | Eliminar huerta por UUID |
| **Cultivos** | GET | `/api/cultivos` | Obtener todos los cultivos |
|  | GET | `/api/cultivos/huerta/{huertaId}` | Obtener cultivos por huerta |
|  | GET | `/api/cultivos/{uuid}` | Obtener cultivo por UUID |
|  | POST | `/api/cultivos` | Crear cultivo |
|  | PUT | `/api/cultivos/{uuid}` | Actualizar cultivo por UUID |
|  | DELETE | `/api/cultivos/{uuid}` | Eliminar cultivo por UUID |
| **Alertas** | GET | `/api/alertas?pagina={pagina}&tamano={tamano}` | Obtener alertas paginadas |
|  | GET | `/api/alertas/{id}` | Obtener alerta por ID |
|  | PUT | `/api/alertas/{id}/leida` | Marcar alerta como leída |
| **Actividades Huerta** | GET | `/api/actividades-huerta` | Obtener todas las actividades |
|  | GET | `/api/actividades-huerta/{id}` | Obtener actividad por ID |
|  | GET | `/api/actividades-huerta/huerta/{huertaId}` | Actividades por huerta |
|  | GET | `/api/actividades-huerta/cultivo/{cultivoId}` | Actividades por cultivo |
|  | POST | `/api/actividades-huerta` | Crear nueva actividad |
|  | PUT | `/api/actividades-huerta/{id}` | Actualizar actividad por ID |
|  | DELETE | `/api/actividades-huerta/{id}` | Eliminar actividad por ID |

