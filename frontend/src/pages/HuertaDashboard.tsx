import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { Huerta } from "../types/Huerta";
import "../css/HuertaDashboard.css";

const HuertaDashboard = () => {
  const navigate = useNavigate();

  const huerta: Huerta | null = JSON.parse(
    sessionStorage.getItem("huertaActiva") || "null"
  );

  useEffect(() => {
    if (!huerta) {
      navigate("/dashboard");
    }
  }, [huerta, navigate]);

  const logout = () => {
    sessionStorage.clear();
    navigate("/login");
  };

  if (!huerta) return null;

  return (
    <div className="huertaDashboard-container">
      {/* HEADER */}
      <div className="huertaDashboard-header">
        <h1>{huerta.nombre}</h1>

        <div className="huertaDashboard-actions">
          <button
            className="back-btn"
            onClick={() => navigate("/dashboard")}
          >
            ← Volver
          </button>

          <button
            className="logout-btn"
            onClick={logout}
          >
            Cerrar sesión
          </button>
        </div>
      </div>

      {/* CARD INFO */}
      <div className="huertaDashboard-card">
        {Object.entries(huerta).map(([key, value]) => {
          if (!value) return null;

          // 🔤 Formatear nombre del campo
          const label = key
            .replace(/([A-Z])/g, " $1")
            .replace(/^./, (l) => l.toUpperCase());

          return (
            <p key={key}>
              <strong>{label}:</strong> {String(value)}
            </p>
          );
        })}
      </div>
    </div>
  );
};

export default HuertaDashboard;
