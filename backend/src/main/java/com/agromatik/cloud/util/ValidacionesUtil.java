package com.agromatik.cloud.util;

public class ValidacionesUtil {

    private ValidacionesUtil() {
        // Evita instanciación
    }

    public static void validarUbicacion(String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            throw new IllegalArgumentException("La ubicación geográfica no puede estar vacía.");
        }

        // Debe tener formato "lat,lon"
        String[] partes = ubicacion.split(",");
        if (partes.length != 2) {
            throw new IllegalArgumentException("La ubicación geográfica debe tener el formato 'latitud,longitud'.");
        }

        try {
            double lat = Double.parseDouble(partes[0].trim());
            double lon = Double.parseDouble(partes[1].trim());

            if (lat < -90 || lat > 90) {
                throw new IllegalArgumentException("La latitud debe estar entre -90 y 90.");
            }
            if (lon < -180 || lon > 180) {
                throw new IllegalArgumentException("La longitud debe estar entre -180 y 180.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La ubicación geográfica contiene valores no numéricos.");
        }
    }
}
