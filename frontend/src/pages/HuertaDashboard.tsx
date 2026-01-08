import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { Huerta } from "../types/Huerta";

const HuertaDashboard = () => {
  const navigate = useNavigate();

  const huerta: Huerta | null = JSON.parse(
    sessionStorage.getItem("huertaActiva") || "null"
  );

  // ✅ Redirección segura
  useEffect(() => {
    if (!huerta) {
      navigate("/dashboard");
    }
  }, [huerta, navigate]);

  const logout = () => {
    localStorage.clear();
    sessionStorage.clear();
    navigate("/login");
  };

  if (!huerta) return null;

  return (
    <div>
      <div style={{ display: "flex", gap: "1rem" }}>
        <button onClick={() => navigate("/dashboard")}>
          ← Volver al Dashboard
        </button>

        <button onClick={logout} style={{ background: "#f87171", color: "white" }}>
          Cerrar sesión
        </button>
      </div>

      <h2>{huerta.nombre}</h2>

      {huerta.tamañoHectareas && <p>Tamaño: {huerta.tamañoHectareas} ha</p>}
      {huerta.direccion && <p>Dirección: {huerta.direccion}</p>}
      {huerta.municipio && <p>Municipio: {huerta.municipio}</p>}
      {huerta.estado && <p>Estado: {huerta.estado}</p>}
      {huerta.pais && <p>País: {huerta.pais}</p>}
    </div>
  );
};

export default HuertaDashboard;

