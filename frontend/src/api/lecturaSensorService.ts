// frontend/src/api/lecturaSensorService.ts

import axios from "axios";
import type { LecturaSensor, SensorDataDTO } from "../types/models";

const API_BASE_URL = "http://localhost:8080/api/lecturas";

/**
 * Envía una lectura individual al backend para procesamiento y evaluación de alertas. (POST)
 */
export async function postLecturaSensor(
  data: SensorDataDTO
): Promise<LecturaSensor> {
  try {
    // Se espera un 202 Accepted
    const response = await axios.post<LecturaSensor>(API_BASE_URL, data);
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.data) {
      throw new Error(
        error.response.data.message || "Error al enviar la lectura."
      );
    }
    throw new Error("Fallo de conexión al backend.");
  }
}

/**
 * Obtiene la última lectura para mostrar el estado actual del sensor. (GET /sensor/{uuid}/ultima)
 */
export async function getUltimaLectura(uuid: string): Promise<LecturaSensor> {
  const response = await axios.get<LecturaSensor>(
    `${API_BASE_URL}/sensor/${uuid}/ultima`
  );
  return response.data;
}

/**
 * Obtiene un historial paginado de lecturas para un sensor. (GET /sensor/{uuid}?pagina=...)
 */
export async function getHistorialLecturas(
  uuid: string,
  pagina: number = 0,
  tamano: number = 100
): Promise<LecturaSensor[]> {
  // Nota: El backend retorna un objeto Page<LecturaSensor>. Aquí simplificamos retornando el 'content'.
  const response = await axios.get<{ content: LecturaSensor[] }>(
    `${API_BASE_URL}/sensor/${uuid}?pagina=${pagina}&tamano=${tamano}`
  );
  return response.data.content;
}

/**
 * Obtiene lecturas dentro de un rango de fechas. (GET /sensor/{uuid}/rango?inicio=...&fin=...)
 */
export async function getLecturasPorRango(
  uuid: string,
  fechaInicio: string, // Formato YYYY-MM-DD
  fechaFin: string
): Promise<LecturaSensor[]> {
  const response = await axios.get<LecturaSensor[]>(
    `${API_BASE_URL}/sensor/${uuid}/rango?inicio=${fechaInicio}&fin=${fechaFin}`
  );
  return response.data;
}
