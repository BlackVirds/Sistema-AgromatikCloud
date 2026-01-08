import { useNavigate } from "react-router-dom";
import type { Huerta } from "../types/Huerta";

const Dashboard = () => {
  const navigate = useNavigate();

  const huertas: Huerta[] = JSON.parse(
    sessionStorage.getItem("huertas") || "[]"
  );

  return (
    <div>
      <h1>Dashboard</h1>

      {huertas.length === 0 && (
        <p>No tienes huertas registradas</p>
      )}

      {huertas.map((h, index) => (
        <div key={index} style={{ border: "1px solid #ccc", padding: "1rem" }}>
          <h3>{h.nombre}</h3>

          <button
            onClick={() => {
              sessionStorage.setItem(
                "huertaActiva",
                JSON.stringify(h)
              );
              navigate("/huerta-dashboard");
            }}
          >
            Ver huerta
          </button>
        </div>
      ))}
    </div>
  );
};

export default Dashboard;
