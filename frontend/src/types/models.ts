// frontend/src/types/models.ts

// =======================
// ENUMS (Tipos de Unión para los selectores y la validación)
// =======================
export type TipoUsuario = "AGRICULTOR" | "COOPERATIVA" | "EMPRESA" | "ADMIN";
export type PlanSuscripcion = "BASICO" | "AVANZADO" | "EMPRESA";
export type TipoSuelo = "ARCILLOSO" | "ARENOSO" | "LIMOSO" | "FRANCO" | "OTROS";
// ENUMS DE CULTIVO
// =======================
export type EstadoCultivo =
  | "PLANIFICADO"
  | "ACTIVO"
  | "COSECHADO"
  | "CANCELADO";
export type MetodoRiego = "GOTEO" | "ASPERSION" | "INUNDACION" | "OTROS";

// ENUMS DE SENSOR
// =======================
export type TipoSensor =
  | "TEMPERATURA"
  | "HUMEDAD_SUELO"
  | "HUMEDAD_AMBIENTAL"
  | "PH"
  | "LUZ"
  | "VIENTO"
  | "LLUVIA";
export type EstadoSensor = "ACTIVO" | "INACTIVO" | "MANTENIMIENTO" | "FALLA";
// ENUMS DE ACTIVIDAD HUERTA
// =======================
export type TipoActividad =
  | "RIEGO"
  | "FERTILIZACION"
  | "PODA"
  | "APLICACION_PESTICIDA"
  | "COSECHA"
  | "MUESTREO";
// ENUMS DE LECTURA
// =======================
export type CalidadDato = "BUENO" | "REGULAR" | "POBRE";
// ENUMS DE ALERTA
export type SeveridadAlerta = "BAJA" | "MEDIA" | "ALTA" | "CRITICA";

// ===============================================================
// INTERFACES
// ===============================================================

/**
 * Interfaz que define la estructura del objeto Usuario que el backend retorna (GET/POST Response).
 * Coincide con la Entidad Usuario de Java, excluyendo el password hash.
 */
export interface Usuario {
  id: number;
  uuid: string;
  email: string;
  nombre: string;
  apellido?: string;
  telefono?: string;
  tipo: TipoUsuario;
  suscriptionPlan: PlanSuscripcion;
  activo: boolean;
  fechaRegistro: string;
  ultimoLogin?: string; // 👈 AÑADIDO: Para reflejar la columna 'ultimo_login'
  configuraciones?: string; // 👈 AÑADIDO: Para reflejar la columna 'configuraciones' (JSON)
}

/**
 * Interfaz que define la estructura de datos que el frontend envía al backend para la creación (POST Request).
 * Incluye el passwordHash que se requiere para el registro, pero que la respuesta (Usuario) no incluye.
 */
export interface UsuarioCreateData {
  email: string;
  passwordHash: string; // Campo de entrada OBLIGATORIO para el registro
  nombre: string;
  tipo: TipoUsuario;
  // Campos opcionales
  apellido?: string;
  telefono?: string;
  suscriptionPlan?: PlanSuscripcion; // Opcional, ya que el backend tiene un default
}
export interface UsuarioReferencia {
  id: number;
}
// =======================
// HUERTA: DTO de Creación/Actualización
// =======================

/**
 * Interfaz que define la estructura de datos que el frontend envía para crear/actualizar una Huerta.
 * Se utiliza 'ubicacionGeografica' como STRING para coincidir con la solución del backend.
 */
export interface HuertaCreateUpdateData {
  // Obligatorios (por la validación de tu servicio)
  nombre: string;
  usuario: UsuarioReferencia; // FK, se envía solo el ID

  // Campo de Ubicación (Validado en el backend con el formato 'latitud,longitud')
  ubicacionGeografica?: string;

  // Campos de Valor y Opcionales
  descripcion?: string;
  direccion?: string;
  municipio?: string;
  estado?: string;
  pais?: string;
  tamañoHectareas?: number; // BigDecimal -> number
  tipoSuelo?: TipoSuelo;
  altitudMetros?: number; // BigDecimal -> number
  activa?: boolean;
}

// =======================
// HUERTA: Interfaz de Salida (Respuesta GET/POST)
// =======================

/**
 * Interfaz que define la estructura del objeto Huerta que el backend retorna.
 */
export interface Huerta {
  id: number;
  uuid: string;
  nombre: string;
  descripcion: string;

  // El backend retorna la ubicación como STRING simple (ej: "32.12,-100.56")
  ubicacionGeografica: string | null;

  direccion: string;
  municipio: string;
  estado: string;
  pais: string;
  tamañoHectareas: number;
  tipoSuelo: TipoSuelo;
  altitudMetros: number;

  // Relación anidada con el objeto Usuario
  usuario: Usuario;

  fechaCreacion: string; // LocalDateTime
  activa: boolean;
}

// =======================
// CULTIVO: DTO de Creación/Actualización
// =======================

export interface CultivoCreateUpdateData {
  // Obligatorios
  tipoCultivo: string;
  fechaSiembra: string; // LocalDate (string en TS)
  huerta: UsuarioReferencia; // FK: solo ID de la huerta

