// frontend/src/pages/AlertsDashboard.tsx

import React, { useEffect, useState } from "react";
import type { Alerta, SeveridadAlerta } from "../types/models";
import { getAllAlertas, marcarAlertaComoLeida } from "../api/alertaService";

const AlertsDashboard: React.FC = () => {
  // Estado para guardar la lista de alertas
  const [alertas, setAlertas] = useState<Alerta[]>([]);
  // Estado para manejar si la información está cargando
  const [loading, setLoading] = useState(true);
  // Estado para manejar cualquier error de la API
  const [error, setError] = useState<string | null>(null);

  // 1. FUNCIÓN PARA CARGAR DATOS DE LA API
  const fetchAlerts = async (page: number = 0) => {
    setLoading(true);
    setError(null);
    try {
      // Usamos el servicio API para obtener la primera página de alertas
      const data = await getAllAlertas(page, 20); // Obtiene la página 0 con 20 elementos
      setAlertas(data.content); // Solo guardamos el contenido de la página
    } catch (err) {
      console.error("Fallo al cargar alertas:", err);
      setError("No se pudo cargar la información del motor de alertas.");
    } finally {
      setLoading(false);
    }
  };

  // 2. FUNCIÓN PARA MARCAR ALERTA COMO LEÍDA
  const handleMarkAsRead = async (id: number) => {
    try {
      await marcarAlertaComoLeida(id);
      // Actualizar el estado localmente después del éxito
      setAlertas((prevAlerts) =>
        prevAlerts.map((a) => (a.id === id ? { ...a, leida: true } : a))
      );
    } catch (err) {
      setError("Error al marcar la alerta. Inténtalo de nuevo.");
    }
  };

  // 3. EFECTO: Se ejecuta una sola vez al cargar el componente
  useEffect(() => {
    fetchAlerts();
    // Opcional: Recargar cada 30 segundos para ver nuevas alertas
    const interval = setInterval(fetchAlerts, 30000);
    return () => clearInterval(interval); // Limpiar el intervalo al desmontar el componente
  }, []);

  // --- RENDERING CONDICIONAL ---
  if (loading)
    return (
      <div style={{ textAlign: "center", marginTop: "50px" }}>
        Cargando alertas...
      </div>
    );
  if (error)
    return (
      <div style={{ color: "red", textAlign: "center", marginTop: "50px" }}>
        Error: {error}
      </div>
    );

  return (
    <div style={{ padding: "20px", maxWidth: "900px", margin: "0 auto" }}>
      <h1>
        Tablero de Alertas Activas ({alertas.filter((a) => !a.leida).length})
      </h1>
      <button onClick={() => fetchAlerts()}>Recargar Ahora</button>

      {alertas.length === 0 && (
        <p style={{ marginTop: "20px" }}>No hay alertas registradas.</p>
      )}

      {alertas.map((alerta) => (
        <div
          key={alerta.id}
          style={{
            border: "1px solid #ddd",
            margin: "10px 0",
            padding: "15px",
            borderRadius: "5px",
            backgroundColor:
              alerta.severidad === "CRITICA" && !alerta.leida
                ? "#ffe0e0"
                : alerta.leida
                ? "#f0f0f0"
                : "white",
            opacity: alerta.leida ? 0.6 : 1,
          }}
        >
          <h3>
            {alerta.titulo} ({alerta.severidad})
          </h3>
          <p>{alerta.descripcion}</p>
          <p>
            Huerta: {alerta.huerta.nombre} | Valor:{" "}
            {alerta.valorActual.toFixed(2)}
          </p>

          {!alerta.leida && (
            <button
              onClick={() => handleMarkAsRead(alerta.id)}
              style={{
                padding: "5px 10px",
                backgroundColor: "#3f51b5",
                color: "white",
                border: "none",
                cursor: "pointer",
                marginTop: "10px",
              }}
            >
              Marcar como Leída
            </button>
          )}
        </div>
      ))}
    </div>
  );
};

export default AlertsDashboard;
