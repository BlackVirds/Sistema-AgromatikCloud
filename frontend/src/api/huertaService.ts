// frontend/src/api/huertaService.ts

import axios from "axios";
import type {
  Huerta,
  HuertaCreateUpdateData,
  UsuarioReferencia,
} from "../types/models";

const API_BASE_URL = "http://localhost:8080/api/huertas";

/**
 * Registra una nueva huerta. (POST)
 * @param data Los datos de la huerta, incluyendo la ubicación como String (lat,lng).
 * @returns El objeto Huerta creado.
 */
export async function createHuerta(
  data: HuertaCreateUpdateData
): Promise<Huerta> {
  try {
    const response = await axios.post<Huerta>(API_BASE_URL, data);
    return response.data;
  } catch (error) {
    // Manejar errores de red o errores 400 (Bad Request) de validación de Spring Boot
    if (axios.isAxiosError(error) && error.response?.data) {
      // Captura errores de validación (ej. el UUID del usuario no existe, o nombre en blanco)
      const errorData = error.response.data;
      let errorMessage =
        errorData.message || "Error desconocido al crear huerta.";

      // Si el backend envió errores de validación de campo
      if (errorData.errors && errorData.errors.length > 0) {
        errorMessage = errorData.errors
          .map((e: any) => e.defaultMessage || e.message)
          .join("; ");
      }
      throw new Error(errorMessage);
    }
    throw new Error("Fallo de conexión al backend.");
  }
}

/**
 * Obtiene la lista completa de huertas. (GET)
 * @returns Lista de objetos Huerta.
 */
export async function getAllHuertas(): Promise<Huerta[]> {
  try {
    const response = await axios.get<Huerta[]>(API_BASE_URL);
    return response.data;
  } catch (error) {
    console.error("Error al obtener huertas:", error);
    throw new Error("No se pudo cargar la lista de huertas.");
  }
}

/**
 * (Futuro) Obtiene las huertas de un usuario específico. (GET /usuarios/{usuarioId})
 * @param usuarioId El ID numérico del usuario.
 * @returns Lista de objetos Huerta.
 */
export async function getHuertasByUsuarioId(
  usuarioId: number
): Promise<Huerta[]> {
  try {
    const response = await axios.get<Huerta[]>(
      `${API_BASE_URL}/usuarios/${usuarioId}`
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error al obtener huertas para el usuario ${usuarioId}:`,
      error
    );
    throw new Error("No se pudo cargar la lista de huertas del usuario.");
  }
}