  // Opcionales
  variedad?: string;
  fechaCosechaEstimada?: string;
  fechaCosechaReal?: string;
  densidadSiembra?: number; // BigDecimal -> number
  metodoRiego?: MetodoRiego;
  notas?: string;
  estado?: EstadoCultivo; // Usar en PUT si se cancela o cosecha
}

// =======================
// CULTIVO: Interfaz de Salida
// =======================

export interface Cultivo {
  id: number;
  uuid: string;
  tipoCultivo: string;
  variedad: string;
  fechaSiembra: string;
  fechaCosechaEstimada: string | null;
  fechaCosechaReal: string | null;
  estado: EstadoCultivo;
  densidadSiembra: number;
  metodoRiego: MetodoRiego;
  notas: string;
  huerta: Huerta; // Objeto Huerta anidado
}

// SENSOR: DTO de Creación/Actualización (INPUT)
// =======================

export interface SensorCreateUpdateData {
  // Obligatorios
  nombre: string;
  tipoSensor: TipoSensor;
  huerta: UsuarioReferencia; // FK: solo ID de la huerta

  // Opcionales (Campos de valor)
  modelo?: string;
  fabricante?: string;

  //  Ubicación (String Lat/Lng): El frontend enviará el formato que ValidacionesUtil espera
  ubicacionGeografica?: string;

  fechaInstalacion?: string; // LocalDate (YYYY-MM-DD)
  ultimoMantenimiento?: string; // LocalDate
  estado?: EstadoSensor;
  bateriaNivel?: number;
  configuraciones?: string; // JSON String
}

// =======================
// SENSOR: Interfaz de Salida (GET Response)
// =======================

export interface Sensor {
  id: number;
  uuid: string;
  nombre: string;
  tipoSensor: TipoSensor;
  modelo: string;
  fabricante: string;
  ubicacionGeografica: string | null;
  fechaInstalacion: string | null;
  ultimoMantenimiento: string | null;
  estado: EstadoSensor;
  bateriaNivel: number;
  configuraciones: string | null;

  // Relación anidada (Ignora las relaciones inversas por JsonIgnoreProperties)
  huerta: Huerta;
}

// ACTIVIDAD HUERTA: DTO de Creación/Actualización (INPUT)
// =======================

/**
 * Los campos que son FK opcionales (Cultivo y Usuario) usan UsuarioReferencia para enviar solo el ID.
 * Nota: El backend de Java está esperando un objeto Cultivo o Usuario con solo el ID dentro, por lo que reusamos UsuarioReferencia.
 */
export interface ActividadCreateUpdateData {
  // Obligatorios
  huerta: UsuarioReferencia; // FK: Solo el ID de la huerta
  tipoActividad: TipoActividad; // Tipo de tarea // Opcionales de relación

  cultivo?: UsuarioReferencia; // FK opcional
  usuarioResponsable?: UsuarioReferencia; // FK opcional (para asignar la tarea) // Campos de valor y control

  descripcion?: string;
  fechaActividad?: string; // LocalDateTime (string en TS)
  fechaProgramada?: string; // LocalDateTime
  completada?: boolean; // Para marcar como completada en un PUT
  recursosUsados?: string; // JSON String
  notas?: string;
}

// =======================
// ACTIVIDAD HUERTA: Interfaz de Salida (GET Response)
// =======================

export interface ActividadHuerta {
  id: number;
  huerta: Huerta; // Objeto Huerta anidado
  cultivo: Cultivo | null; // Objeto Cultivo anidado (puede ser null)
  tipoActividad: TipoActividad;
  descripcion: string;
  fechaActividad: string | null;
  fechaProgramada: string | null;
  completada: boolean;
  recursosUsados: string | null;
  notas: string | null;
  usuarioResponsable: Usuario | null; // Objeto Usuario anidado (puede ser null)
}

// LECTURA SENSOR: DTO de Entrada (INPUT)
// Coincide con SensorDataDTO.java
// =======================

export interface SensorDataDTO {
  // Clave para buscar la FK
  sensorUuid: string;

  // Valores de la lectura
  valor: number; // Mapeado a Double en Java
  unidad: string;
  rawData?: string; // JSON String
}

// =======================
// LECTURA SENSOR: Interfaz de Salida
// =======================

export interface LecturaSensor {
  id: number;

  // Relaciones (deben ser objetos anidados para el GET)
  sensor: Sensor;

  valor: number; // BigDecimal se mapea a number
  unidad: string;
  timestamp: string; // LocalDateTime
  calidadDato: CalidadDato;
  rawData: string | null;
}

// ALERTA: Interfaz de Salida (GET Response)
// =======================

export interface Alerta {
  id: number;
  uuid: string;

  // Relaciones anidadas (Ignoradas en la recursión)
  usuario: Usuario;
  huerta: Huerta;
  sensor: Sensor;

  // Campos de Detección
  parametro: string;
  valorActual: number;
  umbralMin: number | null;
  umbralMax: number | null;

  // Campos Comunes
  severidad: SeveridadAlerta;
  titulo: string;
  descripcion: string;
  leida: boolean; // El frontend usará esto para el filtro
  fechaCreacion: string; // LocalDateTime
}

// NOTA: Para el POST/PUT de Alertas, solo usas el ID, por lo que no requerimos un DTO de entrada complejo
