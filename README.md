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

📊 Ver Diagrama Entidad–Relación (DER): https://dbdiagram.io/d/AgromatikCloudDB-68da3ad3d2b621e42258e007

Nota: el diagrama fue generado con dbdiagram.io.

Rutas de la API
----------------
A continuación un resumen de los endpoints disponibles (organizados por módulo). Los parámetros entre llaves {} indican variables de ruta o query.

Usuarios
- GET    /api/usuarios
- GET    /api/usuarios/{uuid}
- POST   /api/usuarios
- PUT    /api/usuarios/{uuid}
- DELETE /api/usuarios/{uuid}

Sensores
- GET    /api/sensores
- GET    /api/sensores/{uuid}
- GET    /api/sensores/huerta/{huertaId}
- POST   /api/sensores
- PUT    /api/sensores/{uuid}
- DELETE /api/sensores/{uuid}

Lecturas
- POST   /api/lecturas
- GET    /api/lecturas/sensor/{uuid}/ultima
- GET    /api/lecturas/sensor/{uuid}
- GET    /api/lecturas/sensor/{uuid}/rango?inicio={fechaInicio}&fin={fechaFin}

Huertas
- GET    /api/huertas
- GET    /api/huertas/{uuid}
- GET    /api/huertas/usuarios/{usuarioId}
- POST   /api/huertas
- PUT    /api/huertas/{uuid}
- DELETE /api/huertas/{uuid}

Cultivos
- GET    /api/cultivos
- GET    /api/cultivos/huerta/{huertaId}
- GET    /api/cultivos/{uuid}
- POST   /api/cultivos
- PUT    /api/cultivos/{uuid}
- DELETE /api/cultivos/{uuid}

Alertas
- GET    /api/alertas?pagina={pagina}&tamano={tamano}
- GET    /api/alertas/{id}
- PUT    /api/alertas/{id}/leida

Actividades Huerta
- GET    /api/actividades-huerta
- GET    /api/actividades-huerta/{id}
- GET    /api/actividades-huerta/huerta/{huertaId}
- GET    /api/actividades-huerta/cultivo/{cultivoId}
- POST   /api/actividades-huerta
- PUT    /api/actividades-huerta/{id}
- DELETE /api/actividades-huerta/{id}

Autenticación
-------------



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

