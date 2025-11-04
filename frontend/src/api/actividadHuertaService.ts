import axios from "axios";
import type {
  ActividadHuerta,
  ActividadCreateUpdateData,
} from "../types/models";

const API_BASE_URL = "http://localhost:8080/api/actividades-huerta";

/**
 * Crea una nueva actividad (POST).
 */
export async function createActividad(
  data: ActividadCreateUpdateData
): Promise<ActividadHuerta> {
  try {
    const response = await axios.post<ActividadHuerta>(API_BASE_URL, data);
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.data) {
      throw new Error(
        error.response.data.message || "Error al crear la actividad."
      );
    }
    throw new Error("Fallo de conexión al backend.");
  }
}

/**
 * Obtiene todas las actividades de una huerta específica. (GET /huerta/{huertaId})
 */
export async function getActividadesByHuertaId(
  huertaId: number
): Promise<ActividadHuerta[]> {
  const response = await axios.get<ActividadHuerta[]>(
    `${API_BASE_URL}/huerta/${huertaId}`
  );
  return response.data;
}

/**
 * Obtiene todas las actividades relacionadas con un cultivo específico. (GET /cultivo/{cultivoId})
 */
export async function getActividadesByCultivoId(
  cultivoId: number
): Promise<ActividadHuerta[]> {
  const response = await axios.get<ActividadHuerta[]>(
    `${API_BASE_URL}/cultivo/${cultivoId}`
  );
  return response.data;
}

/**
 * Actualiza una actividad por su ID numérico. (PUT)
 */
export async function updateActividad(
  id: number,
  data: ActividadCreateUpdateData
): Promise<ActividadHuerta> {
  const response = await axios.put<ActividadHuerta>(
    `${API_BASE_URL}/${id}`,
    data
  );
  return response.data;
}

/**
 * Finaliza/Completa una actividad (Soft Delete/Finalización Lógica). (DELETE)
 */
export async function completeActividad(id: number): Promise<void> {
  await axios.delete(`${API_BASE_URL}/${id}`);
}
