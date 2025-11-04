// frontend/src/api/alertService.ts

import axios from "axios";
import type { Alerta } from "../types/models";

const API_BASE_URL = "http://localhost:8080/api/alertas";

// Interfaz para la respuesta paginada de Spring Boot (Page<T>)
// Esto es necesario para manejar la estructura que devuelve el backend.
interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  // ... otras propiedades de paginación
}

/**
 * Obtiene una lista paginada de todas las alertas. (GET /api/alertas)
 * @param pagina El número de página (0-indexed).
 * @param tamano El número de elementos por página.
 * @returns Un objeto Page con el contenido de las alertas.
 */
export async function getAllAlertas(
  pagina: number = 0,
  tamano: number = 20
): Promise<Page<Alerta>> {
  try {
    const response = await axios.get<Page<Alerta>>(
      `${API_BASE_URL}?pagina=${pagina}&tamano=${tamano}`
    );
    return response.data;
  } catch (error) {
    throw new Error("No se pudo cargar el historial de alertas.");
  }
}

/**
 * Obtiene una alerta específica por su ID. (GET /api/alertas/{id})
 */
export async function getAlertaById(id: number): Promise<Alerta> {
  try {
    const response = await axios.get<Alerta>(`${API_BASE_URL}/${id}`);
    return response.data;
  } catch (error) {
    // Asumiendo que el backend retorna 404 si no encuentra el ID
    throw new Error(`Alerta con ID ${id} no encontrada.`);
  }
}

/**
 * Marca una alerta como leída. (PUT /api/alertas/{id}/leida)
 */
export async function marcarAlertaComoLeida(id: number): Promise<void> {
  try {
    // Se espera un 204 No Content
    await axios.put(`${API_BASE_URL}/${id}/leida`);
  } catch (error) {
    throw new Error(`Fallo al marcar la alerta ${id} como leída.`);
  }
}
