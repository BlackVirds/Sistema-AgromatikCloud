import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/Login.css";

const Login = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!email || !password) {
      alert("Todos los campos son obligatorios");
      return;
    }

    if (email === "admin@test.com" && password === "123456") {
      localStorage.setItem("auth", "true");
      navigate("/dashboard"); // flujo central
    } else {
      alert("Credenciales incorrectas");
    }
  };

  return (
    <div className="container-Login">
      <form onSubmit={handleSubmit} className="form-Login">
        <h2>Iniciar Sesión</h2>

        <input
          className="input-Login"
          type="email"
          placeholder="Correo"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        <input
          className="input-Login"
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button type="submit" className="button-Login">
          Entrar
        </button>

        <div className="register-link">
          <span>¿No tienes cuenta?</span>
          <button
            type="button"
            className="register-button"
            onClick={() => navigate("/register")}
          >
            Regístrate
          </button>
        </div>
      </form>
    </div>
  );
};

export default Login;
