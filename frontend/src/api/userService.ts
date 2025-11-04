// frontend/src/api/userService.ts

import axios from "axios";
import type { Usuario, UsuarioCreateData } from "../types/models";
// Asegúrate de que las interfaces Usuario y UsuarioCreateData estén definidas en types/models.ts

const API_BASE_URL = "http://localhost:8080/api/usuarios";

/**
 * Registra un nuevo usuario en el sistema. (POST)
 * @param data Los datos requeridos para la creación del usuario (incluyendo passwordHash).
 * @returns El objeto Usuario creado devuelto por el backend.
 */
export async function createUsuario(data: UsuarioCreateData): Promise<Usuario> {
  try {
    const response = await axios.post<Usuario>(API_BASE_URL, data);
    return response.data;
  } catch (error) {
    // Manejar errores de red o errores 400 (Bad Request) de validación de Spring Boot
    if (axios.isAxiosError(error) && error.response?.data) {
      // Spring Boot a menudo devuelve el mensaje de validación aquí
      const errorData = error.response.data;
      let errorMessage = "Fallo de validación: ";

      // Intenta extraer el mensaje de error de la respuesta del backend
      if (errorData.message) {
        errorMessage = errorData.message;
      } else if (errorData.errors && errorData.errors.length > 0) {
        // Para errores de @Valid (campo por campo)
        errorMessage = errorData.errors
          .map((e: any) => e.defaultMessage)
          .join("; ");
      }
      throw new Error(errorMessage || "Error desconocido del servidor.");
    }
    throw new Error("Fallo de conexión al backend.");
  }
}

/**
 * Obtiene la lista de todos los usuarios paginada o completa. (GET)
 * @returns Lista de objetos Usuario.
 */
export async function getAllUsuarios(): Promise<Usuario[]> {
  try {
    const response = await axios.get<Usuario[]>(API_BASE_URL);
    return response.data;
  } catch (error) {
    // Manejo de error de red
    console.error("Error al obtener usuarios:", error);
    throw new Error("No se pudo cargar la lista de usuarios.");
  }
}

/**
 * (Futuro) Aquí iría la función para iniciar sesión, usando un endpoint /login
 *
 * export async function loginUsuario(credentials: LoginData): Promise<AuthToken> {
 * // ... Lógica de POST a /api/login y manejo de tokens
 * }
 */

/**
 * (Futuro) Función para obtener un usuario por su UUID.
 * * export async function getUsuarioByUuid(uuid: string): Promise<Usuario> {
 * const response = await axios.get<Usuario>(`${API_BASE_URL}/${uuid}`);
 * return response.data;
 * }
 */
