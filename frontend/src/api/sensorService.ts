// frontend/src/api/sensorService.ts

import axios from "axios";
import type { Sensor, SensorCreateUpdateData } from "../types/models";

const API_BASE_URL = "http://localhost:8080/api/sensores";

/**
 * Registra un nuevo sensor en una huerta. (POST)
 */
export async function createSensor(
  data: SensorCreateUpdateData
): Promise<Sensor> {
  try {
    const response = await axios.post<Sensor>(API_BASE_URL, data);
    return response.data;
  } catch (error) {
    // Captura errores de validación (@NotNull, FK no existe, o ubicación inválida)
    if (axios.isAxiosError(error) && error.response?.data) {
      throw new Error(
        error.response.data.message || "Error al crear el sensor."
      );
    }
    throw new Error("Fallo de conexión al backend.");
  }
}

/**
 * Obtiene todos los sensores de una huerta específica. (GET /huerta/{huertaId})
 */
export async function getSensoresByHuertaId(
  huertaId: number
): Promise<Sensor[]> {
  const response = await axios.get<Sensor[]>(
    `${API_BASE_URL}/huerta/${huertaId}`
  );
  return response.data;
}

/**
 * Actualiza un sensor por su UUID. (PUT)
 */
export async function updateSensor(
  uuid: string,
  data: SensorCreateUpdateData
): Promise<Sensor> {
  const response = await axios.put<Sensor>(`${API_BASE_URL}/${uuid}`, data);
  return response.data;
}

/**
 * Desactiva un sensor (Soft Delete). (DELETE)
 */
export async function deleteSensor(uuid: string): Promise<void> {
  // El backend lo marca como INACTIVO (Soft Delete)
  await axios.delete(`${API_BASE_URL}/${uuid}`);
}
