// frontend/src/api/cultivoService.ts

import axios from "axios";
import type { Cultivo, CultivoCreateUpdateData } from "../types/models";

const API_BASE_URL = "http://localhost:8080/api/cultivos";

/**
 * Registra un nuevo cultivo. (POST)
 */
export async function createCultivo(
  data: CultivoCreateUpdateData
): Promise<Cultivo> {
  try {
    const response = await axios.post<Cultivo>(API_BASE_URL, data);
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.data) {
      // Captura errores de validación (@NotNull, FK no existe)
      throw new Error(
        error.response.data.message || "Error al crear el cultivo."
      );
    }
    throw new Error("Fallo de conexión al backend.");
  }
}

/**
 * Obtiene todos los cultivos de una huerta específica. (GET /huerta/{huertaId})
 */
export async function getCultivosByHuertaId(
  huertaId: number
): Promise<Cultivo[]> {
  const response = await axios.get<Cultivo[]>(
    `${API_BASE_URL}/huerta/${huertaId}`
  );
  return response.data;
}

/**
 * Actualiza un cultivo por su UUID. (PUT)
 */
export async function updateCultivo(
  uuid: string,
  data: CultivoCreateUpdateData
): Promise<Cultivo> {
  const response = await axios.put<Cultivo>(`${API_BASE_URL}/${uuid}`, data);
  return response.data;
}

/**
 * Marca un cultivo como CANCELADO (Soft Delete). (DELETE)
 */
export async function deleteCultivo(uuid: string): Promise<void> {
  // El backend lo marca como CANCELADO (Soft Delete)
  await axios.delete(`${API_BASE_URL}/${uuid}`);
}
