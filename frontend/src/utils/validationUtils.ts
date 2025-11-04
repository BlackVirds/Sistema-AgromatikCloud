// frontend/src/utils/validationUtils.ts

/**
 * Valida que la cadena de ubicación cumpla con el formato "latitud,longitud" y rangos geográficos.
 * @param value La cadena de ubicación.
 * @returns true si es válido o si está vacío, o un mensaje de error si es inválido.
 */
export const validateUbicacion = (value: string | undefined): true | string => {
  // 1. ✅ CLAVE: Si el valor es nulo, undefined, o una cadena vacía, retornamos TRUE (es válido porque es opcional).
  if (!value || value.trim() === "") {
    return true;
  }

  // 2. Si hay datos, validamos el formato (replicando la lógica de ValidacionesUtil.java).
  const partes = value.split(",");
  if (partes.length !== 2) {
    return "Debe tener el formato 'latitud,longitud'.";
  }

  const latStr = partes[0].trim();
  const lonStr = partes[1].trim();

  const lat = Number(latStr);
  const lon = Number(lonStr);

  if (isNaN(lat) || isNaN(lon)) {
    return "Ambos valores deben ser numéricos.";
  }

  if (lat < -90 || lat > 90) {
    return "La latitud debe estar entre -90 y 90.";
  }
  if (lon < -180 || lon > 180) {
    return "La longitud debe estar entre -180 y 180.";
  }

  return true; // Formato válido
};
