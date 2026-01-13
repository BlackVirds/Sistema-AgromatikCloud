import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./components/Login";
import Register from "./pages/Register";
import RegisterHuerta from "./pages/RegisterHuerta";
import HuertaDashboard from "./pages/HuertaDashboard";
import Dashboard from "./pages/Dashboard";

const App = () => {
  const isAuth = localStorage.getItem("auth") === "true";
  const huertas = JSON.parse(sessionStorage.getItem("huertas") || "[]");

  return (
    <Routes>
      {/* LOGIN (no redirige) */}
      <Route path="/login" element={<Login />} />

      {/* REGISTRO USUARIO */}
      <Route path="/register" element={<Register />} />

      {/* DASHBOARD */}
      <Route
        path="/dashboard"
        element={
          isAuth ? (
            <Dashboard />
          ) : (
            <Navigate to="/login" />
          )
        }
      />


      {/* REGISTRO HUERTA */}
      <Route
        path="/register-huerta"
        element={isAuth ? <RegisterHuerta /> : <Navigate to="/login" />}
      />

      {/* HUERTA DASHBOARD */}
      <Route
        path="/huerta-dashboard"
        element={isAuth ? <HuertaDashboard /> : <Navigate to="/login" />}
      />

      {/* CUALQUIER OTRA RUTA */}
      <Route path="*" element={<Navigate to="/login" />} />
    </Routes>
  );
};

export default App;
